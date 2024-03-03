package com.dglisic.zakazime.business.controller.dto;

import java.time.LocalDateTime;

public record CreateBlockTimeRequest(
    Integer businessId,
    Integer employeeId,
    LocalDateTime start,
    Integer duration
) {
}
