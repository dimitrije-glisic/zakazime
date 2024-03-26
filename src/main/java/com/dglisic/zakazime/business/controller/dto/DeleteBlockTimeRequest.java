package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DeleteBlockTimeRequest(
    @NotNull Integer businessId,
    @NotNull Integer employeeId,
    @NotNull Integer blockTimeId
) {
}
