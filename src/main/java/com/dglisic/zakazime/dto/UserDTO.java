package com.dglisic.zakazime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String password,
                      @NotBlank String email) {
  public UserDTO(String firstName, String lastName, String email) {
    this(firstName, lastName, null, email);
  }
}
