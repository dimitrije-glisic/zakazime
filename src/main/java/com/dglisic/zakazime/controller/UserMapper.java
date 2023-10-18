package com.dglisic.zakazime.controller;

import org.springframework.stereotype.Component;

import model.tables.records.AccountRecord;

@Component
public class UserMapper {

  public UserDTO mapToUserDTO(AccountRecord user) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserType(),
        user.getRegistrationStatus());
  }

  public UserDTO mapToUserDTOWithToken(AccountRecord user, String token) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), token, user.getUserType(),
        user.getRegistrationStatus());
  }

  public AccountRecord mapToAccount(UserRegistrationDTO registrationDTO) {
    AccountRecord accountRecord = new AccountRecord();
    accountRecord.setFirstName(registrationDTO.firstName());
    accountRecord.setLastName(registrationDTO.lastName());
    accountRecord.setEmail(registrationDTO.email());
    accountRecord.setPassword(registrationDTO.password());
    accountRecord.setUserType(registrationDTO.userType().toString());
    return accountRecord;
  }

}
