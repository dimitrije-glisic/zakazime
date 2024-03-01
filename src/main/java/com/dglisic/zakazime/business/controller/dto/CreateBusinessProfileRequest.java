package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateBusinessProfileRequest(@NotBlank String name, @NotBlank String phoneNumber, String contactPerson,
                                           @NotBlank String email, @NotBlank String city, @NotBlank String address,
                                           @NotEmpty List<ServiceKind> serviceKinds, Integer yearOfEstablishment) {
}

