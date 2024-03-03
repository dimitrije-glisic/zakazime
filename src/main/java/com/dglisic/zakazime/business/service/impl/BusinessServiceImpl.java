package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.BusinessMapper;
import com.dglisic.zakazime.business.controller.dto.BusinessRichObject;
import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.ImageType;
import com.dglisic.zakazime.business.repository.BusinessImageRepository;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.PredefinedCategoryRepository;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.business.service.ImageStorage;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class BusinessServiceImpl implements BusinessService {

  private final BusinessMapper businessMapper;
  private final UserService userService;
  private final BusinessRepository businessRepository;
  private final PredefinedCategoryRepository predefinedCategoryRepository;
  private final ImageStorage imageStorage;
  private final BusinessImageRepository businessImageRepository;
  private final BusinessValidator businessValidator;

  @Override
  public Business create(final CreateBusinessProfileRequest request) {
    validateOnCreateBusiness(request);
    final Business toBeCreated = businessMapper.map(request);
    toBeCreated.setStatus(BusinessStatus.CREATED.toString());
    toBeCreated.setCreatedOn(LocalDateTime.now());
    return businessRepository.storeBusinessProfile(toBeCreated);
  }

  @Override
  public void submitBusiness(Integer businessId) {
    final Business business = businessValidator.requireBusinessExistsAndReturn(businessId);
    if (!business.getStatus().equals(BusinessStatus.APPROVED.toString())) {
      throw new ApplicationException("Business with id " + businessId + " is not in status APPROVED", HttpStatus.BAD_REQUEST);
    }
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    businessRepository.changeStatus(businessId, BusinessStatus.ACTIVE.toString());
  }

  @Override
  public Optional<Business> findBusinessById(Integer businessId) {
    return businessRepository.findById(businessId);
  }

  @Override
  public Business getBusinessProfileForUser(final String userEmail) {
    Account user = userService.findUserByEmailOrElseThrow(userEmail);
    return businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));
  }

  @Override
  public List<Business> getAll() {
    return businessRepository.getAll();
  }

  @Override
  public List<Business> getAllActive() {
    return businessRepository.getAllWithStatus(BusinessStatus.ACTIVE);
  }

  @Override
  public List<Service> getServicesOfBusiness(final Integer businessId) {
    log.debug("Getting services for business with id {}", businessId);
    final Business business = businessRepository.findById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return businessRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  public void linkPredefinedCategories(List<Integer> categoryIds, Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final boolean allExist = predefinedCategoryRepository.allExist(new HashSet<>(categoryIds));
    if (!allExist) {
      throw new ApplicationException("Category does not exist", HttpStatus.BAD_REQUEST);
    }
    businessRepository.linkPredefined(categoryIds, businessId);
  }

  public List<PredefinedCategory> getPredefinedCategories(Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    return businessRepository.getPredefinedCategories(businessId);
  }

  @Override
  public List<UserDefinedCategory> getUserDefinedCategories(Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    return businessRepository.getUserDefinedCategories(businessId);
  }

  @Override
  public List<Business> searchBusinesses(String city, String businessType, String category) {
    return businessRepository.searchBusinesses(city, businessType, category);
  }

  @Override
  public List<Business> getAllBusinessesInCity(String city) {
    return businessRepository.getAllBusinessesInCity(city);
  }

  @Override
  @Transactional
  public String uploadImage(Integer businessId, MultipartFile imageFile, ImageType imageType) {
    validateOnUploadImage(businessId);
    final String url = makeUrl(businessId, imageFile);
    businessImageRepository.storeImage(businessId, url);
    if (imageType.equals(ImageType.PROFILE)) {
      businessRepository.updateProfileImageUrl(businessId, url);
    }
    storeImage(url, imageFile);
    return url;
  }

  @Override
  @Transactional
  public void deleteImage(Integer businessId, Integer imageId) {
    validateOnDeleteImage(businessId, imageId);
    businessImageRepository.deleteImage(imageId);
    // todo - maybe delete image from storage or archive, for now we will keep it but it will not be used
//    imageStorage.deleteImage(url);
  }

  @Override
  public List<BusinessImage> getImages(Integer businessId) {
    return businessImageRepository.getImages(businessId);
  }

  @Override
  public BusinessImage getProfileImage(Integer businessId) {
    return businessRepository.getProfileImage(businessId).orElseThrow(
        () -> new ApplicationException("Profile image not found for business with id " + businessId, HttpStatus.NOT_FOUND)
    );
  }

  @Override
  public BusinessRichObject getCompleteBusinessData(String city, String businessName) {
    final Business business = businessRepository.findBusinessByCityAndName(city, businessName)
        .orElseThrow(
            () -> new ApplicationException(String.format("Business with name %s in city %s not found", businessName, city),
                HttpStatus.NOT_FOUND));
    final List<Service> services = businessRepository.getServicesOfBusiness(business.getId());
    final List<UserDefinedCategory> userDefinedCategories = businessRepository.getUserDefinedCategories(business.getId());
    return new BusinessRichObject(business, services, userDefinedCategories);
  }

  private @NotNull String makeUrl(final Integer id, final MultipartFile imageFile) {
    final String idPartOfPath = String.format("id_%d", id);
    return "businesses" + "/" + idPartOfPath + "/" + imageFile.getOriginalFilename();
  }

  private void storeImage(final String url, final MultipartFile imageFile) {
    try {
      imageStorage.storeImage(url, imageFile);
    } catch (IOException e) {
      log.error("Failed to store imageFile", e);
      throw new ApplicationException("Failed to store imageFile", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void validateOnCreateBusiness(CreateBusinessProfileRequest request) {
    // name must be unique
    if (businessRepository.findBusinessByName(request.name()).isPresent()) {
      throw new ApplicationException("Business with name " + request.name() + " already exists", HttpStatus.BAD_REQUEST);
    }
  }

  private void validateOnUploadImage(Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
  }

  private void validateOnDeleteImage(Integer businessId, Integer imageId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    final boolean imageBelongsToBusiness = businessImageRepository.imageBelongsToBusiness(imageId, businessId);
    if (!imageBelongsToBusiness) {
      throw new ApplicationException("Image with id " + imageId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

}