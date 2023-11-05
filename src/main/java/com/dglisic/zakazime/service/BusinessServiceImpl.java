package com.dglisic.zakazime.service;

import com.dglisic.zakazime.domain.User;
import com.dglisic.zakazime.repository.BusinessRepository;
import model.tables.records.BusinessProfileRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl implements BusinessService {

  private final UserService userService;
  private final BusinessRepository businessRepository;

  public BusinessServiceImpl(UserService userService, BusinessRepository businessRepository) {
    this.userService = userService;
    this.businessRepository = businessRepository;
  }

  //add roles authorization
  @Override
  public BusinessProfileRecord getBusinessProfileForUser(String userEmail) {
    User user = userService.findUserByEmailOrElseThrow(userEmail);
//    if (user.getUserType().equals(UserType.CUSTOMER.toString())) {
//      throw new ApplicationException("This is permitted only for business users", HttpStatus.BAD_REQUEST);
//    }
    return businessRepository.getBusinessProfile(user.getId())
        .orElseThrow(() -> new ApplicationException("Business profile not found for user " + userEmail, HttpStatus.NOT_FOUND));
  }

}
