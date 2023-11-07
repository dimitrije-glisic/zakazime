package com.dglisic.zakazime.user.controller;

import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/login")
  public ResponseEntity<UserDTO> login(Principal authenticatedUser, HttpSession session) {
    if (authenticatedUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    User user = userService.findUserByEmailOrElseThrow(authenticatedUser.getName());
    UserDTO userDTO = UserDTO.fromUser(user);
    session.setAttribute("user", userDTO);
    return ResponseEntity.ok(userDTO);
  }

  @GetMapping("/user")
  public ResponseEntity<UserDTO> getUser(HttpSession session) {
    UserDTO user = (UserDTO) session.getAttribute("USER");
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok().body(user);
  }

  @GetMapping("/user/role")
  public ResponseEntity<?> getUserRole(HttpSession session) {
    User user = (User) session.getAttribute("USER");
    if (user != null) {
      return ResponseEntity.ok().body(user.getRole());
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Logged In");
    }
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
  public UserDTO registerUser(@RequestBody RegistrationRequest registrationRequest) {
    return userService.registerUser(registrationRequest);
  }

  @GetMapping("/users/{email}")
  public User fullUser(@PathVariable String email) {
    return userService.findUserByEmailOrElseThrow(email);
  }

}
