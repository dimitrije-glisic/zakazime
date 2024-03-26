package com.dglisic.zakazime.user.controller;

import com.dglisic.zakazime.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import jooq.tables.pojos.Account;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  @PostMapping("/register")
  public Account registerUser(@RequestBody final RegistrationRequest registrationRequest) {
    logger.info("Registering user: {}", registrationRequest);
    return userService.registerUser(registrationRequest);
  }

  @GetMapping("/login")
  public ResponseEntity<Account> login(final Principal authenticatedUser) {
    logger.info("Login user: {}", authenticatedUser);
    if (authenticatedUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final Account user = userService.findUserByEmailOrElseThrow(authenticatedUser.getName());
    return ResponseEntity.ok(user);
  }

  @PutMapping("/users/{userId}")
  public ResponseEntity<Account> updateUser(
      @PathVariable final Integer userId,
      @RequestBody @Valid final UpdateUserInfoRequest updateRequest) {
    logger.info("Updating user: {}", updateRequest);
    final Account updatedUser = userService.updateUser(userId, updateRequest);
    return ResponseEntity.ok(updatedUser);
  }

}
