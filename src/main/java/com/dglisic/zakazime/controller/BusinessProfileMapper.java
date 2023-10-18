package com.dglisic.zakazime.controller;

import model.tables.records.BusinessProfileRecord;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

  public BusinessProfileRecord mapToBusinessProfile(BusinessProfileDTO businessProfileDTO) {
    BusinessProfileRecord businessProfile = new BusinessProfileRecord();
    businessProfile.setName(businessProfileDTO.businessName());
    businessProfile.setPhoneNumber(businessProfileDTO.phoneNumber());
    businessProfile.setCity(businessProfileDTO.city());
    businessProfile.setPostalCode(businessProfileDTO.postalCode());
    businessProfile.setAddress(businessProfileDTO.address());
    return businessProfile;
  }

}
