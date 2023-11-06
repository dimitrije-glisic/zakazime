package com.dglisic.zakazime.business.controller;

import jakarta.validation.constraints.NotBlank;

record CreateBusinessProfileResponse(@NotBlank String businessName, @NotBlank String phoneNumber, @NotBlank String city,
                                     @NotBlank String postalCode, @NotBlank String address, @NotBlank String status) {
}

