package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository {

  List<DateTimeSlot> getAppointmentsAndBlocks(Integer employeeId, LocalDate date);

}
