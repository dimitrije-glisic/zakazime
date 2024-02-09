package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<Business> getBusinessProfile(@PathVariable @Valid @NotBlank Integer businessId) {
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

  @GetMapping("{businessId}/services")
  public List<Service> getServices(@PathVariable @Valid @NotBlank Integer businessId) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessId);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessId, servicesOfBusiness);
    return servicesOfBusiness;
  }

  @PostMapping("{businessId}/services")
  public ResponseEntity<List<Service>> addServicesToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                             @RequestBody @Valid List<CreateServiceRequest> serviceRequests) {
    logger.info("Saving services {} for business {}", serviceRequests, businessId);
    List<Service> services = businessService.addServicesToBusiness(serviceRequests, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(services);
  }

  @PostMapping("{businessId}/single-service")
  public ResponseEntity<Service> addServiceToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                      @RequestBody @Valid CreateServiceRequest serviceRequest) {
    logger.info("Saving service {} for business {}", serviceRequest, businessId);
    Service service = businessService.addServicesToBusiness(serviceRequest, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(service);
  }

  @PutMapping("{businessId}/services/{serviceId}")
  public ResponseEntity<MessageResponse> updateService(@PathVariable @Valid @NotBlank final Integer businessId,
                                                       @PathVariable @Valid @NotBlank final Integer serviceId,
                                                       @RequestBody @Valid final UpdateServiceRequest updateRequest) {
    logger.info("Updating service {} for business {}", updateRequest, businessId);
    businessService.updateService(businessId, serviceId, updateRequest);
    return ResponseEntity.ok(new MessageResponse("Service updated successfully"));
  }

  @DeleteMapping("{businessId}/services/{serviceId}")
  public ResponseEntity<MessageResponse> deleteService(@PathVariable @Valid @NotBlank final Integer businessId,
                                                       @PathVariable @Valid @NotBlank final Integer serviceId) {
    logger.info("Deleting service {} for business {}", serviceId, businessId);
    businessService.deleteService(businessId, serviceId);
    return ResponseEntity.ok(new MessageResponse("Service deleted successfully"));
  }

  //====================================================================================================
  // NEW ENDPOINTS
  //====================================================================================================

  // endpoint for adding new mapping: predefined_category - business
  // mapping between the two is important for searching businesses by predefined category

  @PostMapping("{businessId}/predefined-categories")
  public ResponseEntity<MessageResponse> linkBusinessWithPredefinedCategories(@PathVariable @Valid @NotBlank Integer businessId,
                                                                              @RequestBody @Valid List<Integer> categoryIds) {
    logger.info("Saving categories {} for business {}", categoryIds, businessId);
    businessService.linkPredefinedCategories(categoryIds, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Categories saved successfully"));
  }

  // get all predefined categories for business
  @GetMapping("{businessId}/predefined-categories")
  public ResponseEntity<List<PredefinedCategory>> getPredefinedCategoriesOfBusiness(
      @PathVariable @Valid @NotBlank Integer businessId) {
    List<PredefinedCategory> predefinedCategoriesOfBusiness = businessService.getPredefinedCategories(businessId);
    logger.info("Getting PredefinedCategories ({}) of business {}: {}", predefinedCategoriesOfBusiness.size(), businessId,
        predefinedCategoriesOfBusiness);
    return ResponseEntity.ok(predefinedCategoriesOfBusiness);
  }

  // =========================
  // User defined categories
  // =========================

  @GetMapping("{businessId}/categories")
  public ResponseEntity<List<UserDefinedCategory>> getUserDefinedCategoriesOfBusiness(
      @PathVariable @Valid @NotBlank Integer businessId) {
    List<UserDefinedCategory> userDefinedCategoriesOfBusiness = businessService.getUserDefinedCategories(businessId);
    logger.info("Getting UserDefinedCategories ({}) of business {}: {}", userDefinedCategoriesOfBusiness.size(), businessId,
        userDefinedCategoriesOfBusiness);
    return ResponseEntity.ok(userDefinedCategoriesOfBusiness);
  }

  @PostMapping("{businessId}/categories")
  public ResponseEntity<MessageResponse> addUserDefinedCategoryToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                                          @RequestBody
                                                                          @Valid CreateUserDefinedCategoryRequest categoryRequest) {
    logger.info("Saving category {} for business {}", categoryRequest, businessId);
    businessService.createUserDefinedCategory(categoryRequest, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Category saved successfully"));
  }

  @PutMapping("{businessId}/categories/{categoryId}")
  public ResponseEntity<MessageResponse> updateUserDefinedCategory(@PathVariable @Valid @NotBlank final Integer businessId,
                                                                   @PathVariable @Valid @NotBlank final Integer categoryId,
                                                                   @RequestBody @Valid
                                                                   final UpdateUserDefinedCategoryRequest categoryRequest) {
    logger.info("Updating category {} for business {}", categoryRequest, businessId);
    businessService.updateUserDefinedCategory(businessId, categoryId, categoryRequest);
    return ResponseEntity.ok(new MessageResponse("Category updated successfully"));
  }

  // =========================

  // =========================
  // Services
  // =========================


}
