package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import java.util.List;
import jooq.tables.pojos.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxMessageProcessor {

  private final OutboxMessageRepository outboxMessageRepository;
  private final JavaMailSender mailSender;

  @Scheduled(fixedRate = 5000) // Example: Run every 5 seconds
  @Transactional
  public void processOutboxMessages() {
    final List<OutboxMessage> pendingMessages = outboxMessageRepository.findByStatus(OutboxMessageStatus.PENDING.toString());
    for (OutboxMessage message : pendingMessages) {
      sendMessage(message);
    }
  }

  private void sendMessage(OutboxMessage message) {
    final SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(message.getRecipient());
    mailMessage.setSubject(message.getSubject());
    mailMessage.setText(message.getBody());
    mailSender.send(mailMessage);
    updateStatusWithRetries(message.getId(), OutboxMessageStatus.SENT);
  }

  private void updateStatusWithRetries(Integer messageId, OutboxMessageStatus status) {
    int maxRetries = 3;
    int retryCount = 0;

    while (retryCount < maxRetries) {
      try {
        outboxMessageRepository.updateStatus(messageId, status);
        return; // Success!
      } catch (Exception e) {
        log.error("Failed to update outbox message status", e);
        retryCount++;

        // Simple backoff (adjust as needed)
        if (retryCount < maxRetries) {
          try {
            Thread.sleep(500L * retryCount);
          } catch (InterruptedException ignored) {
          }
        }
      }
    }
    // After max retries, log and potentially raise an alert for manual intervention
    log.error("Failed to update outbox message status after {} retries", maxRetries);
  }


}
