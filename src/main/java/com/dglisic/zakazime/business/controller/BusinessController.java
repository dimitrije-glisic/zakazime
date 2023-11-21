package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.service.BusinessService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BusinessController {
  private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

  private final BusinessService businessService;
  private final BusinessMapper businessMapper;
  private final ServiceMapper serviceMapper;

  @PostMapping("/business")
  public ResponseEntity<CreateBusinessProfileResponse> createBusinessProfile(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest) {
    logger.info("Creating business profile {}", createBusinessProfileRequest);
    Business business = businessService.createBusinessProfile(createBusinessProfileRequest);
    return ResponseEntity.ok(businessMapper.mapToCreateBusinessProfileResponse(business));
  }

  @GetMapping("/business")
  public BusinessProfileDTO getBusinessProfileForUser(Principal user) {
    String userEmail = user.getName();
    Business business = businessService.getBusinessProfileForUser(userEmail);
    return businessMapper.mapToBusinessProfileDTO(business);
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

  @GetMapping("/business/{businessName}/services")
  public List<Service> getServicesForBusiness(@PathVariable @Valid @NotBlank String businessName) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessName);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessName, servicesOfBusiness);
    return servicesOfBusiness;
  }

  @PostMapping("/business/{businessName}/services")
  public void saveServicesForBusiness(@PathVariable @Valid @NotBlank String businessName,
                                      @RequestBody List<CreateServiceRequest> serviceRequests) {
    logger.info("Saving services {} for business {}", serviceRequests, businessName);
    List<Service> servicesToBeSaved = serviceRequests.stream()
        .map(serviceMapper::mapToService)
        .toList();
    businessService.saveServices(servicesToBeSaved, businessName);
  }

}
