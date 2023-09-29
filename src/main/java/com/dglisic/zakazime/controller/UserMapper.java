package com.dglisic.zakazime.controller;

import org.springframework.stereotype.Component;

import model.tables.records.AccountsRecord;

@Component
public class UserMapper {

  public UserDTO mapToUserDTO(AccountsRecord user) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
  }

  public UserDTO mapToUserDTOWithToken(AccountsRecord user, String token) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, token);
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
