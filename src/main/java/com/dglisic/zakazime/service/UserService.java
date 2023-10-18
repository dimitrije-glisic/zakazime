package com.dglisic.zakazime.service;

import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;

public interface UserService {

  AccountRecord registerUser(AccountRecord account);

  /**
   * Finds user by email
   *
   * @param email user email
   * @return user
   * @throws ApplicationException if user is not found
   */
  AccountRecord findUserByEmailOrElseThrow(String email) throws ApplicationException;

  /**
   * Logs in user
   *
   * @param email email
   * @param password password
   * @return account
   * @throws ApplicationException if user is not found or password is wrong
   */
  AccountRecord loginUser(String email, String password) throws ApplicationException;

  void finishBusinessUserRegistration(String ownerEmail, BusinessProfileRecord businessProfile);

  AccountRecord registerBusinessUser(AccountRecord accountRecord);
}
