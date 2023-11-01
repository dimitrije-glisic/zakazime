package com.dglisic.zakazime.service;

import com.dglisic.zakazime.controller.RegistrationRequest;
import com.dglisic.zakazime.controller.UserDTO;
import com.dglisic.zakazime.domain.User;
import model.tables.records.BusinessProfileRecord;

public interface UserService {

  UserDTO registerUser(RegistrationRequest registrationRequest);

  /**
   * Finds user by email
   *
   * @param email user email
   * @return user
   * @throws ApplicationException if user is not found
   */
  User findUserByEmailOrElseThrow(String email) throws ApplicationException;

  /**
   * Logs in user
   *
   * @param email email
   * @param password password
   * @return account
   * @throws ApplicationException if user is not found or password is wrong
   */
  User loginUser(String email, String password) throws ApplicationException;

  void finishBusinessUserRegistration(String ownerEmail, BusinessProfileRecord businessProfile);

//  User registerBusinessUser(User accountRecord);
}
