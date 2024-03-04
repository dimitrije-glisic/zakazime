package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Appointment.APPOINTMENT;
import static jooq.tables.EmployeeBlockTime.EMPLOYEE_BLOCK_TIME;
import static org.jooq.impl.DSL.select;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jooq.tables.pojos.Appointment;
import jooq.tables.records.AppointmentRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

  private final DSLContext jooq;

  @Override
  public List<DateTimeSlot> getAppointmentsAndBlocks(Integer employeeId, LocalDate date) {
    final Result<Record2<LocalDateTime, LocalDateTime>> fetch = jooq.select(APPOINTMENT.START_TIME, APPOINTMENT.END_TIME)
        .from(APPOINTMENT)
        .where(APPOINTMENT.EMPLOYEE_ID.eq(employeeId)
            .and(APPOINTMENT.START_TIME.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
        )
        .unionAll(select(EMPLOYEE_BLOCK_TIME.START_TIME, EMPLOYEE_BLOCK_TIME.END_TIME)
            .from(EMPLOYEE_BLOCK_TIME)
            .where(EMPLOYEE_BLOCK_TIME.EMPLOYEE_ID.eq(employeeId)
                .and(EMPLOYEE_BLOCK_TIME.START_TIME.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay())))
        )
        .fetch();

    return fetch.map(record -> new DateTimeSlot(record.value1(), record.value2()));
  }

  @Override
  public Appointment save(Appointment appointment) {
    final AppointmentRecord appointmentRecord = jooq.newRecord(APPOINTMENT, appointment);
    appointmentRecord.store();
    return appointmentRecord.into(Appointment.class);
  }

  @Override
  public List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date) {
    final Condition condition = APPOINTMENT.BUSINESS_ID.eq(businessId)
        .and(APPOINTMENT.EMPLOYEE_ID.eq(employeeId))
        .and(APPOINTMENT.START_TIME.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
    return jooq.selectFrom(APPOINTMENT)
        .where(condition)
        .fetchInto(Appointment.class);
  }

}
