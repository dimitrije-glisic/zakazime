package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Employee.EMPLOYEE;
import static jooq.tables.EmployeeServiceMap.EMPLOYEE_SERVICE_MAP;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.WorkingHours.WORKING_HOURS;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.selectDistinct;

import com.dglisic.zakazime.business.controller.dto.EmployeeRichObject;
import com.dglisic.zakazime.business.controller.dto.WorkingHoursItem;
import com.dglisic.zakazime.business.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.EmployeeDao;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.WorkingHours;
import jooq.tables.records.EmployeeRecord;
import jooq.tables.records.ServiceRecord;
import jooq.tables.records.WorkingHoursRecord;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
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

  @Override
  public void addService(Integer employeeId, Integer serviceId) {
    dsl.insertInto(EMPLOYEE_SERVICE_MAP)
        .set(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID, employeeId)
        .set(EMPLOYEE_SERVICE_MAP.SERVICE_ID, serviceId)
        .execute();
  }

  @Override
  public void deleteService(Integer employeeId, Integer serviceId) {
    dsl.deleteFrom(EMPLOYEE_SERVICE_MAP)
        .where(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID.eq(employeeId))
        .and(EMPLOYEE_SERVICE_MAP.SERVICE_ID.eq(serviceId))
        .execute();
  }

  @Override
  public List<Service> getAllServices(Integer businessId, Integer employeeId) {
    return dsl.select(SERVICE)
        .from(EMPLOYEE_SERVICE_MAP)
        .join(SERVICE).on(EMPLOYEE_SERVICE_MAP.SERVICE_ID.eq(SERVICE.ID))
        .join(EMPLOYEE).on(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID.eq(EMPLOYEE.ID))
        .where(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID.eq(employeeId))
        .and(EMPLOYEE.BUSINESS_ID.eq(businessId))
        .fetchInto(Service.class);
  }

  @Override
  public Optional<EmployeeRichObject> findByIdFull(Integer businessId, Integer employeeId) {
    final Record3<EmployeeRecord, Result<Record>, Result<Record>>
        fetched = dsl.select(
            EMPLOYEE,
            multiset(
                selectDistinct(asterisk())
                    .from(EMPLOYEE_SERVICE_MAP)
                    .join(SERVICE).on(EMPLOYEE_SERVICE_MAP.SERVICE_ID.eq(SERVICE.ID))
                    .where(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID.eq(EMPLOYEE.ID))
                    .orderBy(SERVICE.TITLE.desc())
            ).as("services"),
            multiset(
                selectDistinct(asterisk())
                    .from(WORKING_HOURS)
                    .where(WORKING_HOURS.EMPLOYEE_ID.eq(EMPLOYEE.ID))
                    .orderBy(WORKING_HOURS.DAY_OF_WEEK.asc())

            ).as("working_hours")
        )
        .from(EMPLOYEE)
        .where(EMPLOYEE.ID.eq(employeeId))
        .and(EMPLOYEE.BUSINESS_ID.eq(businessId))
        .orderBy(EMPLOYEE.NAME.desc())
        .fetchOne();

    if (fetched == null) {
      return Optional.empty();
    } else {
      final Employee employee = fetched.value1().into(Employee.class);
      final List<Service> serviceList = fetched.value2().into(Service.class);
      final List<WorkingHours> workingHoursRecords = fetched.value3().into(WorkingHours.class);
      final List<WorkingHoursItem> workingHoursItems =
          workingHoursRecords.stream().map(whr -> new WorkingHoursItem(whr.getDayOfWeek(), whr.getStartTime(), whr.getEndTime(), whr.getIsWorkingDay()))
              .toList();
      return Optional.of(new EmployeeRichObject(employee, serviceList, workingHoursItems));
    }

  }

}
