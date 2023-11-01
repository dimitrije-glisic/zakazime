package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.domain.Role;
import model.tables.records.RoleRecord;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRecordMapper implements RecordMapper<RoleRecord, Role> {

  @Override
  public Role map(RoleRecord record) {
    return new Role(record.getId(), record.getName());
  }

}
