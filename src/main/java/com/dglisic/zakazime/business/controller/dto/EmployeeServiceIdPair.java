package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;

public record EmployeeServiceIdPair(
    // if employeeId is null, it means that the service can be performed by any employee
    Integer employeeId,
    @NotNull Integer serviceId
) {
}
