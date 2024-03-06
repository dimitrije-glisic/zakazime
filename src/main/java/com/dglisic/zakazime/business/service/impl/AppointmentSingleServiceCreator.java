package com.dglisic.zakazime.business.service.impl;

import static com.dglisic.zakazime.business.domain.AppointmentStatus.SCHEDULED;

import com.dglisic.zakazime.business.controller.dto.SingleServiceAppointmentRequest;
import com.dglisic.zakazime.business.domain.SingleServiceAppointmentData;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Customer;
import org.springframework.stereotype.Service;

@Service
public class AppointmentSingleServiceCreator extends AppointmentCreator<SingleServiceAppointmentRequest> {


  public AppointmentSingleServiceCreator(BusinessValidator businessValidator, EmployeeValidator employeeValidator,
                                         CustomerService customerService, TimeSlotManagement timeSlotManagement,
                                         AppointmentRepository appointmentRepository,
                                         OutboxMessageRepository outboxMessageRepository) {
    super(businessValidator, employeeValidator, customerService, timeSlotManagement, appointmentRepository,
        outboxMessageRepository);
  }

  @Override
  public void createAppointment(SingleServiceAppointmentRequest request) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    final SingleServiceAppointmentData appointmentData = validateAndPrepareAppointmentData(request);
    final Customer customer =
        customerService.handleCustomerDataOnAppointmentCreation(request.businessId(), request.customerData());
    appointmentData.setCustomer(customer);
    final Appointment appointment = storeAppointment(request, customer);
    appointmentData.setAppointment(appointment);
    createMessage(appointmentData);
  }

  private SingleServiceAppointmentData validateAndPrepareAppointmentData(SingleServiceAppointmentRequest request) {
    final var business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    final var employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
    final var service =
        businessValidator.requireServiceBelongsToBusinessAndReturn(request.serviceId(), request.businessId());
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
    // require employee is available at requested time
    timeSlotManagement.validateAvailabilityNew(request.businessId(), request.employeeId(), request.startTime(), request.duration());

    final SingleServiceAppointmentData appointmentData = new SingleServiceAppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setService(service);
    appointmentData.setEmployee(employee);
    return appointmentData;
  }

  private Appointment storeAppointment(SingleServiceAppointmentRequest request, Customer customer) {
    final Appointment appointment = new Appointment();
    appointment.setCustomerId(customer.getId());
    appointment.setBusinessId(request.businessId());
    appointment.setServiceId(request.serviceId());
    appointment.setEmployeeId(request.employeeId());
    final var startTime = request.startTime().withSecond(0).withNano(0);
    appointment.setStartTime(startTime);
    final var endTime = startTime.plusMinutes(request.duration());
    appointment.setEndTime(endTime);
    appointment.setStatus(SCHEDULED.toString());
    return appointmentRepository.save(appointment);
  }

  private void createMessage(SingleServiceAppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin zakazan";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin zakazan za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() +
        " za uslugu " + appointmentData.getService().getTitle() + " kod " + appointmentData.getEmployee().getName();
    createOutboxMessage(recipient, subject, message);
  }

}
