package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.AdminService;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.OutboxMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final BusinessRepository businessRepository;
  private final OutboxMessageRepository outboxMessageRepository;

  @Override
  public List<Business> getAllWaitingForApproval() {
    return businessRepository.getAllWithStatus(BusinessStatus.CREATED);
  }

  @Override
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
    final String message = "Your business " + business.getName() + " has been approved!";
    outboxMessage.setBody(message);
    outboxMessage.setStatus(OutboxMessageStatus.PENDING.toString());
    outboxMessageRepository.save(outboxMessage);
  }

  @Override
  public void rejectBusiness(Integer businessId) {

  }

}