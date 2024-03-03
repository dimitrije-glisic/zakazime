package com.dglisic.zakazime.business.controller.dto;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
    Integer businessId,
    Integer employeeId,
    Integer serviceId,
    Integer customerId,
    LocalDateTime start,
    Integer duration
) {
}
