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
    final Business business = validateOnReview(businessId);
    businessRepository.updateStatus(businessId, BusinessStatus.APPROVED);
    final Account businessUser = createBusinessUser(business);
    createOutboxMessageApproved(business, businessUser);
  }

  @Override
  public void rejectBusiness(Integer businessId, String reason) {
    final Business business = validateOnReview(businessId);
    businessRepository.updateStatus(businessId, BusinessStatus.REJECTED);
    createOutboxMessageRejected(business, reason);
  }

  private Business validateOnReview(Integer businessId) {
    final Business business = businessRepository.findById(businessId).orElseThrow(
        () -> new ApplicationException("Business with id " + businessId + " not found", HttpStatus.BAD_REQUEST)
    );

    if (!business.getStatus().equals(BusinessStatus.CREATED.name())) {
      throw new ApplicationException("Business with id " + businessId + " is not waiting for approval", HttpStatus.BAD_REQUEST);
    }

    return business;
  }

  private Account createBusinessUser(Business business) {
    Account businessUser = userService.createBusinessUser(business);
    log.info("Created business user with id {}", businessUser.getId());
    return businessUser;
  }

  private void createOutboxMessageApproved(Business business, Account businessUser) {
    final String recipient = business.getEmail();
    final String subject = "Zahtev Prihvacen!";
    final String message = "Vaša registracija za salon " + business.getName() + " je prihvaćena! " +
        "Vaše korisničko ime je " + businessUser.getEmail() + " a vaša lozinka je " + businessUser.getPassword();
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessageRejected(Business business, String reason) {
    final String recipient = business.getEmail();
    final String subject = "Zahtev odbijen";
    final String message = "Vaša registracija za salon " + business.getName() + " je odbijena. Razlog: " + reason;
    createOutboxMessage(recipient, subject, message);
  }

  private void createOutboxMessage(String recipient, String subject, String body) {
    OutboxMessage outboxMessage = new OutboxMessage();
    outboxMessage.setRecipient(recipient);
    outboxMessage.setSubject(subject);
    outboxMessage.setBody(body);
    outboxMessage.setStatus(OutboxMessageStatus.PENDING.toString());
    outboxMessageRepository.save(outboxMessage);
  }


}