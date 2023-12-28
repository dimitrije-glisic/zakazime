package com.dglisic.zakazime.business.controller.dto;

import java.math.BigDecimal;

public record UpdateServiceRequest(
    String title,
    Integer subcategoryId,
    String note,
    String description,
    BigDecimal price,
    Integer avgDuration
) {
}
