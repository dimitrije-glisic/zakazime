package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.domain.Role;
import java.util.Optional;

public interface RoleRepository {
  Optional<Role> findByName(String role);

  Optional<Role> findById(Integer roleId);
}
