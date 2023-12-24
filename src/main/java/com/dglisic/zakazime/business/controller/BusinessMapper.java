package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Business;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessMapper {

  public CreateBusinessProfileResponse mapToCreateBusinessProfileResponse(Business business) {
    return new CreateBusinessProfileResponse(business.getName(),
        business.getPhoneNumber(), business.getCity(), business.getPostalCode(),
        business.getAddress(), business.getStatus());
  }

  public Business mapToBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest) {
    return Business.builder()
        .name(createBusinessProfileRequest.name())
        .phoneNumber(createBusinessProfileRequest.phoneNumber())
        .city(createBusinessProfileRequest.city())
        .postalCode(createBusinessProfileRequest.postalCode())
        .address(createBusinessProfileRequest.address())
        .build();
  }

  public BusinessDTO mapToBusinessProfileDTO(Business business) {
    return BusinessDTO.builder()
        .name(business.getName())
        .phone(business.getPhoneNumber())
        .city(business.getCity())
        .postalCode(business.getPostalCode())
        .address(business.getAddress())
        .status(business.getStatus())
        .type(business.getType().getTitle())
        .ownerName(business.getOwner().getFirstName() + " " + business.getOwner().getLastName())
        .services(ServiceMapperUtil.mapToServiceDTOs(business.getServices()))
        .build();
  }

}
