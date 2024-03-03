package com.dglisic.zakazime.business.repository;

import java.time.LocalDate;
import jooq.tables.pojos.WorkingHours;

public interface WorkingHoursRepository {

  void storeWorkingHours(WorkingHours workingHours);

  void deleteWorkingHours(Integer employeeId);

  WorkingHours getWorkingHours(Integer employeeId, LocalDate date);
}
