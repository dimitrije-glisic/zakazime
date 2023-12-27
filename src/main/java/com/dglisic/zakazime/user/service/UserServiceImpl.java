package com.dglisic.zakazime.user.service;

import static com.dglisic.zakazime.user.service.UserServiceImpl.RoleName.SERVICE_PROVIDER;
import static com.dglisic.zakazime.user.service.UserServiceImpl.RoleName.USER;

import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.repository.RoleRepository;
import com.dglisic.zakazime.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public Account registerUser(final RegistrationRequest registrationRequest) {
    validateOnRegistration(registrationRequest);
    final Account newUserAccount = fromRegistrationRequest(registrationRequest);
    return userRepository.saveUser(newUserAccount);
  }

  @Override
  public Account findUserByEmailOrElseThrow(String email) {
    Optional<Account> user = userRepository.findByEmail(email);
    return user.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public Account getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new ApplicationException("User not authenticated", HttpStatus.UNAUTHORIZED);
    }
    return findUserByEmailOrElseThrow(authentication.getName());
  }

  @Override
  public void setRoleToServiceProvider(Account user) {
    Role currentRole = roleRepository.findById(user.getRoleId())
        // this should never happen; this would mean that user has not been properly created
        .orElseThrow(() -> new ApplicationException("Role not found", HttpStatus.INTERNAL_SERVER_ERROR));

    Role serviceProviderRole = roleRepository.findByName(SERVICE_PROVIDER.value).get();

    if (USER.value.equals(currentRole.getName())) {
        userRepository.updateRole(user, serviceProviderRole);
    }
  }

  private Account fromRegistrationRequest(final RegistrationRequest registrationRequest) {
    final Role role = fromString(registrationRequest.role());
    final LocalDateTime createdOn = LocalDateTime.now();
    return new Account(
        null,
        registrationRequest.firstName(),
        registrationRequest.lastName(),
        registrationRequest.password(),
        registrationRequest.email(),
        true,
        role.getId(),
        createdOn,
        null
    );
  }

  private Role fromString(String roleName) {
    Optional<Role> role = roleRepository.findByName(roleName);
    return role.orElseThrow(() ->
        new ApplicationException("Role not found", HttpStatus.BAD_REQUEST)
    );
  }

  private void validateOnRegistration(RegistrationRequest request) {
    userRepository.findByEmail(request.email()).ifPresent(user -> {
      throw new ApplicationException("User with this email already exists", HttpStatus.BAD_REQUEST);
    });

    roleRepository.findByName(request.role()).orElseThrow(() ->
        new ApplicationException("Role not found", HttpStatus.BAD_REQUEST)
    );
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


  @AllArgsConstructor
  enum RoleName {
    USER("USER"),
    SERVICE_PROVIDER("SERVICE_PROVIDER"),
    ADMIN("ADMIN"),
    ;
    private final String value;
  }
}
