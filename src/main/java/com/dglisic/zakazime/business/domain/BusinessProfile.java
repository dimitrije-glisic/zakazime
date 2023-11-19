package com.dglisic.zakazime.business.domain;

import com.dglisic.zakazime.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import model.tables.records.BusinessProfileRecord;
import model.tables.records.BusinessTypeRecord;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
public class BusinessProfile {

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

  public BusinessProfile(model.tables.records.BusinessProfileRecord businessProfileRecord) {
    this.id = businessProfileRecord.getId();
    this.name = businessProfileRecord.getName();
    this.phoneNumber = businessProfileRecord.getPhoneNumber();
    this.city = businessProfileRecord.getCity();
    this.postalCode = businessProfileRecord.getPostalCode();
    this.address = businessProfileRecord.getAddress();
    this.status = businessProfileRecord.getStatus();
    this.createdOn = businessProfileRecord.getCreatedOn();
  }

  public BusinessProfile(BusinessProfileRecord businessProfileRecord, BusinessTypeRecord businessTypeRecord) {
    this(businessProfileRecord);
    this.type = new BusinessType(businessTypeRecord);
  }
}
