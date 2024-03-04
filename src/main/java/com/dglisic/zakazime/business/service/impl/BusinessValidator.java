package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BusinessValidator {

  private final BusinessRepository businessRepository;
  private final UserService userService;

  public void requireServiceBelongsToBusiness(Integer serviceId, Integer businessId) {
    final boolean serviceBelongsToBusiness = businessRepository.serviceBelongsToBusiness(serviceId, businessId);
    if (!serviceBelongsToBusiness) {
      throw new ApplicationException("Service with id " + serviceId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

  public Service requireServiceBelongsToBusinessAndReturn(Integer serviceId, Integer businessId) {
    return businessRepository.findServiceOfBusiness(serviceId, businessId).orElseThrow(
        () -> new ApplicationException("Service with id " + serviceId + " does not belong to business with id " + businessId,
            HttpStatus.BAD_REQUEST)
    );
  }

  public void requireBusinessExists(final Integer businessId) {
    businessRepository.findById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " does not exist", HttpStatus.BAD_REQUEST)
    );
  }

  public Business requireBusinessExistsAndReturn(final Integer businessId) {
    return businessRepository.findById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " does not exist", HttpStatus.BAD_REQUEST)
    );
  }
  // todo - add business_role table and check if logged in user has role of owner/business_admin for business

  public void requireCurrentUserPermittedToChangeBusiness(final Integer businessId) {
    Account loggedInUser = userService.requireLoggedInUser();
    if (!businessRepository.isUserRelatedToBusiness(loggedInUser.getId(), businessId)) {
      throw new ApplicationException("User " + loggedInUser.getEmail() + " is not related to business " + businessId,
          HttpStatus.BAD_REQUEST);
    }
  }

}
