package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.AppointmentRequestContext;
import com.dglisic.zakazime.business.controller.dto.AppointmentRichObject;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.DeleteBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.MultiServiceAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.SingleServiceAppointmentRequest;
import com.dglisic.zakazime.business.domain.AppointmentStatus;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.domain.SingleServiceAppointmentData;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.business.service.ServiceManagement;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  private final TimeSlotManagement timeSlotManagement;
  private final OutboxMessageRepository outboxMessageRepository;
  private final AppointmentRepository appointmentRepository;
  private final AppointmentSingleServiceCreator appointmentSingleServiceCreator;
  private final AppointmentMultiServiceCreator appointmentMultiServiceCreator;
  private final ServiceManagement serviceManagement;
  private final CustomerService customerService;

  @Override
  @Transactional
  public void createSingleServiceAppointment(SingleServiceAppointmentRequest request) {
    TimeSlotManagement.validateStartTime(request.startTime());
    appointmentSingleServiceCreator.createAppointment(request);
  }

  @Override
  @Transactional
  public void createMultiServiceAppointment(MultiServiceAppointmentRequest request) {
    appointmentMultiServiceCreator.createAppointment(request);
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
    final SingleServiceAppointmentData appointmentData = handleAppointmentAction(request, AppointmentStatus.CONFIRMED);
    createOutboxMessageAppointmentConfirmed(appointmentData);
  }

  @Override
  public void cancelAppointment(AppointmentRequestContext request) {
    final SingleServiceAppointmentData appointmentData = handleAppointmentAction(request, AppointmentStatus.CANCELLED);
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

  @Override
  public List<Appointment> getAllAppointments(Integer businessId) {
    return appointmentRepository.getAllAppointments(businessId);
  }

  @Override
  public List<AppointmentRichObject> getAllAppointmentsFullInfoFromDate(Integer businessId, @Nullable LocalDate fromDate) {
    var startDate = fromDate;
    if (fromDate == null) {
      startDate = LocalDate.now();
    }
    final List<Appointment> allAppointmentsFromDate = appointmentRepository.getAllAppointmentsFromDate(businessId, startDate);
    final List<AppointmentRichObject> result = new ArrayList<>();
    allAppointmentsFromDate.forEach(appointment -> {
      final jooq.tables.pojos.Service service = serviceManagement.getServiceById(appointment.getServiceId());
      final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(appointment.getEmployeeId());
      final Customer customer = customerService.requireCustomerExistsAndReturn(appointment.getCustomerId());
      result.add(new AppointmentRichObject(appointment, service, employee, customer));
    });
    return result;
  }

  private SingleServiceAppointmentData handleAppointmentAction(AppointmentRequestContext request, AppointmentStatus status) {
    businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    final Integer businessId = request.businessId();
    final Integer employeeId = request.employeeId();
    final Integer appointmentId = request.appointmentId();
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final SingleServiceAppointmentData appointmentData = businessAndEmployeeValidation(businessId, employeeId);
    final Appointment appointment = requireAppointmentExistsAndBelongsToEmployee(businessId, employeeId, appointmentId);
    appointmentData.setAppointment(appointment);
    final Customer customer = customerService.requireCustomerExistsAndReturn(appointment.getCustomerId());
    appointmentData.setCustomer(customer);
    // change appointment status to cancelled
    appointmentRepository.updateAppointmentStatus(appointmentId, status.toString());
    return appointmentData;
  }

  private SingleServiceAppointmentData businessAndEmployeeValidation(Integer businessId, Integer employeeId) {
    final Business business = businessValidator.requireBusinessExistsAndReturn(businessId);
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
    final SingleServiceAppointmentData appointmentData = new SingleServiceAppointmentData();
    appointmentData.setBusiness(business);
    appointmentData.setEmployee(employee);
    return appointmentData;
  }

  private void validateBlockTime(CreateBlockTimeRequest request) {
    businessValidator.requireBusinessExists(request.businessId());
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(request.employeeId());
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, request.businessId());
    timeSlotManagement.validateAvailabilityNew(request.businessId(), request.employeeId(), request.start(), request.duration());
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

  private void requireBlockTimeExistsAndBelongsToEmployee(Integer employeeId, Integer blockTimeId) {
    final Optional<EmployeeBlockTime> blockTime = appointmentRepository.findBlockTimeById(blockTimeId);
    if (blockTime.isEmpty()) {
      throw new ApplicationException("Block time not found", HttpStatus.BAD_REQUEST);
    }
    if (!blockTime.get().getEmployeeId().equals(employeeId)) {
      throw new ApplicationException("Block time does not belong to employee", HttpStatus.BAD_REQUEST);
    }
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

  private void createOutboxMessageAppointmentConfirmed(SingleServiceAppointmentData appointmentData) {
    final String recipient = appointmentData.getCustomer().getEmail();
    final String subject = "Termin potvrđen";
    final String timeFormatted = appointmentData.getAppointment().getStartTime().toString();
    final String message = "Termin potvrđen za " + timeFormatted + " kod " + appointmentData.getBusiness().getName() +
        " očekujemo vas u " + timeFormatted;
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessageAppointmentCancelled(SingleServiceAppointmentData appointmentData) {
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
