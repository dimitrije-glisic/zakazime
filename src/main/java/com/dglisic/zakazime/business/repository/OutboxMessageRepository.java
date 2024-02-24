package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.OutboxMessageStatus;
import java.util.List;
import jooq.tables.pojos.OutboxMessage;

public interface OutboxMessageRepository {

  void save(OutboxMessage outboxMessage);

  void updateStatus(Integer outboxMessageId, OutboxMessageStatus status);

  List<OutboxMessage> findByStatus(String outboxMessageStatus);
}
