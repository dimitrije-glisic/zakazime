package com.dglisic.zakazime.user.repository;

import com.dglisic.zakazime.user.domain.Role;
import jooq.tables.records.RoleRecord;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRecordMapper implements RecordMapper<RoleRecord, Role> {

  @Override
  public Role map(RoleRecord record) {
    return new Role(record.getId(), record.getName());
  }

}
