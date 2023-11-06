package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.BusinessProfileMapper;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final UserService userService;
  private final BusinessProfileMapper businessMapper;
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

    BusinessProfile toBeSaved = businessMapper.mapToBusinessProfile(createBusinessProfileRequest);
    toBeSaved.setOwner(user);
    toBeSaved.setStatus("CREATED");
    toBeSaved.setCreatedOn(LocalDateTime.now());

    return businessRepository.createBusinessProfile(toBeSaved);
  }

  @Override
  public List<BusinessProfile> getAll() {
    return businessRepository.getAll();
  }

}
