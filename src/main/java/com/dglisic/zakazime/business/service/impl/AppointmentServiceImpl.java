package com.dglisic.zakazime.business.service.impl;

import static com.dglisic.zakazime.business.domain.AppointmentStatus.SCHEDULED;

import com.dglisic.zakazime.business.controller.dto.AppointmentRequestContext;
import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.DeleteBlockTimeRequest;
import com.dglisic.zakazime.business.domain.AppointmentData;
import com.dglisic.zakazime.business.domain.AppointmentStatus;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.EmployeeBlockTime;
import jooq.tables.pojos.OutboxMessage;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

  private final BusinessValidator businessValidator;
  private final EmployeeValidator employeeValidator;
  private final CustomerService customerService;
  private final TimeSlotManagement timeSlotManagement;
  private final OutboxMessageRepository outboxMessageRepository;
  private final AppointmentRepository appointmentRepository;

  @Override
  @Transactional
  public void createAppointment(CreateAppointmentRequest request) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    final AppointmentData appointmentData = validateAppointment(request);
    final Customer customer =
        customerService.handleCustomerDataOnAppointmentCreation(request.businessId(), request.customerData());
    appointmentData.setCustomer(customer);
    final Appointment appointment = storeAppointment(request, customer);
    appointmentData.setAppointment(appointment);
    createOutboxMessageAppointmentScheduled(appointmentData);
  }

  @Override
  @Transactional
  public void createBlockTime(CreateBlockTimeRequest request) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    validateBlockTime(request);
    storeBlockTime(request);
  }

  @Override
  public void confirmAppointment(AppointmentRequestContext request) {
    final AppointmentData appointmentData = handleAppointmentAction(request, AppointmentStatus.CONFIRMED);
    createOutboxMessageAppointmentConfirmed(appointmentData);
  }

  @Override
  public void cancelAppointment(AppointmentRequestContext request) {
    final AppointmentData appointmentData = handleAppointmentAction(request, AppointmentStatus.CANCELLED);
    createOutboxMessageAppointmentCancelled(appointmentData);
  }

  @Override
  public void deleteBlockTime(DeleteBlockTimeRequest request) {
    final Integer businessId = request.businessId();
    final Integer employeeId = request.employeeId();
    final Integer blockTimeId = request.blockTimeId();
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    businessAndEmployeeValidation(businessId, employeeId);
    requireBlockTimeExistsAndBelongsToEmployee(employeeId, blockTimeId);
    appointmentRepository.deleteBlockTime(blockTimeId);
  }

  @Override
  public void rescheduleAppointment(Integer businessId, Integer employeeId, Integer appointmentId, LocalDateTime newStart) {

  }

  @Override
  public List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date) {
    businessAndEmployeeValidation(businessId, employeeId);
    return appointmentRepository.getAppointmentsForDate(businessId, employeeId, date);
  }

  @Override
  public List<EmployeeBlockTime> getBlockTimeForDate(Integer businessId, Integer employeeId, LocalDate date) {
    businessAndEmployeeValidation(businessId, employeeId);
    return appointmentRepository.getBlockTimeForDate(employeeId, date);
  }

  private AppointmentData handleAppointmentAction(AppointmentRequestContext request, AppointmentStatus status) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    final Integer businessId = request.businessId();
    final Integer employeeId = request.employeeId();
    final Integer appointmentId = request.appointmentId();
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final AppointmentData appointmentData = businessAndEmployeeValidation(businessId, employeeId);
    final Appointment appointment = requireAppointmentExistsAndBelongsToEmployee(businessId, employeeId, appointmentId);
    appointmentData.setAppointment(appointment);
    // change appointment status to cancelled
    appointmentRepository.updateAppointmentStatus(appointmentId, status.toString());
    return appointmentData;
  }

  private AppointmentData businessAndEmployeeValidation(Integer businessId, Integer employeeId) {
    final Business business = businessValidator.requireBusinessExistsAndReturn(businessId);
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
    final AppointmentData appointmentData = new AppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setEmployee(employee);
    return appointmentData;
  }

  private AppointmentData validateAppointment(CreateAppointmentRequest request) {
    final var business = businessValidator.requireBusinessExistsAndReturn(request.businessId());
    final var employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
    final var service =
        businessValidator.requireServiceBelongsToBusinessAndReturn(request.serviceId(), request.businessId());
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
    // require employee is available at requested time

    //request.startTime() minutes should be a multiple of 15 minutes
    timeSlotManagement.validateTimeSlot(request.businessId(), request.employeeId(), request.startTime(), request.duration());

    final AppointmentData appointmentData = new AppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setService(service);
    appointmentData.setEmployee(employee);
    return appointmentData;
  }

  private void validateBlockTime(CreateBlockTimeRequest request) {
    businessValidator.requireBusinessExists(request.businessId());
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
    timeSlotManagement.validateTimeSlot(request.businessId(), request.employeeId(), request.start(), request.duration());
  }

  private Appointment requireAppointmentExistsAndBelongsToEmployee(Integer businessId, Integer employeeId,
                                                                   Integer appointmentId) {
    final Appointment appointment = requireAppointmentExistsAndReturn(appointmentId);
    if (!appointment.getEmployeeId().equals(employeeId)) {
      throw new ApplicationException("Appointment does not belong to employee", HttpStatus.BAD_REQUEST);
    }
    if (!appointment.getBusinessId().equals(businessId)) {
      // should never happen because of businessAndEmployeeValidation
      throw new IllegalStateException("Appointment does not belong to business");
    }
    return appointment;
  }

  private Appointment requireAppointmentExistsAndReturn(Integer appointmentId) {
    final Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
    return appointment.orElseThrow(() -> new ApplicationException("Appointment not found", HttpStatus.BAD_REQUEST));
  }

  private void requireAppointmentStatusScheduled(Appointment appointment) {
    if (!appointment.getStatus().equals(SCHEDULED.toString())) {
      throw new ApplicationException("Appointment status is not " + SCHEDULED, HttpStatus.BAD_REQUEST);
    }
  }

  private void requireBlockTimeExistsAndBelongsToEmployee(Integer employeeId, Integer blockTimeId) {
    final Optional<EmployeeBlockTime> blockTime = appointmentRepository.findBlockTimeById(blockTimeId);
    if (blockTime.isEmpty()) {
      throw new ApplicationException("Block time not found", HttpStatus.BAD_REQUEST);
    }
    if (!blockTime.get().getEmployeeId().equals(employeeId)) {
      throw new ApplicationException("Block time does not belong to employee", HttpStatus.BAD_REQUEST);
    }
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
    appointment.setStatus(SCHEDULED.toString());
    return appointmentRepository.save(appointment);
  }

  private void storeBlockTime(CreateBlockTimeRequest request) {
    final EmployeeBlockTime blockTime = new EmployeeBlockTime();
    blockTime.setEmployeeId(request.employeeId());
    final var startTime = request.start().withSecond(0).withNano(0);
    blockTime.setStartTime(startTime);
    final var endTime = startTime.plusMinutes(request.duration());
    blockTime.setEndTime(endTime);
    appointmentRepository.save(blockTime);
  }

  private void createOutboxMessageAppointmentScheduled(AppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin zakazan";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin zakazan za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() +
        " za uslugu " + appointmentData.getService().getTitle() + " kod " + appointmentData.getEmployee().getName();
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessageAppointmentConfirmed(AppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin potvrđen";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin potvrđen za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() +
        " očekujemo vas u " + timeFormatted;
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessageAppointmentCancelled(AppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin otkazan";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin otkazan za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() +
        " je otkazan. Molimo vas zakažite novi termin i dobićete popust od 10%";
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
