package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import model.tables.records.AccountsRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<MessageDTO> registerUser(@Valid @RequestBody UserDTO user) {
    userService.registerUser(userMapper.mapToAccount(user));
    return ResponseEntity.created(URI.create("/users/" + user.email())).body(new MessageDTO("User created"));
  }

  @PostMapping("/login")
  public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody CredentialsDTO credentials) {
    AccountsRecord account = userService.loginUser(credentials.email(), credentials.password());
    return ResponseEntity.ok(userMapper.mapToUserDTO(account));
  }

  @GetMapping("/users/{email}")
  public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
    AccountsRecord userByEmail = userService.findUserByEmail(email);
    return ResponseEntity.ok(userMapper.mapToUserDTO(userByEmail));
  }

}
