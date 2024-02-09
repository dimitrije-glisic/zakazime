package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDefinedCategoryRequest(@NotBlank String title) {
}
