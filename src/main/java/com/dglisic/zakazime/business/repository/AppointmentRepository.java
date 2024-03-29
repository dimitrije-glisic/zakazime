package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.EmployeeBlockTime;
import jooq.tables.pojos.Review;

public interface AppointmentRepository {

  List<DateTimeSlot> getAppointmentsAndBlocks(Integer employeeId, LocalDate date);

  Appointment save(Appointment appointment);

  List<Appointment> getAppointmentsForDate(Integer businessId, Integer employeeId, LocalDate date);

  EmployeeBlockTime save(EmployeeBlockTime blockTime);

  List<EmployeeBlockTime> getBlockTimeForDate(Integer employeeId, LocalDate date);

  Optional<Appointment> findById(Integer appointmentId);

  void updateAppointmentStatus(Integer appointmentId, String status);

  Optional<EmployeeBlockTime> findBlockTimeById(Integer blockTimeId);

  void deleteBlockTime(Integer blockTimeId);

  List<Appointment> getAllAppointments(Integer businessId);

  List<Appointment> getAllAppointmentsFromDate(Integer businessId, LocalDate fromDate);

  List<Appointment> getAppointmentsForCustomerAndBusiness(Integer businessId, Integer customerId);

  List<Appointment> getAppointmentsForCustomer(Integer customerId);

  Optional<Review> findReviewByAppointmentId(Integer id);

  List<Appointment> getAllAppointmentsWithReview(Integer businessId);

  Optional<Appointment> getLastCreatedAppointment(Integer businessId);
}
