package com.dglisic.zakazime.user.service;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.controller.UserDTO;
import com.dglisic.zakazime.user.domain.Role;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.repository.RoleRepository;
import com.dglisic.zakazime.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BusinessRepository businessRepository;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BusinessRepository businessRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.businessRepository = businessRepository;
  }

  @Override
  public UserDTO registerUser(final RegistrationRequest registrationRequest) {
    validateOnRegistration(registrationRequest);
    final Role role = fromString(registrationRequest.role());
    final User user = new User(registrationRequest, role);
    final User savedUser = userRepository.saveUser(user);
    return UserDTO.fromUser(savedUser);
  }

  private Role fromString(String roleName) {
    Optional<Role> role = roleRepository.findByName(roleName);
    return role.orElseThrow(() ->
        new ApplicationException("Role not found", HttpStatus.BAD_REQUEST)
    );
  }

//  @Override
//  public User registerBusinessUser(User account) {
//    validateOnRegistration(account);
//    account.setRegistrationStatus(UserRegistrationStatus.INITIAL.toString());
//    return userRepository.saveUser(account);
//  }

  @Override
  public User findUserByEmailOrElseThrow(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public User loginUser(String email, String password) throws ApplicationException {
    var user = userRepository.findByEmail(email);
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
  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

//  //add roles authorization
//  @Override
//  public void finishBusinessUserRegistration(String ownerEmail, BusinessProfileRecord businessProfile) {
//    Optional<User> userByEmail = userRepository.findByEmail(ownerEmail);
//    if (userByEmail.isPresent()) {
//      User user = userByEmail.get();
//
////      if (user.getUserType().equals(UserType.CUSTOMER.toString())) {
////        throw new ApplicationException("This is permitted only for business users", HttpStatus.BAD_REQUEST);
////      }
//
////      if (user.getRegistrationStatus().equals(UserRegistrationStatus.COMPLETED.toString())) {
////        throw new ApplicationException("User is already registered", HttpStatus.BAD_REQUEST);
////      }
//
//      businessProfile.setStatus(BusinessProfileStatus.PENDING.toString());
//      int businessProfileId = businessRepository.saveBusinessProfile(businessProfile);
//      final int userId = user.getId();
//      userRepository.linkBusinessProfileToUser(userId, businessProfileId);
////      userRepository.updateRegistrationStatus(user.getId(), UserRegistrationStatus.COMPLETED);
//    } else {
//      throw new ApplicationException("User not found", HttpStatus.NOT_FOUND);
//    }
//  }

  private void validateOnRegistration(RegistrationRequest request) {
    userRepository.findByEmail(request.email()).ifPresent(user -> {
      throw new ApplicationException("User with this email already exists", HttpStatus.BAD_REQUEST);
    });

    roleRepository.findByName(request.role()).orElseThrow(() ->
        new ApplicationException("Role not found", HttpStatus.BAD_REQUEST)
    );
  }

}
