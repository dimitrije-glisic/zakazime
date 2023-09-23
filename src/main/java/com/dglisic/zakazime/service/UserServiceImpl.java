package com.dglisic.zakazime.service;

import com.dglisic.zakazime.domain.UserDTO;
import com.dglisic.zakazime.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void saveUser(UserDTO user) {
    userRepository.saveUser(user);
  }

  @Override
  public UserDTO findUserByEmail(String email) {
    var user = userRepository.findUserByEmail(email);
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
  }

}
