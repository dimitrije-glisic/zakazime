package com.dglisic.zakazime.business.controller.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
    @NotNull Integer appointmentId,
    @NotNull Short service,
    @NotNull Short priceQuality,
    @NotNull Short hygiene,
    @NotNull Short ambience,
    String comment
) {
}
