package com.dglisic.zakazime.admin.controller;

import jakarta.validation.constraints.NotBlank;

public record CreateBusinessTypeRequest(@NotBlank String title) {
}
