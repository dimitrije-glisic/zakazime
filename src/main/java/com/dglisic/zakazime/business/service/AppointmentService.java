package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.AppointmentRequestContext;
import com.dglisic.zakazime.business.controller.dto.AppointmentRichObject;
import com.dglisic.zakazime.business.controller.dto.MultiServiceAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.SingleServiceAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.DeleteBlockTimeRequest;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.EmployeeBlockTime;

public interface AppointmentService {

  void createSingleServiceAppointment(SingleServiceAppointmentRequest request);
  void createMultiServiceAppointment(MultiServiceAppointmentRequest request);

  void createBlockTime(CreateBlockTimeRequest request);

  void confirmAppointment(AppointmentRequestContext request);

  //  void cancelAppointment(Integer appointmentId);
  void cancelAppointment(AppointmentRequestContext request);

  void noShowAppointment(AppointmentRequestContext request);

  void deleteBlockTime(DeleteBlockTimeRequest request);

  void rescheduleAppointment(Integer businessId, Integer employeeId, Integer appointmentId, LocalDateTime newStart);

  List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date);

  List<EmployeeBlockTime> getBlockTimeForDate(Integer businessId, Integer employeeId, LocalDate date);

  List<Appointment> getAllAppointments(Integer businessId);

  List<AppointmentRichObject> getAllAppointmentsFullInfoFromDate(Integer businessId, @Nullable LocalDate fromDate);

  List<AppointmentRichObject> getAppointmentsForCustomer(Integer businessId, Integer customerId);

  AppointmentRichObject getAppointmentFullInfo(Integer businessId, Integer appointmentId);

  List<AppointmentRichObject> getAppointmentsForUser(Integer userId);
}
