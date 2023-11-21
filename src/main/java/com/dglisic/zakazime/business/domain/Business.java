package com.dglisic.zakazime.business.domain;

import com.dglisic.zakazime.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import model.tables.records.BusinessRecord;
import model.tables.records.BusinessTypeRecord;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
public class Business {

  private final int id;
  private final String name;
  private final String phoneNumber;
  private final String city;
  private final String postalCode;
  private final String address;

  @Setter
  private BusinessType type;
  @Setter
  private User owner;
  @Setter
  private LocalDateTime createdOn;
  @Setter
  private String status;
  @Setter
  private List<Service> services;

  public Business(BusinessRecord businessRecord) {
    this.id = businessRecord.getId();
    this.name = businessRecord.getName();
    this.phoneNumber = businessRecord.getPhoneNumber();
    this.city = businessRecord.getCity();
    this.postalCode = businessRecord.getPostalCode();
    this.address = businessRecord.getAddress();
    this.status = businessRecord.getStatus();
    this.createdOn = businessRecord.getCreatedOn();
  }

  public Business(BusinessRecord businessRecord, BusinessTypeRecord businessTypeRecord) {
    this(businessRecord);
    this.type = new BusinessType(businessTypeRecord);
  }

}
