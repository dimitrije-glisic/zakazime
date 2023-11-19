package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.BusinessMapper;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final UserService userService;
  private final BusinessMapper businessMapper;
  private final BusinessRepository businessRepository;

  //add roles authorization
  @Override
  public BusinessProfile getBusinessProfileForUser(String userEmail) {
    User user = userService.findUserByEmailOrElseThrow(userEmail);
    BusinessProfile businessProfile = businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));

    return businessProfile.toBuilder()
        .owner(user)
        .build();
  }

  @Override
  public BusinessProfile createBusinessProfile(
      CreateBusinessProfileRequest createBusinessProfileRequest) {
    User user = userService.findUserByEmailOrElseThrow(createBusinessProfileRequest.ownerEmail());
    BusinessType businessType = getBusinessType(createBusinessProfileRequest.type());

    BusinessProfile toBeSaved = businessMapper.mapToBusinessProfile(createBusinessProfileRequest);
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
  public List<BusinessProfile> getAll() {
    return businessRepository.getAll();
  }

  @Override
  public List<BusinessType> getBusinessTypes() {
    return businessRepository.getBusinessTypes();
  }

  @Override
  public List<com.dglisic.zakazime.business.domain.Service> getServicesForType(String type) {
    return businessRepository.getServicesForType(type);
  }

}