package com.dglisic.zakazime.controller;

import model.tables.records.AccountsRecord;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDTO mapToUserDTO(AccountsRecord user) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
  }

  public AccountsRecord mapToAccount(UserDTO user) {
    AccountsRecord accountsRecord = new AccountsRecord();
    accountsRecord.setFirstName(user.firstName());
    accountsRecord.setLastName(user.lastName());
    accountsRecord.setEmail(user.email());
    accountsRecord.setPassword(user.password());
    return accountsRecord;
  }

}
