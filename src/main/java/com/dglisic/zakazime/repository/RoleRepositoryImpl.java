package com.dglisic.zakazime.repository;

import static model.tables.Role.ROLE;

import com.dglisic.zakazime.domain.Role;
import java.util.Optional;
import model.tables.records.RoleRecord;
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