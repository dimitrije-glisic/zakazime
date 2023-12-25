package com.dglisic.zakazime.user.controller;

import com.dglisic.zakazime.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jooq.tables.pojos.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/login")
  public ResponseEntity<Account> login(Principal authenticatedUser) {
    if (authenticatedUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Account user = userService.findUserByEmailOrElseThrow(authenticatedUser.getName());
    return ResponseEntity.ok(user);
  }

  @GetMapping("/user")
  public ResponseEntity<Account> getUser(Principal authenticatedUser) {
    if (authenticatedUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Account user = userService.findUserByEmailOrElseThrow(authenticatedUser.getName());
    return ResponseEntity.ok().body(user);
  }

  @GetMapping("/token")
  public Map<String, String> token(HttpSession session) {
    return Collections.singletonMap("token", session.getId());
  }

  @GetMapping("/resource")
  @ResponseBody
  public Map<String, Object> home() {
    Map<String, Object> model = new HashMap<>();
    model.put("id", UUID.randomUUID().toString());
    model.put("content", "Hello World");
    return model;
  }

  @PostMapping("/register")
  public Account registerUser(@RequestBody final RegistrationRequest registrationRequest) {
    return userService.registerUser(registrationRequest);
  }

}
