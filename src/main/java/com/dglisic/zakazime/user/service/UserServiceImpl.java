package com.dglisic.zakazime.user.service;

import static com.dglisic.zakazime.user.service.UserServiceImpl.RoleName.SERVICE_PROVIDER;
import static com.dglisic.zakazime.user.service.UserServiceImpl.RoleName.USER;

import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.repository.RoleRepository;
import com.dglisic.zakazime.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Role;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

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

  /**
   * Retrieves the logged-in user.
   *
   * @return The logged-in user's account information.
   * @throws ApplicationException If the user is not authenticated, or if the user is not found.
   */
  @Override
  public Account requireLoggedInUser() throws ApplicationException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new ApplicationException("User not authenticated", HttpStatus.UNAUTHORIZED);
    }
    return findUserByEmailOrElseThrow(authentication.getName());
  }


  //not needed anymore?
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

  @Override
  @Transactional
  public Account createBusinessUser(Business business) {
    final String username = generateUsername(business.getName());
    final String password = generatePassword();
    final Account businessUser = new Account();
    businessUser.setFirstName(business.getName());
    businessUser.setLastName(business.getName());
    businessUser.setEmail(username);
    businessUser.setPassword(password);
    businessUser.setIsEnabled(true);
    businessUser.setCreatedOn(LocalDateTime.now());
    businessUser.setRoleId(roleRepository.findByName(SERVICE_PROVIDER.value).get().getId());
    final Account savedUser = userRepository.saveUser(businessUser);
    this.userRepository.linkBusinessProfileToUser(savedUser.getId(), business.getId());
    return savedUser;
  }

  private String generateUsername(String name) {
    return name.toLowerCase().replaceAll("\\s+", "") + "_user" + UUID.randomUUID().toString().substring(0, 4);
  }

  private String generatePassword() {
    return UUID.randomUUID().toString().substring(0, 8);
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

  @AllArgsConstructor
  public enum RoleName {
    USER("USER"),
    SERVICE_PROVIDER("SERVICE_PROVIDER"),
    ADMIN("ADMIN"),
    ;
    private final String value;
  }
}
