package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateEmployeeRequest;
import com.dglisic.zakazime.business.controller.dto.EmployeeRichObject;
import com.dglisic.zakazime.business.controller.dto.WorkingHoursItem;
import com.dglisic.zakazime.business.controller.dto.WorkingHoursRequest;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.EmployeeRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import com.dglisic.zakazime.business.service.EmployeeService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.OutboxMessage;
import jooq.tables.pojos.WorkingHours;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  private final BusinessValidator businessValidator;
  private final EmployeeValidator employeeValidator;
  private final UserService userService;
  private final EmployeeRepository employeeRepository;
  private final WorkingHoursRepository workingHoursRepository;
  private final OutboxMessageRepository outboxMessageRepository;

  @Override
  @Transactional
  public Employee createEmployee(Integer businessId, CreateEmployeeRequest request) {
    log.info("Creating employee for business with id: {}", businessId);
    final Business business = businessValidator.requireBusinessExistsAndReturn(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    // create account for the new employee
    final Account employeeAccount = userService.createBusinessUser(business);
    final Employee employee = fromRequest(request);
    employee.setBusinessId(businessId);
    employee.setAccountId(employeeAccount.getId());
    employee.setActive(false);
    createOutboxMessageEmployeeCreated(employee, employeeAccount, business);
    final Employee saved = employeeRepository.save(employee);
    setWorkingHours(businessId, saved.getId(), defaultWorkingHours());
    return saved;
  }

  @Override
  public List<Employee> getAll(Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    return employeeRepository.findByBusinessId(businessId);
  }

  @Override
  public Employee findById(Integer businessId, Integer employeeId) {
    businessValidator.requireBusinessExists(businessId);
    return employeeValidator.requireEmployeeExistsAndReturn(employeeId);
  }

  @Override
  public EmployeeRichObject findByIdFull(Integer businessId, Integer employeeId) {
    log.debug("Getting full employee data for business with id: {}", businessId);
    final Optional<EmployeeRichObject> employee = employeeRepository.findByIdFull(businessId, employeeId);
    return employee.orElseThrow(
        () -> new ApplicationException("Employee with id " + employeeId + " not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public void activate(Integer businessId, Integer employeeId) {
    validateOnEmployeeChange(businessId, employeeId);
    employeeRepository.setEmployeeActive(employeeId);
  }

  @Override
  public void deactivate(Integer businessId, Integer employeeId) {
    validateOnEmployeeChange(businessId, employeeId);
    employeeRepository.setEmployeeInactive(employeeId);
  }

  @Override
  public void update(Integer businessId, Integer employeeId, CreateEmployeeRequest request) {
    validateOnEmployeeChange(businessId, employeeId);
    final Employee updatedEmployee = fromRequest(request);
    updatedEmployee.setId(employeeId);
    updatedEmployee.setBusinessId(businessId);
    employeeRepository.update(updatedEmployee);
  }

  // =================
  // service related

  // =================
  @Override
  public void addServices(Integer businessId, Integer employeeId, List<Integer> serviceIds) {
    validateOnServiceChange(businessId, employeeId, serviceIds);
    serviceIds.forEach(serviceId -> employeeRepository.addService(employeeId, serviceId));
  }

  @Override
  public void deleteService(Integer businessId, Integer employeeId, Integer serviceId) {
    validateOnServiceChange(businessId, employeeId, Collections.singletonList(serviceId));
    employeeRepository.deleteService(employeeId, serviceId);
  }

  @Override
  public List<jooq.tables.pojos.Service> getAllServices(Integer businessId, Integer employeeId) {
    return employeeRepository.getAllServices(businessId, employeeId);
  }

  // =================
  // working hours

  // =================
  @Override
  @Transactional
  public void setWorkingHours(Integer businessId, Integer employeeId, WorkingHoursRequest request) {
    validateOnEmployeeChange(businessId, employeeId);
    validateWorkingHours(request);
    // first delete all working hours for the employee
    workingHoursRepository.deleteWorkingHours(employeeId);
    // then add new working hours
    final List<WorkingHours> workingHours = fromRequest(request, employeeId);
    workingHours.forEach(workingHoursRepository::storeWorkingHours);
  }

  private void validateOnEmployeeChange(Integer businessId, Integer employeeId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
  }

  private void validateOnServiceChange(Integer businessId, Integer employeeId, List<Integer> serviceIds) {
    serviceIds.forEach(serviceId -> businessValidator.requireServiceBelongsToBusiness(serviceId, businessId));
    validateOnEmployeeChange(businessId, employeeId);
  }

  private WorkingHoursRequest defaultWorkingHours() {
    final List<WorkingHoursItem> workingHours = List.of(
        new WorkingHoursItem(1, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(2, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(3, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(4, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(5, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(6, LocalTime.of(8, 0), LocalTime.of(16, 0), true),
        new WorkingHoursItem(7, LocalTime.of(8, 0), LocalTime.of(16, 0), false)
    );
    return new WorkingHoursRequest(workingHours);
  }

  private void validateWorkingHours(WorkingHoursRequest request) {
    final Set<Integer> daysOfWeek = new HashSet<>();
    for (WorkingHoursItem wh : request.getWorkingHours()) {
      if (wh.isWorkingDay() && wh.getStartTime().isAfter(wh.getEndTime())) {
        throw new ApplicationException("Start time must be before end time", HttpStatus.BAD_REQUEST);
      }
      int day = wh.getDayOfWeek();
      if (day < 1 || day > 7) {
        throw new ApplicationException("Day of week must be between 0 and 6", HttpStatus.BAD_REQUEST);
      }
      daysOfWeek.add(day);
    }
    if (daysOfWeek.size() != 7) {
      throw new ApplicationException("Working hours for all days are required", HttpStatus.BAD_REQUEST);
    }
  }

  private Employee fromRequest(CreateEmployeeRequest request) {
    return new Employee()
        .setName(request.name())
        .setEmail(request.email())
        .setPhone(request.phone());
  }

  private List<WorkingHours> fromRequest(WorkingHoursRequest request, Integer employeeId) {
    return request.getWorkingHours().stream().map(
        wh -> new WorkingHours()
            .setEmployeeId(employeeId)
            .setDayOfWeek(wh.getDayOfWeek())
            .setStartTime(wh.getStartTime())
            .setEndTime(wh.getEndTime())
            .setIsWorkingDay(wh.isWorkingDay())
    ).toList();
  }

  private void createOutboxMessageEmployeeCreated(Employee employee, Account employeeAccount, Business business) {
    final String recipient = business.getEmail();
    final String subject = "Nalog kreiran za zaposlenog";
    final String body =
        "Nalog je kreiran za  " + employee.getName() + ". Korisnicko ime: " + employeeAccount.getEmail() + " Sifra: " +
            employeeAccount.getPassword() + " Molimo vas da ga aktivirate.";
    createOutboxMessage(recipient, subject, body);
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
