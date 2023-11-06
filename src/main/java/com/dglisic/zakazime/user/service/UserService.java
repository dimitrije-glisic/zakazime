package com.dglisic.zakazime.user.service;

import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.controller.UserDTO;
import com.dglisic.zakazime.user.domain.User;
import java.util.List;

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
   * @param email    email
   * @param password password
   * @return account
   * @throws ApplicationException if user is not found or password is wrong
   */
  User loginUser(String email, String password) throws ApplicationException;

  List<User> getAllUsers();

//  void finishBusinessUserRegistration(String ownerEmail, BusinessProfile businessProfile);

//  User registerBusinessUser(User accountRecord);

}
