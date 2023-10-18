package com.dglisic.zakazime.repository;


import com.dglisic.zakazime.service.UserRegistrationStatus;
import java.util.Optional;
import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;

public interface UserRepository {

  AccountRecord saveUser(AccountRecord account);

  void updateRegistrationStatus(Integer accountId, UserRegistrationStatus status);

  Optional<AccountRecord> findUserByEmail(String email);

  int saveBusinessProfile(BusinessProfileRecord businessProfile);

  void linkBusinessProfileToUser(int userId, int businessProfileId);
}
