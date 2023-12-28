package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateServiceRequest(
    @NotBlank String title,
    @NotBlank Integer subcategoryId,
    @NotBlank String note,
    @NotBlank String description,
    @NotNull BigDecimal price,
    @NotNull int avgDuration
) {
}