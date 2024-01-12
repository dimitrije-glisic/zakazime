package com.dglisic.zakazime.common.config.security;

import com.dglisic.zakazime.user.repository.RoleRepository;
import com.dglisic.zakazime.user.repository.UserRepository;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    Optional<Account> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(
          "No user found with email: " + email);
    }
    Account user = userOptional.get();

    Optional<Role> roleOptional = roleRepository.findById(user.getRoleId());
    if (roleOptional.isEmpty()) {
      // this should never happen
      throw new UsernameNotFoundException(
          "No role found with id: " + user.getRoleId());
    }
    Role role = roleOptional.get();

    return User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .disabled(!user.getIsEnabled())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .authorities("ROLE_" + role.getName())
        .build();
  }
}