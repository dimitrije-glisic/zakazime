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
import com.dglisic.zakazime.user.service.UserService;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.EmployeeBlockTime;
import jooq.tables.pojos.OutboxMessage;
import jooq.tables.pojos.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
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
  private final UserService userService;

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
  public void completeAppointment(AppointmentRequestContext request) {
    handleAppointmentAction(request, AppointmentStatus.COMPLETED);
  }

  @Override
  public void noShowAppointment(AppointmentRequestContext request) {
    handleAppointmentAction(request, AppointmentStatus.NO_SHOW);
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
    return getAppointmentRichObjects(allAppointmentsFromDate);
  }

  @Override
  public List<AppointmentRichObject> getAppointmentsForCustomer(Integer businessId, Integer customerId) {
    final Customer customer = customerService.requireCustomerExistsAndReturn(customerId);
    requireCustomerBelongsToBusiness(businessId, customer);
    final List<Appointment> appointments = appointmentRepository.getAppointmentsForCustomerAndBusiness(businessId, customerId);
    return getAppointmentRichObjects(appointments);
  }

  @Override
  public AppointmentRichObject requireAppointmentFullInfo(Integer businessId, Integer appointmentId) {
    final Appointment appointment = requireAppointmentExistsAndBelongsToBusiness(businessId, appointmentId);
    return createRichObject(appointment);
  }

  @Override
  public AppointmentRichObject requireAppointmentFullInfo(Integer appointmentId) {
    final Appointment appointment = requireAppointmentExistsAndReturn(appointmentId);
    return createRichObject(appointment);
  }

  @Override
  public List<AppointmentRichObject> getAppointmentsForUser(Integer userId) {
    final Account account = userService.findUserByIdOrElseThrow(userId);
    final List<Customer> customers = customerService.findCustomersByEmail(account.getEmail());
    if (customers.isEmpty()) {
      // account is not a customers - does not have appointments yet
      return new ArrayList<>();
    }

    final List<Appointment> allAppointments = new ArrayList<>();
    for (Customer customer : customers) {
      final List<Appointment> appointments = appointmentRepository.getAppointmentsForCustomer(customer.getId());
      allAppointments.addAll(appointments);
    }

    allAppointments.sort(Comparator.comparing(Appointment::getStartTime));
    return getAppointmentRichObjects(allAppointments);

  }

  @Override
  public List<AppointmentRichObject> getAllAppointmentsWithReviewsForBusiness(Integer businessId) {
    final List<Appointment> allAppointments = appointmentRepository.getAllAppointmentsWithReview(businessId);
    final List<AppointmentRichObject> result = new ArrayList<>();
    for (Appointment appointment : allAppointments) {
      result.add(createRichObject(appointment));
    }
    return result;
  }

  @Override
  public AppointmentRichObject getLastCreatedAppointmentFullInfo(Integer businessId) {
    final Optional<Appointment> lastAppointment = appointmentRepository.getLastCreatedAppointment(businessId);
    final Appointment appointment =
        lastAppointment.orElseThrow(() -> new ApplicationException("No appointments found", HttpStatus.NOT_FOUND));
    return createRichObject(appointment);
  }

  private Appointment requireAppointmentExistsAndBelongsToBusiness(Integer businessId, Integer appointmentId) {
    final Appointment appointment = requireAppointmentExistsAndReturn(appointmentId);
    if (!appointment.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Appointment does not belong to business", HttpStatus.BAD_REQUEST);
    }
    return appointment;
  }

  private void requireCustomerBelongsToBusiness(Integer businessId, Customer customer) {
    if (!customer.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Customer does not belong to business", HttpStatus.BAD_REQUEST);
    }
  }

  private List<AppointmentRichObject> getAppointmentRichObjects(List<Appointment> allAppointmentsFromDate) {
    final List<AppointmentRichObject> result = new ArrayList<>();
    allAppointmentsFromDate.forEach(appointment -> result.add(createRichObject(appointment)));
    return result;
  }

  private AppointmentRichObject createRichObject(Appointment appointment) {
    final jooq.tables.pojos.Service service = serviceManagement.getServiceById(appointment.getServiceId());
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(appointment.getEmployeeId());
    final Customer customer = customerService.requireCustomerExistsAndReturn(appointment.getCustomerId());
    final Business business = businessValidator.requireBusinessExistsAndReturn(appointment.getBusinessId());
    final Review review = appointmentRepository.findReviewByAppointmentId(appointment.getId()).orElse(null);
    return new AppointmentRichObject(appointment, service, employee, customer, business, review);
  }

  //todo: consider having separate methods for business owner and customer to handle appointment actions
  //todo: there is duplication in the code for handling appointment actions
  private SingleServiceAppointmentData handleAppointmentAction(AppointmentRequestContext request, AppointmentStatus status) {
    // appointment can be changed by business owner or customer who made the appointment
    requireUserPermittedToChangeAppointment(request);

    validateStatusTransition(request, status);

    final Integer businessId = request.businessId();
    final Integer employeeId = request.employeeId();
    final Integer appointmentId = request.appointmentId();
    final SingleServiceAppointmentData appointmentData = businessAndEmployeeValidation(businessId, employeeId);
    final Appointment appointment = requireAppointmentExistsAndBelongsToEmployee(businessId, employeeId, appointmentId);
    appointmentData.setAppointment(appointment);
    final Customer customer = customerService.requireCustomerExistsAndReturn(appointment.getCustomerId());
    appointmentData.setCustomer(customer);
    // change appointment status to cancelled
    appointmentRepository.updateAppointmentStatus(appointmentId, status.toString());
    return appointmentData;
  }

  private void requireUserPermittedToChangeAppointment(AppointmentRequestContext request) {
    try {
      businessValidator.requireCurrentUserPermittedToChangeBusiness(request.businessId());
    } catch (ApplicationException e) {
      // if not business owner, check if customer who made the appointment
      final Account account = userService.requireLoggedInUser();
      final Customer customer = customerService.findCustomerOfBusinessByEmail(request.businessId(), account.getEmail())
          .orElseThrow(() -> new ApplicationException("User not permitted to change appointment", HttpStatus.BAD_REQUEST));
      final Appointment appointment = requireAppointmentExistsAndReturn(request.appointmentId());
      if (!appointment.getCustomerId().equals(customer.getId())) {
        throw new ApplicationException("User not permitted to change appointment", HttpStatus.BAD_REQUEST);
      }
    }
  }

  private void validateStatusTransition(AppointmentRequestContext request, AppointmentStatus status) {
    final Appointment appointment = requireAppointmentExistsAndReturn(request.appointmentId());
    final AppointmentStatus currentStatus = AppointmentStatus.valueOf(appointment.getStatus());

    final boolean isBusinessOwner = isBusinessOwner(userService.requireLoggedInUser());
    if (!AppointmentStatus.canTransition(currentStatus, status, isBusinessOwner)) {
      throw new ApplicationException("Invalid status transition", HttpStatus.BAD_REQUEST);
    }
  }

  private boolean isBusinessOwner(Account user) {
    // todo: improve this
    final Integer SERVICE_PROVIDER_ROLE_ID = 3;
    return user.getRoleId().equals(SERVICE_PROVIDER_ROLE_ID);
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
