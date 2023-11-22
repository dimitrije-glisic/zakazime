package com.dglisic.zakazime.business.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import model.tables.records.ServiceCategoryRecord;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Category {
  private final int id;
  private final String name;

  public Category(ServiceCategoryRecord categoryRecord) {
    this.id = categoryRecord.getId();
    this.name = categoryRecord.getName();
  }
}
