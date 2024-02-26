package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.AdminService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import java.util.List;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

  private final BusinessRepository businessRepository;
  private final OutboxMessageRepository outboxMessageRepository;
  private final UserService userService;

  @Override
  public List<Business> getAllWaitingForApproval() {
    return businessRepository.getAllWithStatus(BusinessStatus.CREATED);
  }

  @Override
  @Transactional
  public void approveBusiness(Integer businessId) {
    Business business = businessRepository.findBusinessById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " not found", HttpStatus.BAD_REQUEST)
    );

    if (!business.getStatus().equals(BusinessStatus.CREATED.name())) {
      throw new ApplicationException("Business with id " + businessId + " is not waiting for approval", HttpStatus.BAD_REQUEST);
    }

    // change status to APPROVED and send email to business owner
    businessRepository.updateStatus(businessId, BusinessStatus.APPROVED);

    // Create an outbox message
    OutboxMessage outboxMessage = new OutboxMessage();
    outboxMessage.setRecipient(business.getEmail());
    outboxMessage.setSubject("Business Approved!");
    Account businessUser = createBusinessUser(business);
    final String message =
        "Your business " + business.getName() + " has been approved! Your username is " + businessUser.getEmail() +
            " and your password is " + businessUser.getPassword();
    outboxMessage.setBody(message);
    outboxMessage.setStatus(OutboxMessageStatus.PENDING.toString());
    outboxMessageRepository.save(outboxMessage);
  }

  private Account createBusinessUser(Business business) {
    Account businessUser = userService.createBusinessUser(business);
    log.info("Created business user with id {}", businessUser.getId());
    return businessUser;
  }

  @Override
  public void rejectBusiness(Integer businessId) {

  }

}