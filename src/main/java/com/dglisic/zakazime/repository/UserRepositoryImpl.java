package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.domain.UserDTO;
import java.time.LocalDateTime;
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
  public void saveUser(UserDTO user) {
    var newUser = create.newRecord(Accounts.ACCOUNTS);
    newUser.setUsername(user.username());
    newUser.setPassword(user.password());
    newUser.setEmail(user.email());
    newUser.setCreatedOn(LocalDateTime.now());
    newUser.store();
  }

  @Override
  public AccountsRecord findUserByEmail(String email) {
    return create.fetchOne(Accounts.ACCOUNTS, Accounts.ACCOUNTS.EMAIL.eq(email));
  }
}
