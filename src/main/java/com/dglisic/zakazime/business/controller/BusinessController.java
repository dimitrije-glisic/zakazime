package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.service.BusinessService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO - add DEDICATED ServiceController and/or ServiceService
@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {
  private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

  private final BusinessService businessService;
  private final BusinessMapper businessMapper;
  private final ServiceMapper serviceMapper;

  @PostMapping
  public ResponseEntity<Business> createBusinessProfile(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest
  ) {
    logger.info("Creating business profile {}", createBusinessProfileRequest);
    Business toBeCreated = businessMapper.map(createBusinessProfileRequest);
    Business created = businessService.createBusinessProfile(toBeCreated);
    return ResponseEntity.ok(created);
  }

  @GetMapping
  public Business getBusinessProfileForUser(Principal user) {
    logger.info("Getting business profile for user {}", user.getName());
    String userEmail = user.getName();
    return businessService.getBusinessProfileForUser(userEmail);
  }

  @GetMapping("business-types")
  public List<BusinessType> getBusinessTypes() {
    logger.info("Getting business types");
    return businessService.getBusinessTypes();
  }

  @GetMapping("types/{businessType}/services")
  public List<Service> getServicesOfType(@PathVariable String businessType) {
    logger.info("Getting services of type {}", businessType);
    List<Service> serviceTemplatesOfType = businessService.getServiceTemplatesOfType(businessType);
    logger.info("Found {} services of type {}", serviceTemplatesOfType.size(), businessType);
    return serviceTemplatesOfType;
  }

  @GetMapping("{businessId}/services")
  public List<Service> getServicesForBusiness(@PathVariable @Valid @NotBlank int businessId) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessId);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessId, servicesOfBusiness);
    return servicesOfBusiness;
  }


  // todo - check if logged in user is owner of business (or admin) before saving
  @PostMapping("{businessId}/services")
  public void saveServicesForBusiness(@PathVariable @Valid @NotBlank int businessId,
                                      @RequestBody List<CreateServiceRequest> serviceRequests) {
    logger.info("Saving services {} for business {}", serviceRequests, businessId);
    List<Service> servicesToBeSaved = serviceRequests.stream()
        .map(serviceMapper::map)
        .toList();
    businessService.saveServicesForBusiness(servicesToBeSaved, businessId);
  }

  // todo - check if logged in user is owner of business (or admin) before updating
  @PutMapping("{businessId}/services/{serviceId}")
  public void updateService(@PathVariable @Valid @NotBlank int businessId,
                            @PathVariable @Valid @NotBlank int serviceId,
                            @RequestBody CreateServiceRequest serviceRequest) {
    logger.info("Updating service {} for business {}", serviceRequest, businessId);
    Service service = serviceMapper.map(serviceRequest);
    businessService.updateService(serviceId, service, businessId);
  }

}
