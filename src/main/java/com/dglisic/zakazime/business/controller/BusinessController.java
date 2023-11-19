package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.service.BusinessService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BusinessController {

  private final BusinessService businessService;
  private final BusinessMapper businessMapper;

  @PostMapping("/business")
  public ResponseEntity<CreateBusinessProfileResponse> createBusinessProfile(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest) {
    BusinessProfile businessProfile = businessService.createBusinessProfile(createBusinessProfileRequest);
    return ResponseEntity.ok(businessMapper.mapToCreateBusinessProfileResponse(businessProfile));
  }

  @GetMapping("/business")
  public BusinessProfileDTO getBusinessProfileForUser(Principal user) {
    String userEmail = user.getName();
    BusinessProfile businessProfile = businessService.getBusinessProfileForUser(userEmail);
    return businessMapper.mapToBusinessProfileDTO(businessProfile);
  }

  @GetMapping("/business/types")
  public List<String> getBusinessTypes() {
    return businessService.getBusinessTypes().stream()
        .map(BusinessType::getName)
        .toList();
  }

  @GetMapping("/business/types/{type}/services")
  public List<Service> getServicesForType(@PathVariable String type) {
    return businessService.getServicesForType(type);
  }

}
