package com.dglisic.zakazime.service;

import com.dglisic.zakazime.domain.UserDTO;

public interface UserService {

  void saveUser(UserDTO user);

  UserDTO findUserByEmail(String email);

}
