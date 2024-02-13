package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBusinessProfileRequest(@NotBlank String name, @NotBlank String phoneNumber, @NotBlank String city,
                                           @NotBlank String postalCode, @NotBlank String address) {
}

