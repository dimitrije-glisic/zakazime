package com.dglisic.zakazime.service;

import com.dglisic.zakazime.dto.CredentialsDTO;
import com.dglisic.zakazime.dto.UserDTO;
import com.dglisic.zakazime.exception.ApplicationException;
import com.dglisic.zakazime.mapper.UserMapper;
import com.dglisic.zakazime.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import model.tables.records.AccountsRecord;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public void registerUser(UserDTO user) {
    userRepository.saveUser(user);
  }

  @Override
  public UserDTO findUserByEmail(String email) {
    Optional<AccountsRecord> user = userRepository.findUserByEmail(email);
    return user.map(userMapper::mapToUserDTO)
            .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public UserDTO loginUser(CredentialsDTO credentials) {
    var user = userRepository.findUserByEmail(credentials.email());
    if (user.isPresent()) {
      if (user.get().getPassword().equals(credentials.password())) {
        return userMapper.mapToUserDTO(user.get());
      } else {
        throw new ApplicationException("Wrong password", HttpStatus.BAD_REQUEST);
      }
    } else {
      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
    }
  }

}
