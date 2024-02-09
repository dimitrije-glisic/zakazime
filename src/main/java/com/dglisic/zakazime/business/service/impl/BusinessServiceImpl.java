package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.BusinessMapper;
import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.ServiceMapper;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;
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
import java.util.stream.Collectors;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class BusinessServiceImpl implements BusinessService {

  private final BusinessMapper businessMapper;
  private final ServiceMapper serviceMapper;
  private final UserService userService;
  private final BusinessRepository businessRepository;
  private final ServiceRepository serviceRepository;
  // used for organization of services of business
  private final UserDefinedCategoryRepository userDefinedCategoryRepository;
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
  @Cacheable(value = "services", key = "#businessId")
  public List<Service> getServicesOfBusiness(final Integer businessId) {
    log.debug("Getting services for business with id {}", businessId);
    final Business business = businessRepository.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return businessRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  @CachePut(value = "services", key = "#businessId")
  public Service addServicesToBusiness(final CreateServiceRequest serviceRequest, final Integer businessId) {
    validateOnSaveService(serviceRequest, businessId);
    final Service serviceToBeSaved = fromRequest(serviceRequest);
    return serviceRepository.create(serviceToBeSaved);
  }

  @Override
  @CacheEvict(value = "services", key = "#businessId")
  public void updateService(final int businessId, final int serviceId, final UpdateServiceRequest updateServiceRequest) {
    validateOnUpdateService(serviceId, businessId, updateServiceRequest);
    final Service service = serviceMapper.map(updateServiceRequest);
    service.setId(serviceId);
    serviceRepository.update(service);
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
  public List<UserDefinedCategory> getUserDefinedCategories(Integer businessId) {
    requireBusinessExists(businessId);
    return businessRepository.getUserDefinedCategories(businessId);
  }

  @Override
  public void createUserDefinedCategory(CreateUserDefinedCategoryRequest categoryRequest, Integer businessId) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    final UserDefinedCategory category = new UserDefinedCategory()
        .setTitle(categoryRequest.title())
        .setBusinessId(businessId);
    businessRepository.createUserDefinedCategory(category);
  }

  @Override
  public void updateUserDefinedCategory(Integer businessId, Integer categoryId,
                                        UpdateUserDefinedCategoryRequest categoryRequest) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    final UserDefinedCategory category = requireUserDefinedCategory(categoryId);

    if (!category.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Category with id " + categoryId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }

    if (category.getTitle().equals(categoryRequest.title())) {
      return;
      // no change
    }
    category.setTitle(categoryRequest.title());
    userDefinedCategoryRepository.update(category);
  }

  @Override
  @CacheEvict(value = "services", key = "#businessId")
  public void deleteService(Integer businessId, Integer serviceId) {
    validateOnDeleteService(businessId, serviceId);
    serviceRepository.delete(serviceId);
  }

  @Override
  @Transactional
  @CacheEvict(value = "services", key = "#businessId")
  public List<Service> addServicesToBusiness(final List<CreateServiceRequest> createServiceRequestList,
                                             final Integer businessId) {
    validateOnSaveServices(createServiceRequestList, businessId);
    final List<Service> servicesToBeSaved = fromRequest(createServiceRequestList, businessId);
    return serviceRepository.saveServices(servicesToBeSaved);
  }

  private List<Service> fromRequest(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    return createServiceRequestList.stream()
        .map(this::fromRequest)
        .toList();
  }

  private Service fromRequest(final CreateServiceRequest req) {
    return serviceMapper.map(req);
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
    requireAllCategoriesExist(createServiceRequestList);
    requireAllTitlesUnique(createServiceRequestList, businessId);
  }

  private void validateOnSaveService(final CreateServiceRequest serviceRequest, final Integer businessId) {
    requireBusinessExists(businessId);
    requireUserPermittedToChangeBusiness(businessId);
    requireCategoryExists(serviceRequest.categoryId());
    requireUniqueServiceTitle(serviceRequest.title(), businessId);
  }

  private void validateOnUpdateService(final Integer serviceId, final Integer businessId,
                                       final UpdateServiceRequest changeServiceRequest) {
    requireBusinessExists(businessId);
    requireCategoryExists(changeServiceRequest.categoryId());
    requireServiceExists(serviceId);
    requireServiceBelongsToBusiness(serviceId, businessId);
    requireUserPermittedToChangeBusiness(businessId);
  }

  private void validateOnDeleteService(Integer businessId, Integer serviceId) {
    requireBusinessExists(businessId);
    requireServiceExists(serviceId);
    requireServiceBelongsToBusiness(serviceId, businessId);
    requireUserPermittedToChangeBusiness(businessId);
  }

  private void requireAllCategoriesExist(List<CreateServiceRequest> createServiceRequestList) {
    for (CreateServiceRequest createServiceRequest : createServiceRequestList) {
      requireCategoryExists(createServiceRequest.categoryId());
    }
  }

  private void requireAllTitlesUnique(List<CreateServiceRequest> createServiceRequestList, int businessId) {
    final var incomingTitles = createServiceRequestList.stream()
        .map(CreateServiceRequest::title)
        .collect(Collectors.toSet());

    boolean incomingTitlesAreUnique = incomingTitles.size() == createServiceRequestList.size();

    if (!incomingTitlesAreUnique) {
      throw new ApplicationException("Service titles must be unique", HttpStatus.BAD_REQUEST);
    }

    for (String title : incomingTitles) {
      requireUniqueServiceTitle(title, businessId);
    }
  }

  private void requireServiceBelongsToBusiness(Integer serviceId, Integer businessId) {
    final boolean serviceBelongsToBusiness = businessRepository.serviceBelongsToBusiness(serviceId, businessId);
    if (!serviceBelongsToBusiness) {
      throw new ApplicationException("Service with id " + serviceId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }
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

  private void requireUniqueServiceTitle(String title, Integer businessId) {
    final boolean exists = serviceRepository.existsByTitleAndBusinessId(title, businessId);
    if (exists) {
      throw new ApplicationException("Service with title " + title + " already exists, nothing was saved",
          HttpStatus.BAD_REQUEST);
    }
  }

  private void requireServiceExists(final Integer serviceId) {
    final Optional<Service> serviceFromDb = serviceRepository.findServiceById(serviceId);
    if (serviceFromDb.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  private void requireCategoryExists(final Integer categoryId) {
    final boolean categoryExists = userDefinedCategoryRepository.exists(categoryId);
    if (!categoryExists) {
      throw new ApplicationException("Category with id" + categoryId + "not exists", HttpStatus.BAD_REQUEST);
    }
  }

  private UserDefinedCategory requireUserDefinedCategory(Integer categoryId) {
    return userDefinedCategoryRepository.findUserDefinedCategoryById(categoryId)
        .orElseThrow(
            () -> new ApplicationException("Category with id " + categoryId + " does not exist", HttpStatus.BAD_REQUEST));
  }
}