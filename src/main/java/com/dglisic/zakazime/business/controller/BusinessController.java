package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.BusinessRichObject;
import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.ImageType;
import com.dglisic.zakazime.business.controller.dto.ImageUploadResponse;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {
  private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

  private final BusinessService businessService;

  @GetMapping("/{city}/{businessName}")
  public ResponseEntity<BusinessRichObject> getRichBusinessData(@PathVariable @Valid @NotBlank String city,
                                                                @PathVariable @Valid @NotBlank String businessName) {
    logger.info("Getting business profile for business {} in city {}", businessName, city);
    final var denormalizeCity = city.replace("-", " ");
    final var denormalizeBusinessName = businessName.replace("-", " ");
    final BusinessRichObject business = businessService.getCompleteBusinessData(denormalizeCity, denormalizeBusinessName);
    return ResponseEntity.ok(business);
  }

  @GetMapping("/all/{city}")
  public ResponseEntity<List<Business>> getAllBusinessesInCity(@PathVariable @Valid @NotBlank String city) {
    logger.info("Getting all businesses in city {}", city);
    List<Business> allBusinesses = businessService.getAllBusinessesInCity(city);
    return ResponseEntity.ok(allBusinesses);
  }

  @GetMapping("/search")
  public ResponseEntity<List<Business>> searchBusinesses(
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String businessType,
      @RequestParam(required = false) String category
  ) {
    logger.info("Searching businesses with city: {}, businessType: {}, category: {}", city, businessType, category);
    List<Business> businesses = businessService.searchBusinesses(city, businessType, category);
    return ResponseEntity.ok(businesses);
  }

  @PostMapping
  public ResponseEntity<Business> createBusiness(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest
  ) {
    logger.info("Creating business profile {}", createBusinessProfileRequest);
    Business created = businessService.create(createBusinessProfileRequest);
    return ResponseEntity.ok(created);
  }

  @PostMapping("/{businessId}/submit")
  //has role service provider (or admin?)
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> submitBusiness(
      @PathVariable Integer businessId
  ) {
    logger.info("Submitting business profile {}", businessId);
    businessService.submitBusiness(businessId);
    return ResponseEntity.ok(new MessageResponse("Business profile submitted successfully"));
  }

  @PostMapping(value = "/{id}/upload-image", consumes = {"multipart/form-data"})
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<ImageUploadResponse> uploadImage(@PathVariable @Valid @NotBlank Integer id,
                                                         @RequestParam("image") final MultipartFile file,
                                                         @RequestParam("imageType") ImageType imageType) {
    logger.info("Uploading image for business {}", id);
    final String url = businessService.uploadImage(id, file, imageType);
    return ResponseEntity.ok(new ImageUploadResponse(url));
  }

  @DeleteMapping("{businessId}/images/{imageId}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> deleteImage(@PathVariable @Valid @NotBlank Integer businessId,
                                                     @PathVariable @Valid @NotBlank Integer imageId) {
    logger.info("Deleting image {} for business {}", imageId, businessId);
    businessService.deleteImage(businessId, imageId);
    return ResponseEntity.ok(new MessageResponse("Image deleted successfully"));
  }

  @GetMapping("{businessId}/profile-image")
  public ResponseEntity<BusinessImage> getProfileImage(@PathVariable @Valid @NotBlank Integer businessId) {
    logger.info("Getting profile image for business {}", businessId);
    BusinessImage profileImage = businessService.getProfileImage(businessId);
    return ResponseEntity.ok(profileImage);
  }

  @GetMapping("{businessId}/images")
  public ResponseEntity<List<BusinessImage>> getImages(@PathVariable @Valid @NotBlank Integer businessId) {
    logger.info("Getting images for business {}", businessId);
    List<BusinessImage> images = businessService.getImages(businessId);
    return ResponseEntity.ok(images);
  }

  @GetMapping("all")
  public ResponseEntity<List<Business>> getAllBusinesses() {
    logger.info("Getting all businesses");
    List<Business> allBusinesses = businessService.getAll();
    return ResponseEntity.ok(allBusinesses);
  }

  @GetMapping("all-active")
  public ResponseEntity<List<Business>> getAllActiveBusinesses() {
    logger.info("Getting all businesses");
    List<Business> allBusinesses = businessService.getAllActive();
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
  public ResponseEntity<List<Service>> getServices(@PathVariable @Valid @NotBlank Integer businessId) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessId);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessId, servicesOfBusiness);
    return ResponseEntity.ok(servicesOfBusiness);
  }

  //====================================================================================================
  // NEW ENDPOINTS
  //====================================================================================================

  // endpoint for adding new mapping: predefined_category - business
  // mapping between the two is important for searching businesses by predefined category

  @PostMapping("{businessId}/predefined-categories")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> linkBusinessWithPredefinedCategories(@PathVariable @Valid @NotBlank Integer
                                                                                  businessId,
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

}
