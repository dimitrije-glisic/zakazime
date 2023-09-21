package com.dglisic.zakazime.domain;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String password, @NotBlank String email) {
  private static final String UNDERSCORE = "_";


  public String username() {
    return firstName + UNDERSCORE + lastName;
  }

  public static String extractFirstName(String username) {
    return username.split(UNDERSCORE)[0];
  }

  public static String extractLastName(String username) {
    return username.split(UNDERSCORE)[1];
  }
}
