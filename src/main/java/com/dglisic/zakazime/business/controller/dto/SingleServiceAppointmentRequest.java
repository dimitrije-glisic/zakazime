package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record SingleServiceAppointmentRequest(
    @NotNull Integer businessId,
    Integer employeeId,
    @NotNull Integer serviceId,
    @NotNull CustomerData customerData,
    @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime startTime,
    @NotNull @Positive Integer duration
) implements CreateAppointmentRequest {

}

