package com.dglisic.zakazime.user.repository;

import static jooq.tables.Role.ROLE;

import com.dglisic.zakazime.user.domain.Role;
import java.util.Optional;
import jooq.tables.records.RoleRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("ConstantConditions")
public class RoleRepositoryImpl implements RoleRepository {

  private final DSLContext dsl;
  private final RoleRecordMapper roleRecordMapper;

  public RoleRepositoryImpl(DSLContext dsl, RoleRecordMapper roleRecordMapper) {
    this.dsl = dsl;
    this.roleRecordMapper = roleRecordMapper;
  }

  @Override
  public Optional<Role> findByName(String role) {
    RoleRecord roleRecord = dsl.selectFrom(ROLE).where(ROLE.NAME.eq(role)).fetchOne();
    if (roleRecord != null) {
      return Optional.of(roleRecordMapper.map(roleRecord));
    }
    return Optional.empty();
  }

  @Override
  public Optional<Role> findById(Integer roleId) {
    RoleRecord roleRecord = dsl.selectFrom(ROLE).where(ROLE.ID.eq(roleId)).fetchOne();
    if (roleRecord != null) {
      return Optional.of(roleRecordMapper.map(roleRecord));
    }
    return Optional.empty();
  }
}
