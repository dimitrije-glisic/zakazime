package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.service.UserRegistrationStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import model.tables.Account;
import model.tables.BusinessAccountMap;
import model.tables.BusinessProfile;
import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext create;

  public UserRepositoryImpl(DSLContext dslContext) {
    this.create = dslContext;
  }

  @Override
  public AccountRecord saveUser(AccountRecord account) {
    var newUser = create.newRecord(Account.ACCOUNT);
    newUser.setFirstName(account.getFirstName());
    newUser.setLastName(account.getLastName());
    newUser.setEmail(account.getEmail());
    newUser.setPassword(account.getPassword());
    newUser.setUserType(account.getUserType());
    newUser.setRegistrationStatus(account.getRegistrationStatus());
    newUser.setCreatedOn(LocalDateTime.now());
    newUser.store();
    return newUser;
  }

  @Override
  public void updateRegistrationStatus(Integer accountId, UserRegistrationStatus status) {
    create.update(Account.ACCOUNT)
        .set(Account.ACCOUNT.REGISTRATION_STATUS, status.toString())
        .where(Account.ACCOUNT.ID.eq(accountId))
        .execute();
  }

  @Override
  public Optional<AccountRecord> findUserByEmail(String email) {
    return Optional.ofNullable(create.fetchOne(Account.ACCOUNT, Account.ACCOUNT.EMAIL.eq(email)));
  }

  @Override
  public void linkBusinessProfileToUser(int userId, int businessProfileId) {
    int insertResult = create.insertInto(BusinessAccountMap.BUSINESS_ACCOUNT_MAP)
        .set(BusinessAccountMap.BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, userId)
        .set(BusinessAccountMap.BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessProfileId)
        .execute();
    if (insertResult == 0) {
      throw new RuntimeException("Business profile not linked to user");
    }
  }
}
