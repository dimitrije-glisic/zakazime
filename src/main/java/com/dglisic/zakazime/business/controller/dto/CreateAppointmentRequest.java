package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateAppointmentRequest(
    @NotNull Integer businessId,
    @NotNull Integer employeeId,
    @NotNull Integer serviceId,
    @NotNull CustomerData customerData,
    @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime startTime,
    @NotNull Integer duration
) {
}
