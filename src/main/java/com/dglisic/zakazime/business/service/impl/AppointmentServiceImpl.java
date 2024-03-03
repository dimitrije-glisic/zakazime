package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import com.dglisic.zakazime.business.service.AppointmentService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jooq.tables.EmployeeBlockTime;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.WorkingHours;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
  private static final int SLOT_DURATION_IN_MINUTES = 15;

  private final WorkingHoursRepository workingHoursRepository;
  private final AppointmentRepository appointmentRepository;

  @Override
  public Appointment createAppointment(CreateAppointmentRequest request) {
    return null;
  }

  @Override
  public EmployeeBlockTime createBlockTime(CreateBlockTimeRequest request) {
    return null;
  }

  @Override
  public List<StartTime> getAvailableTimeSlots(Integer businessId, Integer employeeId, LocalDate date) {
    final WorkingHours workingHours = workingHoursRepository.getWorkingHours(employeeId, date);
    final LocalTime startTime = workingHours.getStartTime();
    final LocalTime endTime = workingHours.getEndTime();

    // Generate all possible slots within working hours
    final List<LocalTime> potentialSlots = new ArrayList<>();
    for (LocalTime slotTime = startTime; slotTime.plusMinutes(SLOT_DURATION_IN_MINUTES).isBefore(endTime);
         slotTime = slotTime.plusMinutes(SLOT_DURATION_IN_MINUTES)) {
      potentialSlots.add(slotTime);
    }

    // Fetch appointments and blocked times for the employee on the given date
    final List<DateTimeSlot> appointmentsAndBlocks = appointmentRepository.getAppointmentsAndBlocks(employeeId, date);
    // Remove slots that overlap with appointments or blocked times
    appointmentsAndBlocks.forEach(record -> {
      LocalTime apptStart = record.startTime().toLocalTime();
      LocalTime apptEnd = record.endTime().toLocalTime();
      potentialSlots.removeIf(slot -> (slot.isBefore(apptEnd) && slot.plusMinutes(SLOT_DURATION_IN_MINUTES).isAfter(apptStart)));
    });

    // LocalTime -> TimeSlot dto conversion
    return potentialSlots.stream().map(StartTime::new).toList();
  }
}
