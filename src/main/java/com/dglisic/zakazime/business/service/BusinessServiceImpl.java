package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.CategoryRepository;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final UserService userService;
  private final BusinessRepository businessRepository;
  private final ServiceRepository serviceRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Business getBusinessProfileForUser(String userEmail) {
    User user = userService.findUserByEmailOrElseThrow(userEmail);
    return businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));
  }

  // todo - check if logged in user is owner of business (or admin) before saving
  @Override
  public Business createBusinessProfile(
      Business toBeCreated) {
    //todo - validate
//    validateOnCreate(toBeCreated);
    toBeCreated.setStatus("CREATED");
    toBeCreated.setCreatedOn(LocalDateTime.now());
    // todo - set ownerId to logged in user id
    // something like this: authService.getLoggedInUser().getId()
    return businessRepository.createBusinessProfile(toBeCreated, 1);
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
  public ServiceCategory getCategoryOrThrow(String categoryName) {
    return categoryRepository.findCategory(categoryName).orElseThrow(
        () -> new ApplicationException("Category with name " + categoryName + " does not exist", HttpStatus.BAD_REQUEST)
    );
  }

  @Override
  public void updateService(final int serviceId, final Service service, final int businessId) {
    validateOnUpdate(serviceId, businessId);
    serviceRepository.updateService(serviceId, service);
  }

  @Override
  public List<Service> getServiceTemplatesOfType(String type) {
    return serviceRepository.getServiceTemplatesOfBusinessType(type);
  }

  @Override
  public List<Service> getServicesOfBusiness(int businessId) {
    Business business = businessRepository.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return serviceRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  @Transactional
  public void saveServicesForBusiness(List<Service> services, int businessId) {
    // todo validate that services belong to business
    //    validateOnSave(services, businessId);
    serviceRepository.saveServices(services);
  }

  @Override
  public Business getBusinessOrThrow(int businessId) {
    return businessRepository.findBusinessById(businessId)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
  }

  // todo - check if logged in user is owner of business (or admin) before saving
  private void validateOnUpdate(final int serviceId, final int businessId) {
    final Optional<Service> serviceFromDb = serviceRepository.findServiceById(serviceId);
    if (serviceFromDb.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    } else {
      Service serviceFromDbValue = serviceFromDb.get();
      if (!serviceFromDbValue.getBusinessId().equals(businessId)) {
        throw new ApplicationException(
            "Service with id " + serviceId + " does not belong to business with id " + businessId,
            HttpStatus.BAD_REQUEST);
      }
    }
  }

}