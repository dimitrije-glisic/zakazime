package com.dglisic.zakazime.business.controller;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ServiceDTO(int id, String name, String note, BigDecimal price, int avgDuration, String description,
                         boolean template, String subCategoryId, String businessId) {

}
