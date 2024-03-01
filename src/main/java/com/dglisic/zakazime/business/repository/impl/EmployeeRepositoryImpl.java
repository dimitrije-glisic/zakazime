package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Employee.EMPLOYEE;
import static org.jooq.impl.DSL.asterisk;

import com.dglisic.zakazime.business.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.EmployeeDao;
import jooq.tables.pojos.Employee;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

  private final EmployeeDao employeeDao;
  private final DSLContext dsl;

  @Override
  public Employee save(Employee employee) {
    return dsl.insertInto(EMPLOYEE)
        .set(dsl.newRecord(EMPLOYEE, employee))
        .returning(asterisk())
        .fetchOneInto(Employee.class);
  }

  @Override
  public void update(Employee employee) {
    // only name, email, and phone
    dsl.update(EMPLOYEE)
        .set(EMPLOYEE.NAME, employee.getName())
        .set(EMPLOYEE.EMAIL, employee.getEmail())
        .set(EMPLOYEE.PHONE, employee.getPhone())
        .where(EMPLOYEE.ID.eq(employee.getId()))
        .execute();
  }

  @Override
  public void linkToAccount(Integer employeeId, Integer accountId) {
    dsl.update(EMPLOYEE)
        .set(EMPLOYEE.ACCOUNT_ID, accountId)
        .where(EMPLOYEE.ID.eq(employeeId))
        .execute();
  }

  @Override
  public Optional<Employee> findById(Integer id) {
    return Optional.ofNullable(employeeDao.findById(id));
  }

  @Override
  public List<Employee> findByBusinessId(Integer businessId) {
    return dsl.selectFrom(EMPLOYEE)
        .where(EMPLOYEE.BUSINESS_ID.eq(businessId))
        .fetchInto(Employee.class);
  }

  @Override
  public void setEmployeeActive(Integer id) {
    dsl.update(EMPLOYEE)
        .set(EMPLOYEE.ACTIVE, true)
        .where(EMPLOYEE.ID.eq(id))
        .execute();
  }

  @Override
  public void setEmployeeInactive(Integer id) {
    dsl.update(EMPLOYEE)
        .set(EMPLOYEE.ACTIVE, false)
        .where(EMPLOYEE.ID.eq(id))
        .execute();
  }
}
