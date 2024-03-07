package com.dglisic.zakazime.business.service.impl;

import static com.dglisic.zakazime.business.domain.AppointmentStatus.SCHEDULED;

import com.dglisic.zakazime.business.controller.dto.EmployeeServiceIdPair;
import com.dglisic.zakazime.business.controller.dto.MultiServiceAppointmentRequest;
import com.dglisic.zakazime.business.domain.MultiServiceAppointmentData;
import com.dglisic.zakazime.business.domain.ServiceEmployeeStartTime;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentMultiServiceCreator extends AppointmentCreator<MultiServiceAppointmentRequest> {

  public static final Integer APPOINTMENT_TOLERANCE = 15;


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
    final MultiServiceAppointmentData appointmentData = validateAndPrepareAppointmentDataReadable(request);
    final Customer customer =
        customerService.handleCustomerDataOnAppointmentCreation(request.businessId(), request.customerData());
    appointmentData.setCustomer(customer);
    final List<Appointment> appointments =
        scheduleAppointments(appointmentData.getServiceEmployeeStartTimeMap(), customer);
    appointmentData.setAppointments(appointments);
    sendAppointmentConfirmation(appointmentData);
  }

  //================================================================================================

  protected MultiServiceAppointmentData validateAndPrepareAppointmentDataReadable(MultiServiceAppointmentRequest request) {
    Business business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    var appointmentStartTime = request.startTime();
    Map<Integer, ServiceEmployeeStartTime> serviceEmployeeMap = new LinkedHashMap<>();

    for (int i = 0; i < request.employeeServicePairs().size(); i++) {
      EmployeeServiceIdPair pair = request.employeeServicePairs().get(i);
      jooq.tables.pojos.Service service =
          businessValidator.requireServiceBelongsToBusinessAndReturn(pair.serviceId(), request.businessId());

      // Determine if tolerance should be applied (not applied for the first service)
      boolean applyTolerance = i != 0;

      // Find suitable employee and the start time for the service considering the tolerance
      Pair<Employee, LocalDateTime> employeeAndStartTime =
          findSuitableEmployeeAndStartTime(business.getId(), pair, appointmentStartTime, service.getAvgDuration(),
              applyTolerance);

      serviceEmployeeMap.put(pair.serviceId(),
          new ServiceEmployeeStartTime(service, employeeAndStartTime.getLeft(), employeeAndStartTime.getRight()));

      // Update appointmentStartTime for the next service based on the current service's duration
      appointmentStartTime = employeeAndStartTime.getRight().plusMinutes(service.getAvgDuration());
    }

    MultiServiceAppointmentData appointmentData = new MultiServiceAppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setServiceEmployeeStartTimeMap(serviceEmployeeMap);
    return appointmentData;
  }

  private Pair<Employee, LocalDateTime> findSuitableEmployeeAndStartTime(Integer businessId, EmployeeServiceIdPair pair,
                                                                         LocalDateTime startTime, Integer duration,
                                                                         boolean applyTolerance) {
    if (pair.employeeId() != null) {
      Employee employee = employeeValidator.requireEmployeeExistsAndReturn(pair.employeeId());
      EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);

      // Directly validate availability with or without tolerance and return the employee and the validated start time
      LocalDateTime validatedStartTime =
          validateEmployeeAvailability(businessId, employee.getId(), startTime, duration, applyTolerance);
      return Pair.of(employee, validatedStartTime);
    } else {
      // Find the first available employee considering the tolerance and return the employee and the adjusted start time
      return findFirstAvailableEmployeeAndStartTime(businessId, startTime, duration, applyTolerance);
    }
  }

  private LocalDateTime validateEmployeeAvailability(Integer businessId, Integer employeeId, LocalDateTime startTime,
                                                     Integer duration, boolean applyTolerance) {
    if (applyTolerance) {
      return timeSlotManagement.validateAvailabilityNewWithTolerance(businessId, employeeId, startTime, duration
      );
    } else {
      timeSlotManagement.validateAvailabilityNew(businessId, employeeId, startTime, duration);
      return startTime; // Return the original start time if tolerance isn't applied
    }
  }

  private Pair<Employee, LocalDateTime> findFirstAvailableEmployeeAndStartTime(Integer businessId, LocalDateTime startTime,
                                                                               Integer duration,
                                                                               boolean applyTolerance) {
    final List<Pair<Employee, LocalDateTime>> availableEmployees = timeSlotManagement.findAvailableEmployees(
        businessId, startTime, applyTolerance ? APPOINTMENT_TOLERANCE : 0, duration);

    if (availableEmployees.isEmpty()) {
      throw new ApplicationException("No available employees for the given time", HttpStatus.BAD_REQUEST);
    }

    // Return the first available employee and their respective available start time
    return availableEmployees.get(0);
  }

  //================================================================================================

  private List<Appointment> scheduleAppointments(Map<Integer, ServiceEmployeeStartTime> serviceEmployeeMap,
                                                 Customer customer) {
    List<Appointment> appointments = new ArrayList<>();

    for (Map.Entry<Integer, ServiceEmployeeStartTime> entry : serviceEmployeeMap.entrySet()) {
      Appointment appointment = new Appointment();
      appointment.setCustomerId(customer.getId());
      appointment.setServiceId(entry.getValue().service().getId());
      appointment.setBusinessId(entry.getValue().employee().getBusinessId());
      appointment.setEmployeeId(entry.getValue().employee().getId());
      appointment.setStartTime(entry.getValue().startTime());
      appointment.setEndTime(entry.getValue().startTime().plusMinutes(entry.getValue().service().getAvgDuration()));
      appointment.setStatus(SCHEDULED.toString());
      appointments.add(appointmentRepository.save(appointment));
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
      var service = data.getServiceEmployeeStartTimeMap().get(appointment.getServiceId()).service();
      var employee = data.getServiceEmployeeStartTimeMap().get(appointment.getServiceId()).employee();
      message.append(service.getTitle()).append(" with ").append(employee.getName()).append(", ");
    });

    // Remove the last comma and space
    message.setLength(message.length() - 2);

    createOutboxMessage(recipient, subject, message.toString());
  }

}
