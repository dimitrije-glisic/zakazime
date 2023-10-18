package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.service.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record UserRegistrationDTO(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String email,
                           @NotBlank String password, @NotNull UserType userType) {
}
