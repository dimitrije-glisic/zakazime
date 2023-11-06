package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

  public CreateBusinessProfileResponse mapToCreateBusinessProfileResponse(BusinessProfile businessProfile) {
    return new CreateBusinessProfileResponse(businessProfile.getName(),
        businessProfile.getPhoneNumber(), businessProfile.getCity(), businessProfile.getPostalCode(),
        businessProfile.getAddress(), businessProfile.getStatus());
  }

  public BusinessProfile mapToBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest) {
    return BusinessProfile.builder()
        .name(createBusinessProfileRequest.businessName())
        .phoneNumber(createBusinessProfileRequest.phoneNumber())
        .city(createBusinessProfileRequest.city())
        .postalCode(createBusinessProfileRequest.postalCode())
        .address(createBusinessProfileRequest.address())
        .build();
  }

  public BusinessProfileDTO mapToBusinessProfileDTO(BusinessProfile businessProfile) {
    return BusinessProfileDTO.builder()
        .name(businessProfile.getName())
        .phoneNumber(businessProfile.getPhoneNumber())
        .city(businessProfile.getCity())
        .postalCode(businessProfile.getPostalCode())
        .address(businessProfile.getAddress())
        .status(businessProfile.getStatus())
        .ownerName(businessProfile.getOwner().getFirstName() + " " + businessProfile.getOwner().getLastName())
        .build();
  }
}
