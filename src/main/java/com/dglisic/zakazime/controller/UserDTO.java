package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.domain.User;

public record UserDTO(String firstName, String lastName, String email, boolean isEnabled, String role) {

  public static UserDTO fromUser(User savedUser) {
    return new UserDTO(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail(), savedUser.isEnabled(),
      savedUser.getRole().getName());
  }

}
