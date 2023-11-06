package com.dglisic.zakazime.user.repository;

import com.dglisic.zakazime.user.domain.Role;
import java.util.Optional;

public interface RoleRepository {
  Optional<Role> findByName(String role);

  Optional<Role> findById(Integer roleId);
}
