package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Appointment.APPOINTMENT;
import static jooq.tables.EmployeeBlockTime.EMPLOYEE_BLOCK_TIME;
import static org.jooq.impl.DSL.select;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.EmployeeBlockTime;
import jooq.tables.records.AppointmentRecord;
import jooq.tables.records.EmployeeBlockTimeRecord;
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

  @Override
  public EmployeeBlockTime save(EmployeeBlockTime blockTime) {
    final EmployeeBlockTimeRecord blockTimeRecord = jooq.newRecord(EMPLOYEE_BLOCK_TIME, blockTime);
    blockTimeRecord.store();
    return blockTimeRecord.into(EmployeeBlockTime.class);
  }

  @Override
  public List<EmployeeBlockTime> getBlockTimeForDate(Integer employeeId, LocalDate date) {
    final Condition condition = EMPLOYEE_BLOCK_TIME.EMPLOYEE_ID.eq(employeeId)
        .and(EMPLOYEE_BLOCK_TIME.EMPLOYEE_ID.eq(employeeId))
        .and(EMPLOYEE_BLOCK_TIME.START_TIME.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
    return jooq.selectFrom(EMPLOYEE_BLOCK_TIME)
        .where(condition)
        .fetchInto(EmployeeBlockTime.class);
  }

  @Override
  public Optional<Appointment> findById(Integer appointmentId) {
    return jooq.selectFrom(APPOINTMENT)
        .where(APPOINTMENT.ID.eq(appointmentId))
        .fetchOptionalInto(Appointment.class);
  }

  @Override
  public void updateAppointmentStatus(Integer appointmentId, String status) {
    jooq.update(APPOINTMENT)
        .set(APPOINTMENT.STATUS, status)
        .where(APPOINTMENT.ID.eq(appointmentId))
        .execute();
  }

  @Override
  public Optional<EmployeeBlockTime> findBlockTimeById(Integer blockTimeId) {
    return jooq.selectFrom(EMPLOYEE_BLOCK_TIME)
        .where(EMPLOYEE_BLOCK_TIME.ID.eq(blockTimeId))
        .fetchOptionalInto(EmployeeBlockTime.class);
  }

  @Override
  public void deleteBlockTime(Integer blockTimeId) {
    jooq.deleteFrom(EMPLOYEE_BLOCK_TIME)
        .where(EMPLOYEE_BLOCK_TIME.ID.eq(blockTimeId))
        .execute();
  }

  @Override
  public List<Appointment> getAllAppointments(Integer businessId) {
    return jooq.selectFrom(APPOINTMENT)
        .where(APPOINTMENT.BUSINESS_ID.eq(businessId))
        .fetchInto(Appointment.class);
  }

  @Override
  public List<Appointment> getAllAppointmentsFromDate(Integer businessId, LocalDate fromDate) {
    return jooq.select(APPOINTMENT)
        .from(APPOINTMENT)
        .where(APPOINTMENT.BUSINESS_ID.eq(businessId)
            .and(APPOINTMENT.START_TIME.greaterOrEqual(fromDate.atStartOfDay())))
        .fetchInto(Appointment.class);
  }

}
