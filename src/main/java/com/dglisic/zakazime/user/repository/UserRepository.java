package com.dglisic.zakazime.user.repository;


import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Role;

public interface UserRepository {

  Account saveUser(Account user);

  Optional<Account> findByEmail(String email);

  void linkBusinessProfileToUser(int userId, int businessProfileId);

  List<Account> getAllUsers();

  void updateRole(Account user, Role role);

  Optional<Account> findById(Integer id);
}
