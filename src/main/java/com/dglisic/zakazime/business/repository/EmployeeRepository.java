package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.controller.dto.EmployeeRichObject;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;

public interface EmployeeRepository {

  Employee save(Employee employee);

  void update(Employee employee);

  void linkToAccount(Integer employeeId, Integer accountId);

  Optional<Employee> findById(Integer id);

  List<Employee> findByBusinessId(Integer businessId);

  void setEmployeeActive(Integer id);

  void setEmployeeInactive(Integer id);

  void addService(Integer employeeId, Integer serviceId);

  void deleteService(Integer employeeId, Integer serviceId);

  List<Service> getAllServices(Integer businessId, Integer employeeId);

  Optional<EmployeeRichObject> findByIdFull(Integer businessId, Integer employeeId);
}
