package com.dglisic.zakazime.controller;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
record UserDTO(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String email,
               @NotBlank String password, String token) {
  public UserDTO(String firstName, String lastName, String email) {
    this(firstName, lastName, email, null, null);
  }
}
