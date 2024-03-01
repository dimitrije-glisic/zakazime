package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateEmployeeRequest;
import java.util.List;
import jooq.tables.pojos.Employee;

public interface EmployeeService {

  Employee createEmployee(Integer businessId, CreateEmployeeRequest request);

  void update(Integer businessId, Integer employeeId, CreateEmployeeRequest request);

  void activate(Integer businessId, Integer employeeId);

  void deactivate(Integer businessId, Integer employeeId);

  List<Employee> getAll(Integer businessId);

  Employee findById(Integer businessId, Integer employeeId);
}