package com.dglisic.zakazime.repository;


import java.util.Optional;
import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;

public interface UserRepository {

  AccountRecord saveUser(AccountRecord account);

  Optional<AccountRecord> findUserByEmail(String email);

  int save(BusinessProfileRecord businessProfile);

  void linkBusinessProfileToUser(int userId, int businessProfileId);
}
