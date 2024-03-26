package com.dglisic.zakazime.user.repository.impl;

import static jooq.Tables.ACCOUNT;

import com.dglisic.zakazime.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.BusinessAccountMap;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Role;
import jooq.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext dsl;

  public UserRepositoryImpl(DSLContext dslContext) {
    this.dsl = dslContext;
  }

  @Override
  @Transactional
  public Account saveUser(Account user) {
    final AccountRecord newUserAccount = dsl.newRecord(ACCOUNT, user);
    newUserAccount.store();

    return newUserAccount.into(Account.class);
  }

  @Override
  public Optional<Account> findByEmail(final String email) {
    Account user = dsl.selectFrom(ACCOUNT)
        .where(ACCOUNT.EMAIL.eq(email))
        .fetchOneInto(Account.class);
    return Optional.ofNullable(user);
  }

  @Override
  public void linkBusinessProfileToUser(int userId, int businessProfileId) {
    int insertResult = dsl.insertInto(BusinessAccountMap.BUSINESS_ACCOUNT_MAP)
        .set(BusinessAccountMap.BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, userId)
        .set(BusinessAccountMap.BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessProfileId)
        .execute();
    if (insertResult == 0) {
      throw new RuntimeException("Business profile not linked to user");
    }
  }

  @Override
  public List<Account> getAllUsers() {
    return dsl.selectFrom(ACCOUNT).fetchInto(Account.class);
  }

  @Override
  public void updateRole(Account user, Role role) {
    dsl.update(ACCOUNT)
        .set(ACCOUNT.ROLE_ID, role.getId())
        .where(ACCOUNT.ID.eq(user.getId()))
        .execute();
  }

  @Override
  public Optional<Account> findById(Integer id) {
    Account user = dsl.selectFrom(ACCOUNT)
        .where(ACCOUNT.ID.eq(id))
        .fetchOneInto(Account.class);
    return Optional.ofNullable(user);
  }

  @Override
  public Account updateUser(Account user) {
    return dsl.update(ACCOUNT)
        .set(ACCOUNT.FIRST_NAME, user.getFirstName())
        .set(ACCOUNT.LAST_NAME, user.getLastName())
        .set(ACCOUNT.EMAIL, user.getEmail())
        .set(ACCOUNT.PHONE, user.getPhone())
        .set(ACCOUNT.PASSWORD, user.getPassword())
        .where(ACCOUNT.ID.eq(user.getId()))
        .returning()
        .fetchOneInto(Account.class);
  }

}
