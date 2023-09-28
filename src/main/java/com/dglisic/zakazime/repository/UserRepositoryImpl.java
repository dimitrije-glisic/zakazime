package com.dglisic.zakazime.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import model.tables.Accounts;
import model.tables.records.AccountsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext create;

  public UserRepositoryImpl(DSLContext dslContext) {
    this.create = dslContext;
  }

  @Override
  public void saveUser(AccountsRecord account) {
    var newUser = create.newRecord(Accounts.ACCOUNTS);
    newUser.setFirstName(account.getFirstName());
    newUser.setLastName(account.getLastName());
    newUser.setEmail(account.getEmail());
    newUser.setPassword(account.getPassword());
    newUser.setCreatedOn(LocalDateTime.now());
    newUser.store();
  }

  @Override
  public Optional<AccountsRecord> findUserByEmail(String email) {
    return Optional.ofNullable(create.fetchOne(Accounts.ACCOUNTS, Accounts.ACCOUNTS.EMAIL.eq(email)));
  }
}
