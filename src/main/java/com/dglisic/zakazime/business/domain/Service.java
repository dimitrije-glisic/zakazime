package com.dglisic.zakazime.business.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
@ToString
public class Service {

  private final int id;
  private final int categoryId;
  private final String name;
  private final String note;
  private final BigDecimal price;
  private final int avgDuration;
  private final String description;
  private final boolean template;

  @Setter
  private String categoryName;
  @Setter
  private Category category;
  @Setter
  private Business business;


  public Service(model.tables.records.ServiceRecord serviceRecord) {
    this.id = serviceRecord.getId();
    this.categoryId = serviceRecord.getCategoryId();
    this.name = serviceRecord.getName();
    this.note = serviceRecord.getNote();
    this.price = serviceRecord.getPrice();
    this.avgDuration = serviceRecord.getAvgDuration();
    this.description = serviceRecord.getDescription();
    this.template = serviceRecord.getTemplate();
  }

}
