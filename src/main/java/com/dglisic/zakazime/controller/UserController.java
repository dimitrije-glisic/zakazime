package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.domain.UserDTO;
import com.dglisic.zakazime.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO user) {
    userService.saveUser(user);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/users/{email}")
  public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
    return ResponseEntity.ok(userService.findUserByEmail(email));
  }

}
