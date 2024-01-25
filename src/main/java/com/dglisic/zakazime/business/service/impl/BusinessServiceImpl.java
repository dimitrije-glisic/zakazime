package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.BusinessMapper;
import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.ServiceMapper;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.PredefinedCategoryRepository;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.repository.UserDefinedCategoryRepository;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final BusinessMapper businessMapper;
  private final ServiceMapper serviceMapper;
  private final UserService userService;
  private final BusinessRepository businessRepository;
  private final ServiceRepository serviceRepository;
  // used for organization of services of business
  private final UserDefinedCategoryRepository categoryRepository;
  // used for search by end user
  private final PredefinedCategoryRepository predefinedCategoryRepository;

  @Override
  @Transactional
  public Business create(final CreateBusinessProfileRequest request) {
    validateOnCreateBusiness(request);
    final Business toBeCreated = businessMapper.map(request);
    toBeCreated.setStatus(BusinessStatus.CREATED.toString());
    toBeCreated.setCreatedOn(LocalDateTime.now());
    final Account user = userService.requireLoggedInUser();
    final Business created = businessRepository.storeBusinessProfile(toBeCreated, user);
    businessRepository.linkBusinessToOwner(created.getId(), user.getId());
    userService.setRoleToServiceProvider(user);
    return created;
  }

  @Override
  public Optional<Business> findBusinessById(Integer businessId) {
    return businessRepository.findBusinessById(businessId);
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
  public void updateService(final int businessId, final int serviceId, final UpdateServiceRequest updateServiceRequest) {
    validateOnUpdate(serviceId, businessId, updateServiceRequest);
    final Service service = serviceMapper.map(updateServiceRequest);
    // this is safe because we validated that service exists and belongs to business
    service.setId(serviceId);
    // todo
    // service no longer has a reference to business id, it has a reference to category id, which has a reference to business id
//    service.setBusinessId(businessId);
    serviceRepository.update(service);
  }

  @Override
  public List<Service> getServicesOfBusiness(int businessId) {
    Business business = businessRepository.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return serviceRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  public void addServiceToBusiness(final CreateServiceRequest serviceRequest, final Integer businessId) {
    validateOnSaveService(serviceRequest, businessId);
    final Service serviceToBeSaved = fromRequest(serviceRequest, businessId);
    serviceRepository.create(serviceToBeSaved);
  }

  @Override
  public void linkPredefinedCategories(List<Integer> categoryIds, Integer businessId) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    final boolean allExist = predefinedCategoryRepository.allExist(new HashSet<>(categoryIds));
    if (!allExist) {
      throw new ApplicationException("Category does not exist", HttpStatus.BAD_REQUEST);
    }
    businessRepository.linkPredefined(categoryIds, businessId);
  }

  public List<PredefinedCategory> getPredefinedCategories(Integer businessId) {
    requireBusinessExists(businessId);
    return businessRepository.getPredefinedCategories(businessId);
  }

  @Override
  @Transactional
  public void addServiceToBusiness(final List<CreateServiceRequest> createServiceRequestList, final Integer businessId) {
    validateOnSaveServices(createServiceRequestList, businessId);
    final List<Service> servicesToBeSaved = fromRequest(createServiceRequestList, businessId);
    serviceRepository.saveServices(servicesToBeSaved);
  }

  private List<Service> fromRequest(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    return createServiceRequestList.stream()
        .map(req -> fromRequest(req, businessId))
        .toList();
  }

  private Service fromRequest(final CreateServiceRequest req, final int businessId) {
    final Service service = serviceMapper.map(req);
    // todo - see what to do with this
//    service.setBusinessId(businessId);
    return service;
  }

  private void validateOnCreateBusiness(CreateBusinessProfileRequest request) {
    // name must be unique
    if (businessRepository.findBusinessByName(request.name()).isPresent()) {
      throw new ApplicationException("Business with name " + request.name() + " already exists", HttpStatus.BAD_REQUEST);
    }
  }

  private void validateOnSaveServices(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    final Set<Integer> subCategoryIds =
        createServiceRequestList.stream().map(CreateServiceRequest::subcategoryId).collect(Collectors.toSet());
    // todo - no longer subcategory - only category
    final boolean allExist = categoryRepository.allExist(subCategoryIds);
    if (!allExist) {
      throw new ApplicationException("Subcategory does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  private void validateOnSaveService(final CreateServiceRequest serviceRequest, final Integer businessId) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    requireSubcategoryExists(serviceRequest.subcategoryId());
  }

  private void validateOnUpdate(final Integer serviceId, final Integer businessId,
                                final UpdateServiceRequest changeServiceRequest) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    requireServiceExistsAndBelongsToBusiness(serviceId, businessId);
    requireSubcategoryExists(changeServiceRequest.subcategoryId());
  }

  private void requireBusinessExists(final Integer businessId) {
    businessRepository.findBusinessById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " does not exist", HttpStatus.BAD_REQUEST)
    );
  }

  // todo - add business_role table and check if logged in user has role of owner/business_admin for business
  private void requireUserPermittedToChangeBusiness(final Integer businessId) {
    Account loggedInUser = userService.requireLoggedInUser();
    if (!businessRepository.isUserRelatedToBusiness(loggedInUser.getId(), businessId)) {
      throw new ApplicationException("User " + loggedInUser.getEmail() + " is not related to business " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

  private void requireServiceExistsAndBelongsToBusiness(final Integer serviceId, final Integer businessId) {
    final Optional<Service> serviceFromDb = serviceRepository.findServiceById(serviceId);
    if (serviceFromDb.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    }
    final Service serviceFromDbValue = serviceFromDb.get();
    // todo - see what with this
//    if (!serviceFromDbValue.getBusinessId().equals(businessId)) {
//      throw new ApplicationException(
//          "Service with id " + serviceId + " does not belong to business with id " + businessId,
//          HttpStatus.BAD_REQUEST);
//    }
  }

  private void requireSubcategoryExists(final Integer subcategoryId) {
    final boolean subcategoryExists = categoryRepository.exists(subcategoryId);
    if (!subcategoryExists) {
      throw new ApplicationException("Subcategory does not exist", HttpStatus.BAD_REQUEST);
    }
  }
}