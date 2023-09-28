package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.dto.CredentialsDTO;
import com.dglisic.zakazime.dto.UserDTO;
import com.dglisic.zakazime.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import jakarta.validation.Valid;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<MessageDTO> registerUser(@Valid @RequestBody UserDTO user) {
    userService.registerUser(user);
    return ResponseEntity.created(URI.create("/users/" + user.email())).body(new MessageDTO("User created"));
  }

  @PostMapping("/login")
  public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody CredentialsDTO credentials) {
    return ResponseEntity.ok(userService.loginUser(credentials));
  }

  @GetMapping("/users/{email}")
  public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
    return ResponseEntity.ok(userService.findUserByEmail(email));
  }

}
