package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import jooq.tables.pojos.Appointment;

public interface AppointmentRepository {

  List<DateTimeSlot> getAppointmentsAndBlocks(Integer employeeId, LocalDate date);

  Appointment save(Appointment appointment);

  List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date);
}
