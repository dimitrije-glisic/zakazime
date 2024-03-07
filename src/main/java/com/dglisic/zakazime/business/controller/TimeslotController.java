package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.MultiServiceEmplAvailabilityRequest;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.service.impl.TimeSlotManagement;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time-slots")
@AllArgsConstructor
public class TimeslotController {

  private final TimeSlotManagement timeSlotManagement;

  @GetMapping("/{businessId}/available")
  public ResponseEntity<List<StartTime>> getAvailableTimeSlots(@PathVariable Integer businessId,
                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                               LocalDate date,
                                                               @RequestParam Integer duration) {
    return ResponseEntity.ok(timeSlotManagement.findAvailableTimeSlotsForBusiness(businessId, date, duration));
  }

  @GetMapping("/{businessId}/{employeeId}/available")
  public ResponseEntity<List<StartTime>> getAvailableTimeSlots(@PathVariable Integer businessId,
                                                               @PathVariable Integer employeeId,
                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                               LocalDate date,
                                                               @RequestParam Integer duration) {
    return ResponseEntity.ok(timeSlotManagement.getEmployeeAvailableTimeSlots(businessId, employeeId, date, duration));
  }

  @PostMapping("/available/multi")
  public ResponseEntity<List<StartTime>> findAllPossibleStartTimesNew(
      @RequestBody @Valid MultiServiceEmplAvailabilityRequest request) {
    Set<LocalTime> allPossibleStartTimesNew = timeSlotManagement.findAllPossibleStartTimes(request);
    return ResponseEntity.ok(allPossibleStartTimesNew.stream().map(StartTime::new).toList());
  }

}