package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.DateTimeSlot;
import com.dglisic.zakazime.business.controller.dto.MultiServiceEmployeeAvailabilityRequest;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.WorkingHoursRepository;
import com.dglisic.zakazime.business.service.EmployeeService;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.annotation.Nullable;
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
  private final EmployeeService employeeService;
  private final WorkingHoursRepository workingHoursRepository;
  private final AppointmentRepository appointmentRepository;


  public static void validateStartTime(LocalDateTime startTime) {
    final var now = LocalDateTime.now();
    if (startTime.isBefore(now)) {
      throw new ApplicationException("Appointment start time cannot be in the past", HttpStatus.BAD_REQUEST);
    }
  }

  public List<StartTime> findAvailableTimeSlotsForBusiness(Integer businessId, LocalDate date, Integer durationInMinutes) {
    businessValidator.requireBusinessExists(businessId);
//    validateDurationAgainstSlot(durationInMinutes);
    final List<Employee> employees = employeeService.getAllForBusiness(businessId);
    final Set<LocalTime> uniqueAvailableSlots = collectUniqueAvailableSlots(employees, date, durationInMinutes);
    return convertAndSortAvailableSlots(uniqueAvailableSlots);
  }

  public List<StartTime> getEmployeeAvailableTimeSlots(Integer employeeId, LocalDate date, Integer duration) {
    final List<LocalTime> allAvailableSlots = getAvailableTimeSlotsForDate(employeeId, date);
    return filterSlotsByDuration(allAvailableSlots, duration).stream().map(StartTime::new).toList();
  }

  protected void validateAvailabilityNew(Integer businessId, Integer employeeId, LocalDateTime startTime, Integer duration) {
    // Clean the start time by removing seconds and nanoseconds, then calculate the end time
    LocalDateTime startTimeClean = startTime.withSecond(0).withNano(0);
    LocalDateTime endTimeClean = startTimeClean.plusMinutes(duration);

    // Fetch working hours for the employee on the specified date
    WorkingHours workingHours = workingHoursRepository.getWorkingHours(employeeId, startTimeClean.toLocalDate());

    // Check if the new appointment's start or end time falls outside the employee's working hours
    if (isNotWithinWorkingHours(startTimeClean, workingHours) || isNotWithinWorkingHours(endTimeClean, workingHours)) {
      throw new ApplicationException("Employee is not available at the requested time", HttpStatus.BAD_REQUEST);
    }

    // Check for any existing appointments that conflict with the new appointment's time
    validateAgainstExistingAppointments(businessId, employeeId, startTimeClean, endTimeClean);

    // Check for any block times that conflict with the new appointment's time
    validateAgainstBlockTimes(employeeId, startTimeClean, endTimeClean);
  }

  protected LocalDateTime validateAvailabilityNewWithTolerance(Integer businessId, Integer employeeId, LocalDateTime startTime,
                                                               Integer duration) {
    final int CHECK_INTERVAL = 5; // Define how often within the tolerance range you want to check

    LocalDateTime time = startTime.withSecond(0).withNano(0);
    LocalDateTime endTime = time.plusMinutes(AppointmentMultiServiceCreator.APPOINTMENT_TOLERANCE);

    while (time.isBefore(endTime)) {
      LocalDateTime endTimeClean = time.plusMinutes(duration);
      WorkingHours workingHours = workingHoursRepository.getWorkingHours(employeeId, time.toLocalDate());

      if (!isNotWithinWorkingHours(time, workingHours) && !isNotWithinWorkingHours(endTimeClean, workingHours)) {
        try {
          validateAgainstExistingAppointments(businessId, employeeId, time, endTimeClean);
          validateAgainstBlockTimes(employeeId, time, endTimeClean);
          return time; // Return the first available start time within the tolerance range
        } catch (ApplicationException ignored) {
          // If there's a conflict, catch the exception and continue to the next time slot
        }
      }
      time = time.plusMinutes(CHECK_INTERVAL); // Move to the next time slot within the tolerance range
    }
    throw new ApplicationException("No available time slots found for the specified services", HttpStatus.BAD_REQUEST);
  }

  private boolean isNotWithinWorkingHours(LocalDateTime time, WorkingHours workingHours) {
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

  private Set<LocalTime> collectUniqueAvailableSlots(List<Employee> employees, LocalDate date,
                                                     Integer durationInMinutes) {
    Set<LocalTime> availableSlots = new LinkedHashSet<>();
    employees.forEach(employee -> {
      List<StartTime> employeeAvailableSlots =
          getEmployeeAvailableTimeSlots(employee.getId(), date, durationInMinutes);
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

  protected List<Pair<Employee, LocalDateTime>> findAvailableEmployees(Integer id, LocalDateTime appointmentStartTime,
                                                                       Integer tolerance,
                                                                       Integer avgDuration) {
    final int CHECK_INTERVAL = 5;
    List<Employee> employees = employeeService.getAllForBusiness(id);
    List<Pair<Employee, LocalDateTime>> availableEmployees = new ArrayList<>();

    LocalDateTime endTime = appointmentStartTime.plusMinutes(tolerance).plusSeconds(1);
    LocalDateTime time;
    for (Employee employee : employees) {
      for (time = appointmentStartTime; time.isBefore(endTime); time = time.plusMinutes(CHECK_INTERVAL)) {
        try {
          validateAvailabilityNew(id, employee.getId(), time, avgDuration);
          availableEmployees.add(Pair.of(employee, time));
          break; // Break from the time loop if the employee is available at this time
        } catch (ApplicationException e) {
          // Continue checking the next time slot
        }
      }
    }
    return availableEmployees;
  }

  // ================================================================================================================
  // no-employeeId-specified functionality
  // ================================================================================================================

  public Set<LocalTime> findAllPossibleStartTimes(MultiServiceEmployeeAvailabilityRequest request) {
    final LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs = new LinkedList<>();
    for (var pair : request.employeeServicePairs()) {
      var service = businessValidator.requireServiceBelongsToBusinessAndReturn(pair.serviceId(), request.businessId());
      var employee = pair.employeeId() != null ? employeeValidator.requireEmployeeExistsAndReturn(pair.employeeId()) : null;
      serviceEmployeePairs.add(Pair.of(service, employee));
    }
    final LocalDate date = request.date();
    final Set<LocalTime> possibleStartTimes = findAllPossibleStartTimesNew(request.businessId(), serviceEmployeePairs, date);
    if (possibleStartTimes.isEmpty()) {
      throw new ApplicationException("No available time slots found for the specified services", HttpStatus.BAD_REQUEST);
    }
    return possibleStartTimes;
  }

  // here the fun begins
  protected Set<LocalTime> findAllPossibleStartTimesNew(Integer businessId,
                                                        LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs,
                                                        LocalDate date) {
    //start times must come from the first service-employee pair
    final Set<LocalTime> possibleStartTimes = new LinkedHashSet<>();

    if (!serviceEmployeePairs.isEmpty()) {
      Pair<jooq.tables.pojos.Service, Employee> firstPair = serviceEmployeePairs.getFirst();
      var serviceDuration = firstPair.getLeft().getAvgDuration();
      // Check if a specific employee is required for the first service
      // if not, check all employees for available slots and add all possible start times
      if (firstPair.getRight() == null) {
        // If no specific employee is required, check all employees for available slots
        List<Employee> allEmployees = employeeService.getAllForBusiness(businessId);
        for (Employee employee : allEmployees) {
          List<LocalTime> availableSlots = getAvailableTimeSlotsForDate(employee.getId(), date);
          checkFirstLevelSlotsAndAddIfChainExists(businessId, availableSlots, serviceEmployeePairs, date, possibleStartTimes,
              serviceDuration);
        }
      } else {
        // If a specific employee is required, check only that employee for available slots
        Integer employeeId = firstPair.getRight().getId();
        List<LocalTime> availableSlots = getAvailableTimeSlotsForDate(employeeId, date);
        checkFirstLevelSlotsAndAddIfChainExists(businessId, availableSlots, serviceEmployeePairs, date, possibleStartTimes,
            serviceDuration);
      }
    }

    return possibleStartTimes;
  }

  private void checkFirstLevelSlotsAndAddIfChainExists(Integer businessId, List<LocalTime> availableSlots,
                                                       LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs,
                                                       LocalDate date, Set<LocalTime> possibleStartTimes,
                                                       Integer serviceDuration) {
    for (LocalTime slot : availableSlots) {
      // Make a copy of the original list for each recursive call
      LinkedList<Pair<jooq.tables.pojos.Service, Employee>> remainingPairs = new LinkedList<>(serviceEmployeePairs);
      remainingPairs.removeFirst();  // Remove the first pair for the recursive call

      // Check if the entire sequence can be scheduled starting from this slot
      List<LocalTime> result =
          getAvailableConsecutiveTimeSlotsForDateNew(businessId, remainingPairs, date, slot.plusMinutes(serviceDuration));

      if (result != null) {
        possibleStartTimes.add(slot);  // Add this slot as a possible start time
        // Do not return here; continue to find all possible start times
      }
    }
  }

  private List<LocalTime> getAvailableConsecutiveTimeSlotsForDateNew(Integer businessId,
                                                                     LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs,
                                                                     LocalDate date, LocalTime previousEndTime) {
    if (serviceEmployeePairs.isEmpty()) {
      return new ArrayList<>();
    }

    Pair<jooq.tables.pojos.Service, Employee> currentPair = serviceEmployeePairs.removeFirst();

    // If no specific employee is required
    if (currentPair.getRight() == null) {
      List<Employee> allEmployees = employeeService.getAllForBusiness(businessId);
      for (Employee employee : allEmployees) {
        List<LocalTime> employeeSlots = getAvailableTimeSlotsForDate(employee.getId(), date);
        List<LocalTime> subsequentSlots =
            consecutiveRecursion(businessId, serviceEmployeePairs, date, previousEndTime, currentPair, employeeSlots);
        if (subsequentSlots != null) {
          // return first suitable sequence of slots found (first suitable employee)
          return subsequentSlots;
        }
      }
    } else {
      Integer employeeId = currentPair.getRight().getId();
      List<LocalTime> availableSlots = getAvailableTimeSlotsForDate(employeeId, date);
      List<LocalTime> subsequentSlots =
          consecutiveRecursion(businessId, serviceEmployeePairs, date, previousEndTime, currentPair, availableSlots);
      if (subsequentSlots != null) {
        return subsequentSlots;
      }
    }

    return null; // No suitable slots found
  }

  @Nullable
  private List<LocalTime> consecutiveRecursion(Integer businessId,
                                               LinkedList<Pair<jooq.tables.pojos.Service, Employee>> serviceEmployeePairs,
                                               LocalDate date,
                                               LocalTime previousEndTime, Pair<jooq.tables.pojos.Service, Employee> currentPair,
                                               List<LocalTime> employeeSlots) {
    for (LocalTime slot : employeeSlots) {
      if (fitsWithinTolerableTime(slot, previousEndTime)) {
        List<LocalTime> subsequentSlots =
            getAvailableConsecutiveTimeSlotsForDateNew(businessId, new LinkedList<>(serviceEmployeePairs), date,
                slot.plusMinutes(currentPair.getLeft().getAvgDuration()));
        if (subsequentSlots != null) {
          subsequentSlots.add(0, slot);
          return subsequentSlots;
        }
      }
    }
    return null;
  }

  private boolean fitsWithinTolerableTime(LocalTime slot, LocalTime previousEndTime) {
    final int tolerableWaitTimeMinutes = 15;
    return slot.equals(previousEndTime) ||
        slot.isAfter(previousEndTime) && slot.isBefore(previousEndTime.plusMinutes(tolerableWaitTimeMinutes).plusSeconds(1));
  }

  //===============================================================================================================

  public List<StartTime> getEmployeeAvailableTimeSlotsNew(Integer businessId, Integer serviceId, LocalDate date,
                                                          @Nullable Integer employeeId) {
    // Validate business and service, and retrieve service details
    businessValidator.requireBusinessExists(businessId);
    final jooq.tables.pojos.Service service = businessValidator.requireServiceBelongsToBusinessAndReturn(serviceId, businessId);

    // If no specific employee is requested, fetch slots for any available employee
    if (employeeId == null) {
      return getEmployeeAvailableTimeSlotsForService(businessId, service, date);
    }

    // Validate requested employee
    final Employee employee = employeeValidator.requireEmployeeExistsAndReturn(employeeId);
    EmployeeValidator.requireIsEmployeeOfBusiness(employee, businessId);

    // Check if the employee provides the specified service
    final List<Employee> employeesForService = employeeService.getAllForService(businessId, serviceId);
    if (!employeesForService.contains(employee)) {
      throw new ApplicationException("Employee does not provide the specified service", HttpStatus.BAD_REQUEST);
    }

    // Fetch available slots for the specific employee
    return getEmployeeAvailableTimeSlotsForEmployee(service, date, employeeId);
  }

  private List<StartTime> getEmployeeAvailableTimeSlotsForService(Integer businessId, jooq.tables.pojos.Service service,
                                                                  LocalDate date) {
    final Set<LocalTime> uniqueAvailableSlots = collectUniqueAvailableSlots(
        employeeService.getAllForService(businessId, service.getId()), date, service.getAvgDuration());
    return convertAndSortAvailableSlots(uniqueAvailableSlots);
  }

  private List<StartTime> getEmployeeAvailableTimeSlotsForEmployee(jooq.tables.pojos.Service service,
                                                                   LocalDate date, Integer employeeId) {
    final List<LocalTime> allAvailableSlots = getAvailableTimeSlotsForDate(employeeId, date);
    return filterSlotsByDuration(allAvailableSlots, service.getAvgDuration())
        .stream().map(StartTime::new).toList();
  }
}
