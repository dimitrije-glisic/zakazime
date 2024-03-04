package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.domain.AppointmentData;
import com.dglisic.zakazime.business.domain.AppointmentStatus;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.business.service.CustomerService;
import java.time.LocalDate;
import java.util.List;
import jooq.tables.EmployeeBlockTime;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.OutboxMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
  private final AppointmentRepository appointmentRepository;
  private final BusinessValidator businessValidator;
  private final EmployeeValidator employeeValidator;
  private final CustomerService customerService;
  private final OutboxMessageRepository outboxMessageRepository;
  private final TimeSlotManagement timeSlotManagement;

  @Override
  @Transactional
  public void createAppointment(CreateAppointmentRequest request) {
    final AppointmentData appointmentData = validateAppointment(request);
    final Customer customer =
        customerService.handleCustomerDataOnAppointmentCreation(request.businessId(), request.customerData());
    appointmentData.setCustomer(customer);
    final Appointment appointment = storeAppointment(request, customer);
    appointmentData.setAppointment(appointment);
    createOutboxMessageAppointmentScheduled(appointmentData);
  }

  @Override
  public EmployeeBlockTime createBlockTime(CreateBlockTimeRequest request) {
    return null;
  }

  @Override
  public List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date) {
    businessValidator.requireBusinessExists(businessId);
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
    return appointmentRepository.getAppointmentsForDate(businessId, employeeId, date);
  }


  private AppointmentData validateAppointment(CreateAppointmentRequest request) {
    final var business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    final var service =
        businessValidator.requireServiceBelongsToBusinessAndReturn(request.serviceId(), request.businessId());
    final var employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
    // require employee is available at requested time

    //request.startTime() minutes should be a multiple of 15 minutes
    timeSlotManagement.validateTimeSlot(request);

    final AppointmentData appointmentData = new AppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setService(service);
    appointmentData.setEmployee(employee);
    return appointmentData;
  }


  private Appointment storeAppointment(CreateAppointmentRequest request, Customer customer) {
    final Appointment appointment = new Appointment();
    appointment.setCustomerId(customer.getId());
    appointment.setBusinessId(request.businessId());
    appointment.setServiceId(request.serviceId());
    appointment.setEmployeeId(request.employeeId());
    final var startTime = request.startTime().withSecond(0).withNano(0);
    appointment.setStartTime(startTime);
    final var endTime = startTime.plusMinutes(request.duration());
    appointment.setEndTime(endTime);
    appointment.setStatus(AppointmentStatus.SCHEDULED.toString());
    return appointmentRepository.save(appointment);
  }

  private void createOutboxMessageAppointmentScheduled(AppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin zakazan";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin zakazan za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() + "" +
        " za uslugu " + appointmentData.getService().getTitle() + " kod " + appointmentData.getEmployee().getName();
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessage(String recipient, String subject, String body) {
    final OutboxMessage outboxMessage = new OutboxMessage();
    outboxMessage.setRecipient(recipient);
    outboxMessage.setSubject(subject);
    outboxMessage.setBody(body);
    outboxMessage.setStatus(OutboxMessageStatus.PENDING.toString());
    outboxMessageRepository.save(outboxMessage);
  }
}
