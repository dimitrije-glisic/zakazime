package com.dglisic.zakazime.user.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfoRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String phone, @NotBlank String email, @Nullable String password) {
}
