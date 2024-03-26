package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.MultiServiceEmployeeAvailabilityRequest;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.service.impl.TimeSlotManagement;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TimeslotController {

  private final TimeSlotManagement timeSlotManagement;

//  @GetMapping("/{businessId}/available")
//  public ResponseEntity<List<StartTime>> getAvailableTimeSlotsByDuration(@PathVariable Integer businessId,
//                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
//                                                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//                                                               LocalDate date,
//                                                               @RequestParam Integer duration) {
//    return ResponseEntity.ok(timeSlotManagement.findAvailableTimeSlotsForBusiness(businessId, date, duration));
//  }

  @GetMapping("/{businessId}/available")
  public ResponseEntity<List<StartTime>> getAvailableTimeSlotsByDuration(@PathVariable Integer businessId,
                                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                                         LocalDate date,
                                                                         @RequestParam Integer duration,
                                                                         @RequestParam(required = false) Integer employeeId) {
    return ResponseEntity.ok(timeSlotManagement.getEmployeeAvailableTimeSlots(employeeId, date, duration));
  }

  @GetMapping("/{businessId}/{serviceId}/available")
  public ResponseEntity<List<StartTime>> getAvailableTimeSlotsForService(@PathVariable Integer businessId,
                                                                         @PathVariable Integer serviceId,
                                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                                         LocalDate date,
                                                                         @RequestParam(required = false) Integer employeeId) {
    log.debug("Getting available time slots for businessId: {}, serviceId: {}, date: {}, employeeId: {}", businessId, serviceId, date, employeeId);
    ResponseEntity<List<StartTime>> ok =
        ResponseEntity.ok(timeSlotManagement.getEmployeeAvailableTimeSlotsNew(businessId, serviceId, date, employeeId));
    log.debug("Returning available time slots: {}", ok);
    return ok;
  }

  @PostMapping("/available/multi")
  public ResponseEntity<List<StartTime>> findAllPossibleStartTimesNew(
      @RequestBody @Valid MultiServiceEmployeeAvailabilityRequest request) {
    Set<LocalTime> allPossibleStartTimesNew = timeSlotManagement.findAllPossibleStartTimes(request);
    return ResponseEntity.ok(allPossibleStartTimesNew.stream().map(StartTime::new).toList());
  }

}