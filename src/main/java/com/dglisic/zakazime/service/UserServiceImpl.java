package com.dglisic.zakazime.service;

import com.dglisic.zakazime.repository.UserRepository;
import java.util.Optional;
import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void registerUser(AccountRecord account) {
    userRepository.saveUser(account);
  }

  @Override
  public AccountRecord findUserByEmailOrElseThrow(String email) {
    Optional<AccountRecord> user = userRepository.findUserByEmail(email);
    return user.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public AccountRecord loginUser(String email, String password) throws ApplicationException {
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

  @Override
  public void finishBusinessUserRegistration(String ownerEmail, BusinessProfileRecord businessProfile) {
    Optional<AccountRecord> user = userRepository.findUserByEmail(ownerEmail);
    if (user.isPresent()) {
      int businessProfileId = userRepository.save(businessProfile);
      final int userId = user.get().getId();
      userRepository.linkBusinessProfileToUser(userId, businessProfileId);
    } else {
      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
    }
  }

}
