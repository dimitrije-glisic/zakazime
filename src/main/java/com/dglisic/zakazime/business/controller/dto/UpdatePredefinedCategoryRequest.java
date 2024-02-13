package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePredefinedCategoryRequest(@NotBlank String title) {
}
