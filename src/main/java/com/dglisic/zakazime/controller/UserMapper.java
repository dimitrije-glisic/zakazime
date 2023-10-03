package com.dglisic.zakazime.controller;

import org.springframework.stereotype.Component;

import model.tables.records.AccountRecord;

@Component
public class UserMapper {

  public UserDTO mapToUserDTO(AccountRecord user) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
  }

  public UserDTO mapToUserDTOWithToken(AccountRecord user, String token) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, token);
  }

  public AccountRecord mapToAccount(UserDTO user) {
    AccountRecord AccountRecord = new AccountRecord();
    AccountRecord.setFirstName(user.firstName());
    AccountRecord.setLastName(user.lastName());
    AccountRecord.setEmail(user.email());
    AccountRecord.setPassword(user.password());
    return AccountRecord;
  }

}
