package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.AppointmentRequestContext;
import com.dglisic.zakazime.business.controller.dto.AppointmentRichObject;
import com.dglisic.zakazime.business.controller.dto.CreateBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.DeleteBlockTimeRequest;
import com.dglisic.zakazime.business.controller.dto.MultiServiceAppointmentRequest;
import com.dglisic.zakazime.business.controller.dto.SingleServiceAppointmentRequest;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.common.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.EmployeeBlockTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Slf4j
public class AppointmentController {

  private final AppointmentService appointmentService;

  @PostMapping
  public ResponseEntity<MessageResponse> createSingleServiceAppointment(
      @RequestBody @Valid SingleServiceAppointmentRequest request) {
    appointmentService.createSingleServiceAppointment(request);
    return ResponseEntity.status(201).body(new MessageResponse("Appointment created successfully"));
  }

  @PostMapping("/multi")
  public ResponseEntity<MessageResponse> createMultiServiceAppointment(
      @RequestBody @Valid MultiServiceAppointmentRequest request) {
    appointmentService.createMultiServiceAppointment(request);
    return ResponseEntity.status(201).body(new MessageResponse("Appointment created successfully"));
  }

  @GetMapping("/{businessId}/{employeeId}")
  public ResponseEntity<List<Appointment>> getAppointments(@PathVariable Integer businessId,
                                                           @PathVariable Integer employeeId,
                                                           @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                           LocalDate date) {
    return ResponseEntity.ok(appointmentService.getAppointmentsForDate(businessId, employeeId, date));
  }

  @GetMapping("/{businessId}/all")
  public ResponseEntity<List<Appointment>> getAllAppointments(@PathVariable Integer businessId) {
    return ResponseEntity.ok(appointmentService.getAllAppointments(businessId));
  }

  @GetMapping("/{businessId}/all-full-info")
  public ResponseEntity<List<AppointmentRichObject>> getAllAppointmentFullInfo(@PathVariable Integer businessId,
                                                                               @RequestParam
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                                                               LocalDate fromDate) {
    return ResponseEntity.ok(appointmentService.getAllAppointmentsFullInfoFromDate(businessId, fromDate));
  }

  @GetMapping("/{businessId}/{appointmentId}/full")
  public ResponseEntity<AppointmentRichObject> getAppointmentFullInfo(@PathVariable Integer businessId,
                                                                      @PathVariable Integer appointmentId) {
    return ResponseEntity.ok(appointmentService.requireAppointmentFullInfo(businessId, appointmentId));
  }

  @GetMapping("/for-user/{userId}")
  public ResponseEntity<List<AppointmentRichObject>> getAppointmentsForUser(@PathVariable Integer userId) {
    return ResponseEntity.ok(appointmentService.getAppointmentsForUser(userId));
  }

  @GetMapping("/{businessId}/with-reviews")
  public ResponseEntity<List<AppointmentRichObject>> getAllAppointmentsWithReviewsForBusiness(@PathVariable Integer businessId) {
    return ResponseEntity.ok(appointmentService.getAllAppointmentsWithReviewsForBusiness(businessId));
  }

  @PostMapping("/confirm")
  public ResponseEntity<MessageResponse> confirmAppointment(@RequestBody @Valid AppointmentRequestContext request) {
    log.debug("Confirming appointment: {}", request);
    appointmentService.confirmAppointment(request);
    return ResponseEntity.status(200).body(new MessageResponse("Appointment confirmed successfully"));
  }

  @PostMapping("/cancel")
  public ResponseEntity<MessageResponse> cancelAppointment(@RequestBody @Valid AppointmentRequestContext request) {
    log.debug("Cancelling appointment: {}", request);
    appointmentService.cancelAppointment(request);
    return ResponseEntity.status(200).body(new MessageResponse("Appointment cancelled successfully"));
  }

  @PostMapping("/complete")
  public ResponseEntity<MessageResponse> completeAppointment(@RequestBody @Valid AppointmentRequestContext request) {
    log.debug("Completing appointment: {}", request);
    appointmentService.completeAppointment(request);
    return ResponseEntity.status(200).body(new MessageResponse("Appointment completed successfully"));
  }

  @PostMapping("/no-show")
  public ResponseEntity<MessageResponse> noShowAppointment(@RequestBody @Valid AppointmentRequestContext request) {
    appointmentService.noShowAppointment(request);
    return ResponseEntity.status(201).body(new MessageResponse("Appointment marked as no-show successfully"));
  }

  @PostMapping("/block-time")
  public ResponseEntity<MessageResponse> createBlockTime(@RequestBody @Valid CreateBlockTimeRequest request) {
    appointmentService.createBlockTime(request);
    return ResponseEntity.status(201).body(new MessageResponse("Block time created successfully"));
  }

  @DeleteMapping("/block-time")
  public ResponseEntity<MessageResponse> deleteBlockTime(@RequestBody @Valid DeleteBlockTimeRequest request) {
    appointmentService.deleteBlockTime(request);
    return ResponseEntity.status(201).body(new MessageResponse("Block time deleted successfully"));
  }

  @GetMapping("{businessId}/{employeeId}/block-time")
  public ResponseEntity<List<EmployeeBlockTime>> getBlockTime(@PathVariable Integer businessId,
                                                              @PathVariable Integer employeeId,
                                                              @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                                                              LocalDate date) {
    return ResponseEntity.ok(appointmentService.getBlockTimeForDate(businessId, employeeId, date));
  }

  @GetMapping("/{businessId}/customer/{customerId}")
  public ResponseEntity<List<AppointmentRichObject>> getAppointmentsForCustomer(@PathVariable Integer businessId,
                                                                                @PathVariable Integer customerId) {
    return ResponseEntity.ok(appointmentService.getAppointmentsForCustomer(businessId, customerId));
  }


}