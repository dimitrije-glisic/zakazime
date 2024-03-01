package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateEmployeeRequest;
import com.dglisic.zakazime.business.controller.dto.EmployeeRichObject;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.EmployeeRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.EmployeeService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.OutboxMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  private final BusinessValidator businessValidator;
  private final UserService userService;
  private final EmployeeRepository employeeRepository;
  private final OutboxMessageRepository outboxMessageRepository;

  @Override
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
    return employeeRepository.save(employee);
  }

  @Override
  public List<Employee> getAll(Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    return employeeRepository.findByBusinessId(businessId);
  }

  @Override
  public Employee findById(Integer businessId, Integer employeeId) {
    businessValidator.requireBusinessExists(businessId);
    return requireEmployeeExistsAndReturn(employeeId);
  }

  @Override
  public void activate(Integer businessId, Integer employeeId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final Employee employee = requireEmployeeExistsAndReturn(employeeId);
    requireIsEmployeeOfBusiness(employee, businessId);
    employeeRepository.setEmployeeActive(employeeId);
  }

  @Override
  public void deactivate(Integer businessId, Integer employeeId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final Employee employee = requireEmployeeExistsAndReturn(employeeId);
    requireIsEmployeeOfBusiness(employee, businessId);
    employeeRepository.setEmployeeInactive(employeeId);
  }

  @Override
  public void update(Integer businessId, Integer employeeId, CreateEmployeeRequest request) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final Employee employee = requireEmployeeExistsAndReturn(employeeId);
    requireIsEmployeeOfBusiness(employee, businessId);
    final Employee updatedEmployee = fromRequest(request);
    updatedEmployee.setId(employeeId);
    updatedEmployee.setBusinessId(businessId);
    employeeRepository.update(updatedEmployee);
  }

  // =================
  // service related
  // =================
  @Override
  public void addService(Integer businessId, Integer employeeId, Integer serviceId) {
    validateOnServiceChange(businessId, employeeId, serviceId);
    employeeRepository.addService(employeeId, serviceId);
  }

  @Override
  public void deleteService(Integer businessId, Integer employeeId, Integer serviceId) {
    validateOnServiceChange(businessId, employeeId, serviceId);
    employeeRepository.deleteService(employeeId, serviceId);
  }

  @Override
  public List<jooq.tables.pojos.Service> getAllServices(Integer businessId, Integer employeeId) {
    return employeeRepository.getAllServices(businessId, employeeId);
  }

  // =================

  @Override
  public EmployeeRichObject findByIdFull(Integer businessId, Integer employeeId) {
    Optional<EmployeeRichObject> employee = employeeRepository.findByIdFull(businessId, employeeId);
    return employee.orElseThrow(
        () -> new ApplicationException("Employee with id " + employeeId + " not found", HttpStatus.NOT_FOUND));
  }

  private void validateOnServiceChange(Integer businessId, Integer employeeId, Integer serviceId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireServiceBelongsToBusiness(serviceId, businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final Employee employee = requireEmployeeExistsAndReturn(employeeId);
    requireIsEmployeeOfBusiness(employee, businessId);
  }

  private Employee requireEmployeeExistsAndReturn(Integer employeeId) {
    return employeeRepository.findById(employeeId)
        .orElseThrow(() -> new ApplicationException("Employee with id " + employeeId + " not found", HttpStatus.NOT_FOUND));
  }

  private void requireIsEmployeeOfBusiness(Employee employee, Integer businessId) {
    if (!employee.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Employee with id " + employee.getId() + " is not part of business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

  private Employee fromRequest(CreateEmployeeRequest request) {
    return new Employee()
        .setName(request.name())
        .setEmail(request.email())
        .setPhone(request.phone());
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
