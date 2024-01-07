package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// consider multiple business types for a business
public record CreateBusinessProfileRequest(@NotBlank String name, @NotNull Integer businessTypeId, @NotBlank String phoneNumber, @NotBlank String city,
                                           @NotBlank String postalCode, @NotBlank String address) {
}

