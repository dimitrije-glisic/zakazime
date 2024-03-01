package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Employee;

public interface EmployeeRepository {

  Employee save(Employee employee);

  void update(Employee employee);

  void linkToAccount(Integer employeeId, Integer accountId);

  Optional<Employee> findById(Integer id);

  List<Employee> findByBusinessId(Integer businessId);

  void setEmployeeActive(Integer id);

  void setEmployeeInactive(Integer id);

}
