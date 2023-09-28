package com.dglisic.zakazime.controller;

import jakarta.validation.constraints.NotBlank;

record CredentialsDTO(@NotBlank String email, @NotBlank String password) {
}
