package com.dglisic.zakazime.controller;

import model.tables.records.BusinessProfileRecord;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

  public BusinessProfileRecord mapToBusinessProfile(BusinessProfileRegistrationDTO businessProfileDTO) {
    BusinessProfileRecord businessProfile = new BusinessProfileRecord();
    businessProfile.setName(businessProfileDTO.businessName());
    businessProfile.setPhoneNumber(businessProfileDTO.phoneNumber());
    businessProfile.setCity(businessProfileDTO.city());
    businessProfile.setPostalCode(businessProfileDTO.postalCode());
    businessProfile.setAddress(businessProfileDTO.address());
    return businessProfile;
  }

  public BusinessProfileDTO mapToBusinessProfileDTO(BusinessProfileRecord businessProfileRecord) {
    return  BusinessProfileDTO.builder()
        .businessName(businessProfileRecord.getName())
        .phoneNumber(businessProfileRecord.getPhoneNumber())
        .city(businessProfileRecord.getCity())
        .postalCode(businessProfileRecord.getPostalCode())
        .address(businessProfileRecord.getAddress())
        .status(businessProfileRecord.getStatus())
        .build();
  }
}
