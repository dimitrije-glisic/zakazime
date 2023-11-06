package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.service.BusinessService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BusinessController {

  private final BusinessService businessService;
  private final BusinessProfileMapper businessMapper;

  @PostMapping("/business")
  public ResponseEntity<CreateBusinessProfileResponse> createBusinessProfile(
      @RequestBody CreateBusinessProfileRequest createBusinessProfileRequest) {
    BusinessProfile businessProfile = businessService.createBusinessProfile(createBusinessProfileRequest);
    return ResponseEntity.ok(businessMapper.mapToCreateBusinessProfileResponse(businessProfile));
  }

  @GetMapping("/business")
  public BusinessProfileDTO getBusinessProfileForUser(Principal user) {
    String userEmail = user.getName();
    BusinessProfile businessProfile = businessService.getBusinessProfileForUser(userEmail);
    return businessMapper.mapToBusinessProfileDTO(businessProfile);
  }

}
