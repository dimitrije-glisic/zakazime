package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record AvailabilityRequest(
    @NotNull Integer businessId,
    @NotNull Integer serviceId,
    Integer employeeId,
    @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDate date) {
}
