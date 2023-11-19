package com.dglisic.zakazime.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import model.tables.records.BusinessTypeRecord;

@AllArgsConstructor
@Getter
public class BusinessType {
  private final int id;
  private final String name;

  public BusinessType(BusinessTypeRecord businessTypeRecord) {
    this.id = businessTypeRecord.getId();
    this.name = businessTypeRecord.getName();
  }
}
