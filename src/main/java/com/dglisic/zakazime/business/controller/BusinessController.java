package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.common.ApplicationException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//TODO - add DEDICATED ServiceController and/or ServiceService
@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {
  private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

  private final BusinessService businessService;

  @PostMapping
  public ResponseEntity<Business> createBusinessProfile(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest
  ) {
    logger.info("Creating business profile {}", createBusinessProfileRequest);
    Business created = businessService.create(createBusinessProfileRequest);
    return ResponseEntity.ok(created);
  }

  @GetMapping("all")
  public ResponseEntity<List<Business>> getAllBusinesses() {
    logger.info("Getting all businesses");
    List<Business> allBusinesses = businessService.getAll();
    return ResponseEntity.ok(allBusinesses);
  }

  @GetMapping("{businessId}")
  public ResponseEntity<Business> getBusinessProfile(@PathVariable @Valid @NotBlank int businessId) {
    logger.info("Getting business profile for business {}", businessId);
    Business business = businessService.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return ResponseEntity.ok(business);
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

  @GetMapping("services/templates")
  public List<Service> getServiceTemplates(@RequestParam(required = false) @Valid @NotBlank String businessType,
                                           @RequestParam(required = false) @Valid @NotBlank String category,
                                           @RequestParam(required = false) @Valid @NotBlank String subcategory) {
    logger.info("Getting services of type {}, category {}, subcategory {}", businessType, category, subcategory);
    List<Service> serviceTemplates = businessService.searchServiceTemplates(businessType, category, subcategory);
    logger.info("Found {} services of type {}, category {}, subcategory {}", serviceTemplates.size(), businessType, category,
        subcategory);
    return serviceTemplates;
  }

  @GetMapping("{businessId}/services")
  public List<Service> getServicesOfBusiness(@PathVariable @Valid @NotBlank int businessId) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessId);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessId, servicesOfBusiness);
    return servicesOfBusiness;
  }

  @PostMapping("{businessId}/services")
  public void addServicesToBusiness(@PathVariable @Valid @NotBlank int businessId,
                                    @RequestBody @Valid List<CreateServiceRequest> serviceRequests) {
    logger.info("Saving services {} for business {}", serviceRequests, businessId);
    businessService.addServicesToBusiness(serviceRequests, businessId);
  }

  @PutMapping("{businessId}/services/{serviceId}")
  public void updateService(@PathVariable @Valid @NotBlank final int businessId,
                            @PathVariable @Valid @NotBlank final int serviceId,
                            @RequestBody @Valid final UpdateServiceRequest serviceRequest) {
    logger.info("Updating service {} for business {}", serviceRequest, businessId);
    businessService.updateService(businessId, serviceId, serviceRequest);
  }

}
