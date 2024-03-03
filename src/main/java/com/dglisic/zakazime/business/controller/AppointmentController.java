package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.StartTime;
import com.dglisic.zakazime.business.service.AppointmentService;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/appointments")
@AllArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  @GetMapping("/free-slots/{businessId}/{employeeId}")
  public List<StartTime> getAvailableTimeSlots(@PathVariable Integer businessId,
                                               @PathVariable Integer employeeId,
                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
    return appointmentService.getAvailableTimeSlots(businessId, employeeId, date);
  }

}
