package com.dglisic.zakazime.domain;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String password,
                      @NotBlank String email) {
  public UserDTO(String firstName, String lastName, String email) {
    this(firstName, lastName, "", email);
  }
}
