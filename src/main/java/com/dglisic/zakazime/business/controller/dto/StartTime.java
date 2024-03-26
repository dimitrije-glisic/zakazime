package com.dglisic.zakazime.business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record StartTime(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime startTime
) {
}
