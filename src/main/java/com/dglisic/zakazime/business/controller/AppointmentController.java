package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.business.service.impl.TimeSlotManagement;
import com.dglisic.zakazime.common.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import jooq.tables.pojos.Appointment;
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
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;
  private final TimeSlotManagement timeSlotManagement;

  @GetMapping("/{businessId}/{employeeId}/available")
  public ResponseEntity<List<StartTime>> getAvailableTimeSlots(@PathVariable Integer businessId,
                                                               @PathVariable Integer employeeId,
                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                               LocalDate date,
                                                               @RequestParam Integer duration) {
    return ResponseEntity.ok(timeSlotManagement.getAvailableTimeSlots(businessId, employeeId, date, duration));
  }

  @GetMapping("/{businessId}/{employeeId}")
  public ResponseEntity<List<Appointment>> getAppointments(@PathVariable Integer businessId,
                                                           @PathVariable Integer employeeId,
                                                           @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                           LocalDate date) {
    return ResponseEntity.ok(appointmentService.getAppointmentsForDate(businessId, employeeId, date));
  }

  @PostMapping
  public ResponseEntity<MessageResponse> createAppointment(@RequestBody @Valid CreateAppointmentRequest request) {
    appointmentService.createAppointment(request);
    return ResponseEntity.status(201).body(new MessageResponse("Appointment created successfully"));
  }

}
