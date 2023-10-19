package com.dglisic.zakazime.service;

import com.dglisic.zakazime.repository.BusinessRepository;
import com.dglisic.zakazime.repository.UserRepository;
import java.util.Optional;
import model.tables.records.AccountRecord;
import model.tables.records.BusinessProfileRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BusinessRepository businessRepository;

  public UserServiceImpl(UserRepository userRepository, BusinessRepository businessRepository) {
    this.userRepository = userRepository;
    this.businessRepository = businessRepository;
  }

  @Override
  public AccountRecord registerUser(AccountRecord account) {
    validateOnRegistration(account);
    account.setRegistrationStatus(UserRegistrationStatus.COMPLETED.toString());
    return userRepository.saveUser(account);
  }

  @Override
  public AccountRecord registerBusinessUser(AccountRecord account) {
    validateOnRegistration(account);
    account.setRegistrationStatus(UserRegistrationStatus.INITIAL.toString());
    return userRepository.saveUser(account);
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
      businessProfile.setStatus(BusinessProfileStatus.PENDING.toString());
      int businessProfileId = businessRepository.saveBusinessProfile(businessProfile);
      final int userId = user.getId();
      userRepository.linkBusinessProfileToUser(userId, businessProfileId);
      userRepository.updateRegistrationStatus(user.getId(), UserRegistrationStatus.COMPLETED);
    } else {
      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
    }
  }

  private void validateOnRegistration(AccountRecord account) {
    if (userRepository.findUserByEmail(account.getEmail()).isPresent()) {
      throw new ApplicationException("User with this email already exists", HttpStatus.BAD_REQUEST);
    }
  }

}
