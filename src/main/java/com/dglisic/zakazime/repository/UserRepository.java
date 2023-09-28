package com.dglisic.zakazime.repository;


import java.util.Optional;
import model.tables.records.AccountsRecord;

public interface UserRepository {

  void saveUser(AccountsRecord account);

  Optional<AccountsRecord> findUserByEmail(String email);

}
