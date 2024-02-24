package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.OutboxMessage.OUTBOX_MESSAGE;

import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import com.dglisic.zakazime.business.repository.OutboxMessageRepository;
import java.util.List;
import jooq.tables.pojos.OutboxMessage;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxMessageRepositoryImpl implements OutboxMessageRepository {

  private final DSLContext dsl;

  @Override
  public void save(OutboxMessage outboxMessage) {
    dsl.insertInto(OUTBOX_MESSAGE)
        .set(OUTBOX_MESSAGE.ID, outboxMessage.getId())
        .set(OUTBOX_MESSAGE.SUBJECT, outboxMessage.getSubject())
        .set(OUTBOX_MESSAGE.BODY, outboxMessage.getBody())
        .set(OUTBOX_MESSAGE.RECIPIENT, outboxMessage.getRecipient())
        .set(OUTBOX_MESSAGE.STATUS, outboxMessage.getStatus())
        .execute();
  }

  @Override
  public void updateStatus(Integer outboxMessageId, OutboxMessageStatus status) {
    dsl.update(OUTBOX_MESSAGE)
        .set(OUTBOX_MESSAGE.STATUS, status.name())
        .where(OUTBOX_MESSAGE.ID.eq(outboxMessageId))
        .execute();
  }

  @Override
  public List<OutboxMessage> findByStatus(String outboxMessageStatus) {
    return dsl.selectFrom(OUTBOX_MESSAGE)
        .where(OUTBOX_MESSAGE.STATUS.eq(outboxMessageStatus))
        .fetchInto(OutboxMessage.class);
  }

}
