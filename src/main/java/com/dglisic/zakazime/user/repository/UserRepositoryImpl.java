package com.dglisic.zakazime.user.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.ROLE;

import com.dglisic.zakazime.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.tables.BusinessAccountMap;
import model.tables.records.AccountRecord;
import model.tables.records.RoleRecord;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext dsl;
  private final RoleRecordMapper roleRecordMapper;
  private final RoleRepository roleRepository;

  public UserRepositoryImpl(DSLContext dslContext, RoleRecordMapper roleRecordMapper, RoleRepository roleRepository) {
    this.dsl = dslContext;
    this.roleRecordMapper = roleRecordMapper;
    this.roleRepository = roleRepository;
  }

  @Override
  @Transactional
  public User saveUser(User user) {
    var newUserAccount = dsl.newRecord(ACCOUNT);
    newUserAccount.setFirstName(user.getFirstName());
    newUserAccount.setLastName(user.getLastName());
    newUserAccount.setEmail(user.getEmail());
    newUserAccount.setPassword(user.getPassword());
    newUserAccount.setRoleId(user.getRole().getId());
    newUserAccount.setIsEnabled(true);
    newUserAccount.setCreatedOn(LocalDateTime.now());
    newUserAccount.store();

    return new User(newUserAccount, roleRepository.findById(newUserAccount.getRoleId()).get());
  }

//  @Override
//  public void updateRegistrationStatus(Integer accountId, UserRegistrationStatus status) {
//    create.update(Account.ACCOUNT)
//        .set(Account.ACCOUNT.REGISTRATION_STATUS, status.toString())
//        .where(Account.ACCOUNT.ID.eq(accountId))
//        .execute();
//  }

  @Override
  public Optional<User> findByEmail(String email) {
    Record2<AccountRecord, RoleRecord> fetch = dsl
        .select(ACCOUNT, ROLE)
        .from(ACCOUNT)
        .join(ROLE)
        .on(ACCOUNT.ROLE_ID.equal(ROLE.ID))
        .where(ACCOUNT.EMAIL.equal(email))
        .fetchOne();

    if (fetch == null) {
      return Optional.empty();
    }

    AccountRecord account = fetch.value1();
    RoleRecord role = fetch.value2();
    User user = new User(account, roleRecordMapper.map(role));
    return Optional.of(user);
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
  public List<User> getAllUsers() {
    Result<Record2<AccountRecord, RoleRecord>> fetch = dsl.select(ACCOUNT, ROLE)
        .from(ACCOUNT)
        .join(ROLE)
        .on(ACCOUNT.ROLE_ID.eq(ROLE.ID))
        .fetch();

    return fetch.map(record -> new User(record.value1(), roleRecordMapper.map(record.value2())));
  }
}
