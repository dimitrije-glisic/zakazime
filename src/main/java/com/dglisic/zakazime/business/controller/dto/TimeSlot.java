package com.dglisic.zakazime.business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TimeSlot(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime startTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime endTime
) {
}
