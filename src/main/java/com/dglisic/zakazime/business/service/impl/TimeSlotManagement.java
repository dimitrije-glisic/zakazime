package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.EmployeeBlockTime;
import jooq.tables.pojos.WorkingHours;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TimeSlotManagement {

  private static final int SLOT_DURATION_IN_MINUTES = 15;

  private final BusinessValidator businessValidator;
  private final EmployeeValidator employeeValidator;
  private final BusinessService businessService;
  private final WorkingHoursRepository workingHoursRepository;
  private final AppointmentRepository appointmentRepository;


  public static void validateStartTime(LocalDateTime startTime) {
    final var now = LocalDateTime.now();
    if (startTime.isBefore(now)) {
      throw new ApplicationException("Appointment start time cannot be in the past", HttpStatus.BAD_REQUEST);
    }
    if (startTime.getMinute() % SLOT_DURATION_IN_MINUTES != 0) {
      throw new ApplicationException("Appointment start time minutes should be a multiple of 15 minutes", HttpStatus.BAD_REQUEST);
    }
  }

  public List<StartTime> findAvailableTimeSlotsForBusiness(Integer businessId, LocalDate date, Integer durationInMinutes) {
    businessValidator.requireBusinessExists(businessId);
//    validateDurationAgainstSlot(durationInMinutes);
    final List<Employee> employees = businessService.getEmployees(businessId);
    final Set<LocalTime> uniqueAvailableSlots = collectUniqueAvailableSlots(employees, businessId, date, durationInMinutes);
    return convertAndSortAvailableSlots(uniqueAvailableSlots);
  }

  public List<StartTime> getEmployeeAvailableTimeSlots(Integer businessId, Integer employeeId, LocalDate date, Integer duration) {
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);
//    validateDurationAgainstSlot(duration);
    final List<LocalTime> allAvailableSlots = getAvailableTimeSlotsForDate(employeeId, date);
    return filterSlotsByDuration(allAvailableSlots, duration).stream().map(StartTime::new).toList();
  }

  protected void validateAvailability(Integer businessId, Integer employeeId, LocalDateTime startTime, Integer duration) {
//    final var startTimeClean = startTime.withSecond(0).withNano(0);
//
//    final var now = LocalDateTime.now();
//    if (startTimeClean.isBefore(now)) {
//      throw new ApplicationException("Appointment start time cannot be in the past", HttpStatus.BAD_REQUEST);
//    }
//
//
//    //availableTimeSlotsInDay are all slots that can accommodate the requested duration
//    final List<StartTime> availableTimeSlotsInDay =
//        getEmployeeAvailableTimeSlots(businessId, employeeId, startTimeClean.toLocalDate(), duration);
//
//
//    // availableTimeSlotsInDay are rounded to the nearest 15 minute interval, but
//    if (availableTimeSlotsInDay.isEmpty()) {
//      throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
//    }
//
//    // startTimeClean might not be, so we need to do a check accordingly
//
//    // if starttime is not rounded to 15 it means it is not first slot in the sequence and we don't need to check previous slot
//
//    if (startTimeClean.toLocalTime().getMinute() % SLOT_DURATION_IN_MINUTES != 0) {
//      // check exact slot
//      if (!availableTimeSlotsInDay.contains(new StartTime(startTimeClean.toLocalTime()))) {
//        throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
//      }
//    } else {
//
//    }

  }


  protected void validateAvailabilityNew(Integer businessId, Integer employeeId, LocalDateTime startTime, Integer duration) {
    // Clean the start time by removing seconds and nanoseconds, then calculate the end time
    LocalDateTime startTimeClean = startTime.withSecond(0).withNano(0);
    LocalDateTime endTimeClean = startTimeClean.plusMinutes(duration);

    // Fetch working hours for the employee on the specified date
    WorkingHours workingHours = workingHoursRepository.getWorkingHours(employeeId, startTimeClean.toLocalDate());

    // Check if the new appointment's start or end time falls outside the employee's working hours
    if (isNotWithingWorkingHours(startTimeClean, workingHours) || isNotWithingWorkingHours(endTimeClean, workingHours)) {
      throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
    }

    // Check for any existing appointments that conflict with the new appointment's time
    validateAgainstExistingAppointments(businessId, employeeId, startTimeClean, endTimeClean);

    // Check for any block times that conflict with the new appointment's time
    validateAgainstBlockTimes(employeeId, startTimeClean, endTimeClean);
  }

  private boolean isNotWithingWorkingHours(LocalDateTime time, WorkingHours workingHours) {
    LocalTime timeToCheck = time.toLocalTime();
    return timeToCheck.isBefore(workingHours.getStartTime()) || timeToCheck.isAfter(workingHours.getEndTime());
  }

  private void validateAgainstExistingAppointments(Integer businessId, Integer employeeId, LocalDateTime startTime,
                                                   LocalDateTime endTime) {
    List<Appointment> appointments =
        appointmentRepository.getAppointmentsForDate(businessId, employeeId, startTime.toLocalDate());
    for (Appointment appointment : appointments) {
      if (timeOverlaps(appointment.getStartTime(), appointment.getEndTime(), startTime, endTime)) {
        throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
      }
    }
  }

  private void validateAgainstBlockTimes(Integer employeeId, LocalDateTime startTime, LocalDateTime endTime) {
    List<EmployeeBlockTime> blocks = appointmentRepository.getBlockTimeForDate(employeeId, startTime.toLocalDate());
    for (EmployeeBlockTime block : blocks) {
      if (timeOverlaps(block.getStartTime(), block.getEndTime(), startTime, endTime)) {
        throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
      }
    }
  }

  private boolean timeOverlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
    return start1.isBefore(end2) && start2.isBefore(end1);
  }

  private void validateDurationAgainstSlot(Integer durationInMinutes) {
    if (durationInMinutes % SLOT_DURATION_IN_MINUTES != 0) {
      throw new ApplicationException("Duration should be a multiple of 15 minutes", HttpStatus.BAD_REQUEST);
    }
  }

  private Set<LocalTime> collectUniqueAvailableSlots(List<Employee> employees, Integer businessId, LocalDate date,
                                                     Integer durationInMinutes) {
    Set<LocalTime> availableSlots = new LinkedHashSet<>();
    employees.forEach(employee -> {
      List<StartTime> employeeAvailableSlots =
          getEmployeeAvailableTimeSlots(businessId, employee.getId(), date, durationInMinutes);
      employeeAvailableSlots.stream()
          .map(StartTime::startTime)
          .forEach(availableSlots::add);
    });
    return availableSlots;
  }

  private List<StartTime> convertAndSortAvailableSlots(Set<LocalTime> availableSlots) {
    return availableSlots.stream()
        .sorted()
        .map(StartTime::new)
        .collect(Collectors.toList());
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

//  public List<LocalTime> getAvailableConsecutiveTimeSlotsForDate(
//      LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs, LocalDate date) {
//
//    List<LocalTime> availableTimeSlotsForDate =
//        getAvailableTimeSlotsForDate(serviceEmployeePairs.getFirst().getRight().getId(), date);
//    List<LocalTime> filteredSlots =
//        filterSlotsByDuration(availableTimeSlotsForDate, serviceEmployeePairs.getFirst().getLeft().getAvgDuration());
//    var firstSlot = filteredSlots.get(0);
//
//
//    List<LocalTime> availableConsecutiveTimeSlotsForDate =
//        getAvailableConsecutiveTimeSlotsForDate(serviceEmployeePairs, date, firstSlot);
//
//    if (availableConsecutiveTimeSlotsForDate == null || availableConsecutiveTimeSlotsForDate.isEmpty() ||
//        availableConsecutiveTimeSlotsForDate.size() < serviceEmployeePairs.size()) {
//      throw new ApplicationException("No available time slots found for the specified services", HttpStatus.BAD_REQUEST);
//    }
//
//    return availableConsecutiveTimeSlotsForDate;
//  }


  public List<LocalTime> findAllPossibleStartTimes(
      LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs, LocalDate date) {

    List<LocalTime> possibleStartTimes = new ArrayList<>();

    if (!serviceEmployeePairs.isEmpty()) {
      Pair<jooq.tables.pojos.Service, Employee> firstPair = serviceEmployeePairs.getFirst();  // Get the first pair but do not remove it
      Integer employeeId = firstPair.getRight().getId();
      Integer serviceDuration = firstPair.getLeft().getAvgDuration();
      List<LocalTime> availableSlots = getAvailableTimeSlotsForDate(employeeId, date);

      for (LocalTime slot : availableSlots) {
        // Make a copy of the original list for each recursive call
        LinkedList<Pair<jooq.tables.pojos.Service, Employee>> remainingPairs = new LinkedList<>(serviceEmployeePairs);
        remainingPairs.removeFirst();  // Remove the first pair for the recursive call

        // Check if the entire sequence can be scheduled starting from this slot
        List<LocalTime> result = getAvailableConsecutiveTimeSlotsForDate(
            remainingPairs, date, slot.plusMinutes(serviceDuration));

        if (result != null) {
          possibleStartTimes.add(slot);  // Add this slot as a possible start time
          // Do not return here; continue to find all possible start times
        }
      }
    }

    return possibleStartTimes;  // Return the list of all possible start times
  }


  public List<LocalTime> getAvailableConsecutiveTimeSlotsForDate(
      LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs, LocalDate date, LocalTime previousEndTime) {

    if (serviceEmployeePairs.isEmpty()) {
      return new ArrayList<>();  // Base case: no more services to schedule
    }

    Pair<jooq.tables.pojos.Service, Employee> currentPair = serviceEmployeePairs.removeFirst();
    Integer employeeId = currentPair.getRight().getId();
    Integer serviceDuration = currentPair.getLeft().getAvgDuration();
    List<LocalTime> availableSlots = getAvailableTimeSlotsForDate(employeeId, date);

    for (LocalTime slot : availableSlots) {
      // Check if the slot fits after the previous service, within the tolerable wait time
      if (fitsWithinTolerableTime(slot, previousEndTime)) {
        List<LocalTime> subsequentSlots = getAvailableConsecutiveTimeSlotsForDate(
            new LinkedList<>(serviceEmployeePairs), date, slot.plusMinutes(serviceDuration));  // Recursive step
        if (subsequentSlots != null) {
          subsequentSlots.add(0, slot);  // Prepend current slot to the list
          return subsequentSlots;
        }
      }
    }
    return null;  // No suitable slots found
  }

  private boolean fitsWithinTolerableTime(LocalTime slot, LocalTime previousEndTime) {
    final int tolerableWaitTimeMinutes = 15;
    return slot.equals(previousEndTime) ||
        slot.isAfter(previousEndTime) && slot.isBefore(previousEndTime.plusMinutes(tolerableWaitTimeMinutes).plusSeconds(1));
  }


}