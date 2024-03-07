package com.dglisic.zakazime.business.service.impl;

import static com.dglisic.zakazime.business.domain.AppointmentStatus.SCHEDULED;

import com.dglisic.zakazime.business.controller.dto.SingleServiceAppointmentRequest;
import com.dglisic.zakazime.business.domain.SingleServiceAppointmentData;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
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
    final Appointment appointment = storeAppointment(appointmentData);
    appointmentData.setAppointment(appointment);
    createMessage(appointmentData);
  }

  private SingleServiceAppointmentData validateAndPrepareAppointmentData(SingleServiceAppointmentRequest request) {
    final var business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    final var service =
        businessValidator.requireServiceBelongsToBusinessAndReturn(request.serviceId(), request.businessId());

    Employee employee;
    if (request.employeeId() == null) {
      // require at least one employee is available at requested time
      employee = findFirstAvailableEmployeeAndStartTime(request.businessId(), request.startTime(), service.getAvgDuration());
    } else {
      employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
      EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
      // require employee is available at requested time
      timeSlotManagement.validateAvailabilityNew(request.businessId(), request.employeeId(), request.startTime(),
          service.getAvgDuration());
    }

    final SingleServiceAppointmentData appointmentData = new SingleServiceAppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setService(service);
    appointmentData.setEmployee(employee);
    appointmentData.setStartTime(request.startTime());
    return appointmentData;
  }

  private Employee findFirstAvailableEmployeeAndStartTime(Integer businessId, LocalDateTime startTime, Integer duration) {
    final var tolerance = 0;
    final List<Pair<Employee, LocalDateTime>> availableEmployees = timeSlotManagement.findAvailableEmployees(
        businessId, startTime, tolerance, duration);

    if (availableEmployees.isEmpty()) {
      throw new ApplicationException("No available employees for the given time", HttpStatus.BAD_REQUEST);
    }

    // Return the first available employee
    return availableEmployees.get(0).getLeft();
  }

  private Appointment storeAppointment(SingleServiceAppointmentData appointmentData) {
    final Appointment appointment = new Appointment();
    appointment.setCustomerId(appointmentData.getCustomer().getId());
    appointment.setBusinessId(appointmentData.getBusiness().getId());
    appointment.setServiceId(appointmentData.getService().getId());
    appointment.setEmployeeId(appointmentData.getEmployee().getId());
    final var startTime = appointmentData.getStartTime();
    appointment.setStartTime(startTime);
    final var endTime = startTime.plusMinutes(appointmentData.getService().getAvgDuration());
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
