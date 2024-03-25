package com.dglisic.zakazime.user.service;

import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Role;

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

  Account findUserByIdOrElseThrow(Integer id) throws ApplicationException;

  Account requireLoggedInUser();

  Account createBusinessUser(Business business);

}
