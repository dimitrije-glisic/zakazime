package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.WorkingHours;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TimeSlotManagement {

  private static final int SLOT_DURATION_IN_MINUTES = 15;

  private final EmployeeValidator employeeValidator;
  private final WorkingHoursRepository workingHoursRepository;
  private final AppointmentRepository appointmentRepository;

  public void validateTimeSlot(Integer businessId, Integer employeeId, LocalDateTime startTime, Integer duration) {
    final var startTimeClean = startTime.withSecond(0).withNano(0);
    final var minutes = startTimeClean.getMinute();

    final var now = LocalDateTime.now();
    if (startTimeClean.isBefore(now)) {
      throw new ApplicationException("Appointment start time cannot be in the past", HttpStatus.BAD_REQUEST);
    }

    if (minutes % SLOT_DURATION_IN_MINUTES != 0) {
      throw new ApplicationException("Appointment start time minutes should be a multiple of 15 minutes", HttpStatus.BAD_REQUEST);
    }
    if (duration % SLOT_DURATION_IN_MINUTES != 0) {
      throw new ApplicationException("Appointment duration should be a multiple of 15 minutes", HttpStatus.BAD_REQUEST);
    }

    final List<StartTime> availableTimeSlots =
        getAvailableTimeSlots(businessId, employeeId, startTimeClean.toLocalDate(), duration);
    if (availableTimeSlots.stream().noneMatch(slot -> slot.startTime().equals(startTimeClean.toLocalTime()))) {
      throw new ApplicationException("Requested time slot is not available", HttpStatus.BAD_REQUEST);
    }
  }

  public List<StartTime> getAvailableTimeSlots(Integer businessId, Integer employeeId, LocalDate date, Integer duration) {
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
    // duration should be a multiple of 15 minutes
    if (duration % SLOT_DURATION_IN_MINUTES != 0) {
      throw new ApplicationException("Duration should be a multiple of 15 minutes", HttpStatus.BAD_REQUEST);
    }
    final List<LocalTime> allAvailableSlots = getAvailableTimeSlotsForDate(employeeId, date);
    return filterSlotsByDuration(allAvailableSlots, duration).stream().map(StartTime::new).toList();
  }

  private List<LocalTime> getAvailableTimeSlotsForDate(Integer employeeId, LocalDate date) {
    final LocalDateTime now = LocalDateTime.now();
    if (date.isBefore(now.toLocalDate())) {
      throw new ApplicationException("Cannot get available time slots for past dates", HttpStatus.BAD_REQUEST);
    }
    final WorkingHours workingHours = workingHoursRepository.getWorkingHours(employeeId, date);

    // working hours is already rounded to the nearest 15 minute interval
    final LocalTime startTime = isToday(date) ? nowRoundedToSlotDuration() : workingHours.getStartTime();
    final LocalTime endTime = workingHours.getEndTime();

    // Generate all possible slots within working hours
    final List<LocalTime> potentialSlots = new ArrayList<>();
    for (LocalTime slotTime = startTime; slotTime.isBefore(endTime);
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

    // LocalTime -> StartTime dto conversion
    return potentialSlots;
  }

  private boolean isToday(LocalDate date) {
    return date.equals(LocalDate.now());
  }

  private LocalTime nowRoundedToSlotDuration() {
    final LocalTime now = LocalTime.now().withSecond(0).withNano(0);
    final int minutes = now.getMinute();
    final int remainder = minutes % SLOT_DURATION_IN_MINUTES;
    return now.plusMinutes(SLOT_DURATION_IN_MINUTES - remainder);
  }

  /**
   * Filters available slots by duration
   * <p>
   * Filters available slots by duration. A slot sequence is considered available if it is long enough to accommodate
   * the requested duration. For example, if the requested duration is 45 minutes, a slot sequence of 3 consecutive
   * 15-minute slots is considered available.
   * </p>
   *
   * @param slots    List of available slots
   * @param duration Requested duration in minutes
   * @return List of available slots that are long enough to accommodate the requested duration
   */
  private List<LocalTime> filterSlotsByDuration(List<LocalTime> slots, int duration) {
    final List<LocalTime> filteredSlots = new ArrayList<>();
    int numberOfSlotsRequired = (int) Math.ceil((double) duration / SLOT_DURATION_IN_MINUTES);

    for (int i = 0; i <= slots.size() - numberOfSlotsRequired; i++) {
      if (isSlotSequenceAvailable(slots, i, numberOfSlotsRequired)) {
        filteredSlots.add(slots.get(i));
      }
    }

    return filteredSlots;
  }

  private boolean isSlotSequenceAvailable(List<LocalTime> slots, int startIndex, int numberOfSlotsRequired) {
    final LocalTime expectedStart = slots.get(startIndex);

    for (int j = 0; j < numberOfSlotsRequired; j++) {
      final LocalTime actualStart = slots.get(startIndex + j);
      final LocalTime expectedSlotTime = expectedStart.plusMinutes((long) j * SLOT_DURATION_IN_MINUTES);

      if (!actualStart.equals(expectedSlotTime)) {
        return false;
      }
    }

    return true;
  }

}
