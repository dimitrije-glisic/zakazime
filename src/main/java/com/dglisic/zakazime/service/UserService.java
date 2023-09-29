package com.dglisic.zakazime.service;

import model.tables.records.AccountsRecord;

public interface UserService {

  void registerUser(AccountsRecord account);

  /**
   * Finds user by email
   *
   * @param email user email
   * @return user
   * @throws ApplicationException if user is not found
   */
  AccountsRecord findUserByEmailOrElseThrow(String email) throws ApplicationException;

  /**
   * Logs in user
   *
   * @param email email
   * @param password password
   * @return account
   * @throws ApplicationException if user is not found or password is wrong
   */
  AccountsRecord loginUser(String email, String password) throws ApplicationException;
}
