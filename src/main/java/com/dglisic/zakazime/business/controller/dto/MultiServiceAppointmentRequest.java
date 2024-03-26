package com.dglisic.zakazime.business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record MultiServiceAppointmentRequest(
    @NotNull Integer businessId,
    @NotEmpty List<EmployeeServiceIdPair> employeeServicePairs,
    @NotNull CustomerData customerData,
    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm") @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDateTime startTime
) implements CreateAppointmentRequest {
}
