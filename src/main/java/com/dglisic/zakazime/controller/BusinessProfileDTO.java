package com.dglisic.zakazime.controller;

import jakarta.validation.constraints.NotBlank;

public record BusinessProfileDTO(@NotBlank String businessName, @NotBlank String phoneNumber,
                                 @NotBlank String address) {
}
