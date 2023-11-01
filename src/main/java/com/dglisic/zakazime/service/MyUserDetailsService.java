package com.dglisic.zakazime.service;

import com.dglisic.zakazime.domain.Role;
import com.dglisic.zakazime.domain.User;
import com.dglisic.zakazime.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service("userDetailsService")
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

    return new org.springframework.security.core.userdetails.User(
      user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
      true, getAuthorities(user.getRole()));
  }

  private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
    return List.of(new SimpleGrantedAuthority(role.getName()));
  }

}
