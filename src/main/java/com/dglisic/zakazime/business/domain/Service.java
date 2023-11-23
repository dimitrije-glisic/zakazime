package com.dglisic.zakazime.business.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.tables.records.BusinessRecord;
import model.tables.records.ServiceCategoryRecord;
import model.tables.records.ServiceRecord;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode(exclude = {"category", "business"})
@ToString
public class Service {
  private final int id;
  private final String name;
  private final String note;
  private final BigDecimal price;
  private final int avgDuration;
  private final String description;
  private final boolean template;

  @Setter
  private Category category;
  @Setter
  private Business business;

  public Service(ServiceRecord serviceRecord, ServiceCategoryRecord serviceCategoryRecord, BusinessRecord businessRecord) {
    this.id = serviceRecord.getId();
    this.category = new Category(serviceCategoryRecord);
    this.business = new Business(businessRecord);
    this.name = serviceRecord.getName();
    this.note = serviceRecord.getNote();
    this.price = serviceRecord.getPrice();
    this.avgDuration = serviceRecord.getAvgDuration();
    this.description = serviceRecord.getDescription();
    this.template = serviceRecord.getTemplate();
  }

  public Service(ServiceRecord serviceRecord, ServiceCategoryRecord serviceCategoryRecord) {
    this.id = serviceRecord.getId();
    this.category = new Category(serviceCategoryRecord);
    this.name = serviceRecord.getName();
    this.note = serviceRecord.getNote();
    this.price = serviceRecord.getPrice();
    this.avgDuration = serviceRecord.getAvgDuration();
    this.description = serviceRecord.getDescription();
    this.template = serviceRecord.getTemplate();
  }
}
