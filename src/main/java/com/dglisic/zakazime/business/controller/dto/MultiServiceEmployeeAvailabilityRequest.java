package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record MultiServiceEmployeeAvailabilityRequest(
    @NotNull Integer businessId,
    @NotEmpty List<EmployeeServiceIdPair> employeeServicePairs,
    @NotNull LocalDate date
) {
}
