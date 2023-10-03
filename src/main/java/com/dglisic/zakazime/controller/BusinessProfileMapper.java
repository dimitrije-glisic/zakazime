package com.dglisic.zakazime.controller;

import model.tables.records.BusinessProfileRecord;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

  public BusinessProfileRecord mapToBusinessProfile(BusinessProfileDTO businessProfileDTO) {
    BusinessProfileRecord businessProfile = new BusinessProfileRecord();
    businessProfile.setName(businessProfileDTO.businessName());
    businessProfile.setPhoneNumber(businessProfileDTO.phoneNumber());
    businessProfile.setAddress(businessProfileDTO.address());
    return businessProfile;
  }

}
