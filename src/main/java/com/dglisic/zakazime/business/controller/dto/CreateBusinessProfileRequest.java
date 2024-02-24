package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBusinessProfileRequest(@NotBlank String name, @NotBlank String phoneNumber, String contactPerson,
                                           @NotBlank String email, @NotBlank String city, @NotBlank String address,
                                           ServiceKind serviceKind, Integer yearOfEstablishment) {
}

