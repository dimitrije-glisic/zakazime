package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateServiceRequest(
    @NotBlank String title,
    @NotBlank String description,
    @NotNull Integer categoryId,
    @NotNull @Positive BigDecimal price,
    @NotNull @Positive Integer avgDuration
) {
}
