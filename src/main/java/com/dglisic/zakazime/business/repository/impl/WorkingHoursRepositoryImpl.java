package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.WorkingHours.WORKING_HOURS;

import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import java.time.LocalDate;
import jooq.tables.daos.WorkingHoursDao;
import jooq.tables.pojos.WorkingHours;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class WorkingHoursRepositoryImpl implements WorkingHoursRepository {

  private final WorkingHoursDao workingHoursDao;
  private final DSLContext dsl;

  @Override
  public void storeWorkingHours(WorkingHours workingHours) {
    workingHoursDao.insert(workingHours);
  }

  @Override
  public void deleteWorkingHours(Integer employeeId) {
    int execute = dsl.deleteFrom(WORKING_HOURS)
        .where(WORKING_HOURS.EMPLOYEE_ID.eq(employeeId))
        .execute();

    // 0 - expected when working hours for the employee are not set
    // 7 - expected when working hours for the employee are set
    if (execute != 0 && execute != 7) {
      throw new IllegalStateException("Working hours for employee with id: " + employeeId + " were not deleted properly");
    }

  }

  @Override
  public WorkingHours getWorkingHours(Integer employeeId, LocalDate date) {
    // Assuming a single working period per day for simplification
    return dsl.selectFrom(WORKING_HOURS)
        .where(WORKING_HOURS.EMPLOYEE_ID.eq(employeeId))
        .and(WORKING_HOURS.DAY_OF_WEEK.eq(date.getDayOfWeek().getValue()))
        .fetchOneInto(WorkingHours.class);
  }


}
