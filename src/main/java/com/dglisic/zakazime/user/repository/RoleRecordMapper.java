package com.dglisic.zakazime.user.repository;

import jooq.tables.pojos.Role;
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
