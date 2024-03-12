package com.dglisic.zakazime.business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record SingleServiceAppointmentRequest(
    @NotNull Integer businessId,
    Integer employeeId,
    @NotNull Integer serviceId,
    @NotNull CustomerData customerData,
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime startTime
    ) implements CreateAppointmentRequest {

}

