package com.dglisic.zakazime.user.repository;


import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;

public interface UserRepository {

  Account saveUser(Account user);

  Optional<Account> findByEmail(String email);

  void linkBusinessProfileToUser(int userId, int businessProfileId);

  List<Account> getAllUsers();
}
