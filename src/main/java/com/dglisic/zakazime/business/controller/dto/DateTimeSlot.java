package com.dglisic.zakazime.business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record DateTimeSlot(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalDateTime startTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalDateTime endTime
) {
}
