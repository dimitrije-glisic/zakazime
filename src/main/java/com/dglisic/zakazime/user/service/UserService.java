package com.dglisic.zakazime.user.service;

import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import java.util.List;
import jooq.tables.pojos.Account;

public interface UserService {

  Account registerUser(RegistrationRequest registrationRequest);

  /**
   * Finds user by email
   *
   * @param email user email
   * @return user
   * @throws ApplicationException if user is not found
   */
  Account findUserByEmailOrElseThrow(String email) throws ApplicationException;

  /**
   * Logs in user
   *
   * @param email    email
   * @param password password
   * @return account
   * @throws ApplicationException if user is not found or password is wrong
   */
  Account loginUser(String email, String password) throws ApplicationException;

  List<Account> getAllUsers();

}
