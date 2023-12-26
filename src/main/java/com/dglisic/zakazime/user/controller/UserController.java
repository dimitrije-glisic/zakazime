package com.dglisic.zakazime.user.controller;

import com.dglisic.zakazime.user.service.UserService;
import java.security.Principal;
import jooq.tables.pojos.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public Account registerUser(@RequestBody final RegistrationRequest registrationRequest) {
    return userService.registerUser(registrationRequest);
  }

  @GetMapping("/login")
  public ResponseEntity<Account> login(Principal authenticatedUser) {
    if (authenticatedUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Account user = userService.findUserByEmailOrElseThrow(authenticatedUser.getName());
    return ResponseEntity.ok(user);
  }

//  @GetMapping("/token")
//  public Map<String, String> token(HttpSession session) {
//    return Collections.singletonMap("token", session.getId());
//  }

}
