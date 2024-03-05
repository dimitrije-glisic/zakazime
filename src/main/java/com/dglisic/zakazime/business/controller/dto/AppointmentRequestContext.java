package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequestContext(
    @NotNull Integer businessId,
    @NotNull Integer employeeId,
    @NotNull Integer appointmentId
) {
}
