package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.BusinessMapper;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final UserService userService;
  private final BusinessMapper businessMapper;
  private final BusinessRepository businessRepository;
  private final ServiceRepository serviceRepository;

  //add roles authorization
  @Override
  public Business getBusinessProfileForUser(String userEmail) {
    User user = userService.findUserByEmailOrElseThrow(userEmail);
    Business business = businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));
    business.setOwner(user);
    List<Service> servicesOfBusiness = serviceRepository.getServicesOfBusiness(business.getId());
    business.setServices(servicesOfBusiness);
    return business;
  }

  @Override
  public Business createBusinessProfile(
      CreateBusinessProfileRequest createBusinessProfileRequest) {
    User user = userService.findUserByEmailOrElseThrow(createBusinessProfileRequest.ownerEmail());
    BusinessType businessType = getBusinessType(createBusinessProfileRequest.type());

    Business toBeSaved = businessMapper.mapToBusinessProfile(createBusinessProfileRequest);
    toBeSaved.setOwner(user);
    toBeSaved.setType(businessType);
    toBeSaved.setStatus("CREATED");
    toBeSaved.setCreatedOn(LocalDateTime.now());

    return businessRepository.createBusinessProfile(toBeSaved);
  }

  private BusinessType getBusinessType(@NotBlank final String typeName) {
    return businessRepository.getBusinessTypes().stream()
        .filter(type -> type.getName().equals(typeName.toUpperCase()))
        .findFirst()
        .orElseThrow(() -> new ApplicationException("Business type not found", HttpStatus.BAD_REQUEST));
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
  public List<Service> getServicesForType(String type) {
    return businessRepository.getServicesForType(type);
  }

  @Override
  public List<Service> getServicesOfBusiness(String businessName) {
    Business business = businessRepository.findBusinessByName(businessName)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    return serviceRepository.getServicesOfBusiness(business.getId());
  }

  @Override
  @Transactional
  public void saveServices(List<Service> services, String businessName) {
    Business business = businessRepository.findBusinessByName(businessName)
        .orElseThrow(() -> new ApplicationException("Business not found", HttpStatus.NOT_FOUND));
    fillServicesWithCategoryAndBusiness(services, business);
    serviceRepository.saveServices(services);
  }

  private void fillServicesWithCategoryAndBusiness(List<Service> services, Business business) {
    for (Service service : services) {
      service.setCategory(serviceRepository.findCategoryByName(service.getCategoryName())
          .orElseThrow(() -> new ApplicationException("Category not found", HttpStatus.BAD_REQUEST)));
      service.setBusiness(business);
    }
  }

}