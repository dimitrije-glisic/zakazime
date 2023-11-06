package com.dglisic.zakazime.controller;

import jakarta.validation.constraints.NotBlank;

public record CreateBusinessProfileRequest(@NotBlank String businessName, @NotBlank String phoneNumber, @NotBlank String city,
                                           @NotBlank String postalCode, @NotBlank String address, @NotBlank String ownerEmail) {
}
