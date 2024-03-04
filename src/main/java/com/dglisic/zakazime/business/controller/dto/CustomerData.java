package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerData(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String email,
    @NotBlank String phone,
    // optional, if provided, it will be used to create a new customer account
    String password
) {
}
