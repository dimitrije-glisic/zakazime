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
  public AccountRecord registerUser(AccountRecord account) {
    account.setRegistrationStatus(UserRegistrationStatus.COMPLETED.toString());
    return userRepository.saveUser(account);
  }

  @Override
  public AccountRecord registerBusinessUser(AccountRecord accountRecord) {
    accountRecord.setRegistrationStatus(UserRegistrationStatus.INITIAL.toString());
    return userRepository.saveUser(accountRecord);
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
    Optional<AccountRecord> userByEmail = userRepository.findUserByEmail(ownerEmail);
    if (userByEmail.isPresent()) {
      AccountRecord user = userByEmail.get();
      if (user.getUserType().equals(UserType.CUSTOMER.toString())) {
        throw new ApplicationException("This is permitted only for business users", HttpStatus.BAD_REQUEST);
      }
      if (user.getRegistrationStatus().equals(UserRegistrationStatus.COMPLETED.toString())) {
        throw new ApplicationException("User is already registered", HttpStatus.BAD_REQUEST);
      }
      int businessProfileId = userRepository.saveBusinessProfile(businessProfile);
      final int userId = user.getId();
      userRepository.linkBusinessProfileToUser(userId, businessProfileId);
      userRepository.updateRegistrationStatus(user.getId(), UserRegistrationStatus.COMPLETED);
    } else {
      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
    }
  }

}
