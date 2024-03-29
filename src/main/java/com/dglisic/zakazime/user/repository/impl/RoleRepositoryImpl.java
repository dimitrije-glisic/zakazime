package com.dglisic.zakazime.user.repository.impl;

import static jooq.tables.Role.ROLE;

import com.dglisic.zakazime.user.repository.RoleRepository;
import java.util.Optional;
import jooq.tables.pojos.Role;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("ConstantConditions")
public class RoleRepositoryImpl implements RoleRepository {

  private final DSLContext dsl;

  public RoleRepositoryImpl(final DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  @Cacheable("roles-by-name")
  public Optional<Role> findByName(final String roleName) {
    final Role role = dsl.selectFrom(ROLE).where(ROLE.NAME.eq(roleName)).fetchOneInto(Role.class);
    return Optional.ofNullable(role);
  }

  @Override
  @Cacheable("roles-by-id")
  public Optional<Role> findById(final Integer roleId) {
    final Role role = dsl.selectFrom(ROLE).where(ROLE.ID.eq(roleId)).fetchOneInto(Role.class);
    return Optional.ofNullable(role);
  }
}
