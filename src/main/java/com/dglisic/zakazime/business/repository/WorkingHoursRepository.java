package com.dglisic.zakazime.business.repository;

import jooq.tables.pojos.WorkingHours;

public interface WorkingHoursRepository {

  void storeWorkingHours(WorkingHours workingHours);

  void deleteWorkingHours(Integer employeeId);
}
