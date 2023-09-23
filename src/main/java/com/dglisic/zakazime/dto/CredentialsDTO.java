package com.dglisic.zakazime.dto;

import jakarta.validation.constraints.NotBlank;

public record CredentialsDTO(@NotBlank String email, @NotBlank String password) {
}
