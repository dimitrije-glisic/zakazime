package com.dglisic.zakazime.business.service.impl;

import static com.dglisic.zakazime.business.domain.AppointmentStatus.SCHEDULED;

import com.dglisic.zakazime.business.controller.dto.EmployeeServiceIdPair;
import com.dglisic.zakazime.business.controller.dto.MultiServiceAppointmentRequest;
import com.dglisic.zakazime.business.domain.MultiServiceAppointmentData;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentMultiServiceCreator extends AppointmentCreator<MultiServiceAppointmentRequest> {


  public AppointmentMultiServiceCreator(BusinessValidator businessValidator, EmployeeValidator employeeValidator,
                                        CustomerService customerService, TimeSlotManagement timeSlotManagement,
                                        AppointmentRepository appointmentRepository,
                                        OutboxMessageRepository outboxMessageRepository) {
    super(businessValidator, employeeValidator, customerService, timeSlotManagement, appointmentRepository,
        outboxMessageRepository);
  }

  @Override
  @Transactional
  public void createAppointment(MultiServiceAppointmentRequest request) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    final MultiServiceAppointmentData appointmentData = validateAndPrepareAppointmentData(request);
    final Customer customer =
        customerService.handleCustomerDataOnAppointmentCreation(request.businessId(), request.customerData());
    appointmentData.setCustomer(customer);
    final List<Appointment> appointments =
        scheduleAppointments(appointmentData.getServiceEmployeeMap(), request.startTime(), customer);
    appointmentData.setAppointments(appointments);
    sendAppointmentConfirmation(appointmentData);
  }

  private MultiServiceAppointmentData validateAndPrepareAppointmentData(MultiServiceAppointmentRequest request) {
    final var business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    final List<EmployeeServiceIdPair> employeeServicePairs = request.employeeServicePairs();

    final Map<Integer, Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeeMap = new HashMap<>();
    // start time is the time of the first service, it needs to be updated for each next service-employee pair
    var appointmentStartTime = request.startTime();
    for (EmployeeServiceIdPair employeeServicePair : employeeServicePairs) {
      final var employee = employeeValidator.requireEmployeeExistsAndReturn(employeeServicePair.employeeId());
      EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
      final var service =
          businessValidator.requireServiceBelongsToBusinessAndReturn(employeeServicePair.serviceId(), request.businessId());
      timeSlotManagement.validateAvailabilityNew(business.getId(), employee.getId(), appointmentStartTime,
          service.getAvgDuration());
      appointmentStartTime = appointmentStartTime.plusMinutes(service.getAvgDuration());
      serviceEmployeeMap.put(employeeServicePair.serviceId(), Pair.of(service, employee));
    }

    final MultiServiceAppointmentData appointmentData = new MultiServiceAppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setServiceEmployeeMap(serviceEmployeeMap);
    return appointmentData;
  }

  private List<Appointment> scheduleAppointments(Map<Integer, Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeeMap,
                                                 LocalDateTime startTime, Customer customer) {
    List<Appointment> appointments = new ArrayList<>();
    LocalDateTime nextStartTime = startTime.withSecond(0).withNano(0);

    for (Map.Entry<Integer, Pair<jooq.tables.pojos.Service, Employee>> entry : serviceEmployeeMap.entrySet()) {
      Appointment appointment = new Appointment();
      appointment.setCustomerId(customer.getId());
      appointment.setBusinessId(entry.getValue().getRight().getBusinessId());
      appointment.setServiceId(entry.getValue().getLeft().getId());
      appointment.setEmployeeId(entry.getValue().getRight().getId());
      appointment.setStartTime(nextStartTime);
      appointment.setEndTime(nextStartTime.plusMinutes(entry.getValue().getLeft().getAvgDuration()));
      appointment.setStatus(SCHEDULED.toString());
      appointments.add(appointmentRepository.save(appointment));

      nextStartTime = appointment.getEndTime();
    }
    return appointments;
  }

  private void sendAppointmentConfirmation(MultiServiceAppointmentData data) {
    String recipient = data.getCustomer().getEmail();
    String subject = "Appointment Scheduled";
    StringBuilder message = new StringBuilder("Your appointment is scheduled on ")
        .append(data.getAppointments().get(0).getStartTime().toString())
        .append(" at ")
        .append(data.getBusiness().getName())
        .append(". Services: ");

    data.getAppointments().forEach(appointment -> {
      var service = data.getServiceEmployeeMap().get(appointment.getServiceId()).getLeft();
      var employee = data.getServiceEmployeeMap().get(appointment.getServiceId()).getRight();
      message.append(service.getTitle()).append(" with ").append(employee.getName()).append(", ");
    });

    // Remove the last comma and space
    message.setLength(message.length() - 2);

    createOutboxMessage(recipient, subject, message.toString());
  }

}
