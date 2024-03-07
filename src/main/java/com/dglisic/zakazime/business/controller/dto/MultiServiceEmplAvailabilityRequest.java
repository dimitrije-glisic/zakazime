package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record MultiServiceEmplAvailabilityRequest(
    @NotNull Integer businessId,
    @NotEmpty List<EmployeeServiceIdPair> employeeServicePairs,
    @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime startTime
) implements CreateAppointmentRequest {
}
