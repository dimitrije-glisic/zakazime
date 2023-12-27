package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.BusinessMapper;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.ServiceMapper;
import com.dglisic.zakazime.business.controller.UpdateServiceRequest;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.repository.ServiceSubcategoryRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
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
  private final ServiceSubcategoryRepository subcategoryRepository;

  @Override
  @Transactional
  public Business create(final CreateBusinessProfileRequest request) {
    validateOnCreateBusiness(request);
    final Business toBeCreated = businessMapper.map(request);
    toBeCreated.setStatus(BusinessStatus.CREATED.toString());
    toBeCreated.setCreatedOn(LocalDateTime.now());
    final Account user = userService.requireLoggedInUser();
    final Business businessProfile = businessRepository.storeBusinessProfile(toBeCreated, user);
    businessRepository.linkBusinessToOwner(toBeCreated.getId(), user.getId());
    userService.setRoleToServiceProvider(user);
    return businessProfile;
  }

  @Override
  public Optional<Business> findBusinessById(int businessId) {
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
  public List<BusinessType> getBusinessTypes() {
    return businessRepository.getBusinessTypes();
  }

  @Override
  public void updateService(final int businessId, final int serviceId, final UpdateServiceRequest updateServiceRequest) {
    validateOnUpdate(serviceId, businessId, updateServiceRequest);
    final Service service = serviceMapper.map(updateServiceRequest);
    // this is safe because we validated that service exists and belongs to business
    service.setId(serviceId);
    service.setBusinessId(businessId);
    serviceRepository.store(service);
  }

  @Override
  public List<Service> searchServiceTemplates(final @Nullable String businessType, @Nullable final String category,
                                              final @Nullable String subcategory) {
    return serviceRepository.searchServiceTemplates(businessType, category, subcategory);
  }

  @Override
  public List<Service> getServicesOfBusiness(int businessId) {
    Business business = businessRepository.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return serviceRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  @Transactional
  public void addServicesToBusiness(final List<CreateServiceRequest> createServiceRequestList, final Integer businessId) {
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
    service.setBusinessId(businessId);
    service.setTemplate(false);
    return service;
  }

  private void validateOnCreateBusiness(CreateBusinessProfileRequest request) {
    // name must be unique
    if (businessRepository.findBusinessByName(request.name()).isPresent()) {
      throw new ApplicationException("Business with name " + request.name() + " already exists", HttpStatus.BAD_REQUEST);
    }
  }

  private void validateOnSaveServices(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    // business must exist
    businessRepository.findBusinessById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " does not exist", HttpStatus.BAD_REQUEST)
    );

    // logged-in user must be related to business
    requireUserPermittedToChangeBusiness(businessId);

    // subcategory must exist
    final Set<Integer> subCategoryIds =
        createServiceRequestList.stream().map(CreateServiceRequest::subcategoryId).collect(Collectors.toSet());
    final boolean allExist = subcategoryRepository.allExist(subCategoryIds);
    if (!allExist) {
      throw new ApplicationException("Subcategory does not exist", HttpStatus.BAD_REQUEST);
    }

  }

  // todo - check if logged in user is owner of business (or admin) before saving

  private void validateOnUpdate(final int serviceId, final int businessId, final UpdateServiceRequest changeServiceRequest) {
    // business must exist
    businessRepository.findBusinessById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " does not exist", HttpStatus.BAD_REQUEST)
    );

    requireUserPermittedToChangeBusiness(businessId);

    final Optional<Service> serviceFromDb = serviceRepository.findServiceById(serviceId);
    if (serviceFromDb.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    }

    final Service serviceFromDbValue = serviceFromDb.get();
    if (!serviceFromDbValue.getBusinessId().equals(businessId)) {
      throw new ApplicationException(
          "Service with id " + serviceId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }

    //subcategory must exist
    final boolean subcategoryExists = subcategoryRepository.exists(changeServiceRequest.subcategoryId());
    if (!subcategoryExists) {
      throw new ApplicationException("Subcategory does not exist", HttpStatus.BAD_REQUEST);
    }

  }

  // todo - add business_role table and check if logged in user has role of owner/business_admin for business
  private void requireUserPermittedToChangeBusiness(int businessId) {
    Account loggedInUser = userService.requireLoggedInUser();
    if (!businessRepository.isUserRelatedToBusiness(loggedInUser.getId(), businessId)) {
      throw new ApplicationException("User " + loggedInUser.getEmail() + " is not related to business " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

}