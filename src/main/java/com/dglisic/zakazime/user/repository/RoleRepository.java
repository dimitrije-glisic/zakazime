package com.dglisic.zakazime.user.repository;

import java.util.Optional;
import jooq.tables.pojos.Role;

public interface RoleRepository {
  Optional<Role> findByName(String role);

  Optional<Role> findById(Integer roleId);
}
