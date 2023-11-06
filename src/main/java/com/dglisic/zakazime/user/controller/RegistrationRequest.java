package com.dglisic.zakazime.user.controller;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String email,
                                  @NotBlank String password, @NotBlank String role) {
}
