package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateServiceRequest(
    @NotBlank String title,
    @NotBlank String note,
    @NotBlank String description,
    @NotNull Integer subcategoryId,
    @NotNull @PositiveOrZero Integer avgDuration,
    @NotNull @PositiveOrZero BigDecimal price
) {
}
