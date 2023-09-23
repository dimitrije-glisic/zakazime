package com.dglisic.zakazime.service;

import com.dglisic.zakazime.dto.CredentialsDTO;
import com.dglisic.zakazime.dto.UserDTO;

public interface UserService {

  void registerUser(UserDTO user);

  UserDTO findUserByEmail(String email);

  UserDTO loginUser(CredentialsDTO credentials);
}
