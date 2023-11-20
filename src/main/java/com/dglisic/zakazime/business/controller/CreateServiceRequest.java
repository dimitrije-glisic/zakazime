package com.dglisic.zakazime.business.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

record CreateServiceRequest(
    @NotBlank String name,
    @NotBlank String categoryName,
    @NotBlank String note,
    @NotBlank String description,
    @NotNull BigDecimal price,
    @NotNull int avgDuration
) {
}
