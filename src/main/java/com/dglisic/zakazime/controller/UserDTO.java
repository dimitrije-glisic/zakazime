package com.dglisic.zakazime.controller;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String password;
  private final String token;
  private final String userType;
  private final String registrationStatus;

  public UserDTO(String firstName, String lastName, String email, String token, String userType, String registrationStatus) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.token = token;
    this.password = null;
    this.userType = userType;
    this.registrationStatus = registrationStatus;
  }

  public UserDTO(String firstName, String lastName, String email, String userType, String registrationStatus) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = null;
    this.token = null;
    this.userType = userType;
    this.registrationStatus = registrationStatus;
  }

  //getters
  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getToken() {
    return token;
  }

  public String getUserType() {
    return userType;
  }

  public String getRegistrationStatus() {
    return registrationStatus;
  }

}