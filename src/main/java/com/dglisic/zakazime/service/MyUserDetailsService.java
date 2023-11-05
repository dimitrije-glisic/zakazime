package com.dglisic.zakazime.service;

import static org.springframework.security.core.userdetails.User.builder;

import com.dglisic.zakazime.domain.User;
import com.dglisic.zakazime.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public MyUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(
          "No user found with email: " + email);
    }
    User user = userOptional.get();

    return builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .disabled(!user.isEnabled())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .authorities("ROLE_" + user.getRole().getName())
        .build();
  }
}