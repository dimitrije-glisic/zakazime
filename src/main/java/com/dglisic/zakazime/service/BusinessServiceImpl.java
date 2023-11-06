package com.dglisic.zakazime.service;

import com.dglisic.zakazime.controller.BusinessProfileMapper;
import com.dglisic.zakazime.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.domain.BusinessProfile;
import com.dglisic.zakazime.domain.User;
import com.dglisic.zakazime.repository.BusinessRepository;
import java.time.LocalDateTime;
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
  public com.dglisic.zakazime.domain.BusinessProfile getBusinessProfileForUser(String userEmail) {
    User user = userService.findUserByEmailOrElseThrow(userEmail);
    BusinessProfile businessProfile = businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));

    return businessProfile.toBuilder()
        .owner(user)
        .build();
  }

  @Override
  public com.dglisic.zakazime.domain.BusinessProfile createBusinessProfile(
      CreateBusinessProfileRequest createBusinessProfileRequest) {
    User user = userService.findUserByEmailOrElseThrow(createBusinessProfileRequest.ownerEmail());

    com.dglisic.zakazime.domain.BusinessProfile toBeSaved = businessMapper.mapToBusinessProfile(createBusinessProfileRequest);
    toBeSaved.setOwner(user);
    toBeSaved.setStatus("CREATED");
    toBeSaved.setCreatedOn(LocalDateTime.now());

    return businessRepository.createBusinessProfile(toBeSaved);
  }

}
