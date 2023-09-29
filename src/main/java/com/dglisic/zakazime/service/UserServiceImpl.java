package com.dglisic.zakazime.service;

import com.dglisic.zakazime.repository.UserRepository;
import java.util.Optional;
import model.tables.records.AccountsRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void registerUser(AccountsRecord account) {
    userRepository.saveUser(account);
  }

  @Override
  public AccountsRecord findUserByEmailOrElseThrow(String email) {
    Optional<AccountsRecord> user = userRepository.findUserByEmail(email);
    return user.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public AccountsRecord loginUser(String email, String password) throws ApplicationException {
    var user = userRepository.findUserByEmail(email);
    if (user.isPresent()) {
      if (user.get().getPassword().equals(password)) {
        return user.get();
      } else {
        throw new ApplicationException("Wrong password", HttpStatus.BAD_REQUEST);
      }
    } else {
      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
    }
  }

}
