package com.dglisic.zakazime.domain;

import com.dglisic.zakazime.controller.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import model.tables.records.AccountRecord;

@Builder
@AllArgsConstructor
@Getter
public class User {
  private int id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String password;
  private boolean isEnabled;
  private Role role;

  public User(AccountRecord accountRecord, Role role) {
    this.id = accountRecord.getId();
    this.firstName = accountRecord.getFirstName();
    this.lastName = accountRecord.getLastName();
    this.email = accountRecord.getEmail();
    this.password = accountRecord.getPassword();
    this.isEnabled = accountRecord.getIsEnabled();
    this.role = role;
  }

  public User(RegistrationRequest registrationRequest, Role role) {
    this.firstName = registrationRequest.firstName();
    this.lastName = registrationRequest.lastName();
    this.email = registrationRequest.email();
    this.password = registrationRequest.password();
    this.isEnabled = true;
    this.role = role;
  }
}
