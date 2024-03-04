package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import java.time.LocalDate;
import java.util.List;
import jooq.tables.EmployeeBlockTime;
import jooq.tables.pojos.Appointment;

public interface AppointmentService {

  void createAppointment(CreateAppointmentRequest request);

  EmployeeBlockTime createBlockTime(CreateBlockTimeRequest request);

  List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date);
}
