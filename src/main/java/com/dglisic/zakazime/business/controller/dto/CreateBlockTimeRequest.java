package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateBlockTimeRequest(
    @NotNull Integer businessId,
    @NotNull Integer employeeId,
    @NotNull LocalDateTime start,
    @NotNull Integer duration
) {
}
