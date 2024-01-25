package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePredefinedCategoryRequest(@NotBlank String title, @NotNull Integer businessTypeId) {
}
