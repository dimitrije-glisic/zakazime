package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.repository.EmployeeRepository;
import com.dglisic.zakazime.common.ApplicationException;
import jooq.tables.pojos.Employee;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmployeeValidator {

  private final EmployeeRepository employeeRepository;

  public Employee requireEmployeeExistsAndReturn(Integer employeeId) {
    return employeeRepository.findById(employeeId)
        .orElseThrow(() -> new ApplicationException("Employee with id " + employeeId + " not found", HttpStatus.NOT_FOUND));
  }

  public static void requireIsEmployeeOfBusiness(Employee employee, Integer businessId) {
    if (!employee.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Employee with id " + employee.getId() + " is not part of business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

}
