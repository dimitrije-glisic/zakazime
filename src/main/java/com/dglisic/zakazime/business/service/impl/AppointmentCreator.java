package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateAppointmentRequest;
import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.AppointmentRepository;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import jooq.tables.pojos.OutboxMessage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AppointmentCreator<T extends CreateAppointmentRequest> {

  protected final BusinessValidator businessValidator;
  protected final EmployeeValidator employeeValidator;
  protected final CustomerService customerService;
  protected final TimeSlotManagement timeSlotManagement;
  protected final AppointmentRepository appointmentRepository;
  protected final OutboxMessageRepository outboxMessageRepository;

  abstract void createAppointment(T request);

  protected void createOutboxMessage(String recipient, String subject, String body) {
    final OutboxMessage outboxMessage = new OutboxMessage();
    outboxMessage.setRecipient(recipient);
    outboxMessage.setSubject(subject);
    outboxMessage.setBody(body);
    outboxMessage.setStatus(OutboxMessageStatus.PENDING.toString());
    outboxMessageRepository.save(outboxMessage);
  }

}
