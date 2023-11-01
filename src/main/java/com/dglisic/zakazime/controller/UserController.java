package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.domain.User;
import com.dglisic.zakazime.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/user")
  @ResponseBody
  public Principal user(Principal user) {
    return user;
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

  @Autowired
  private UserService userService;

  @GetMapping("/users/{email}")
  public User fullUser(@PathVariable String email) {
    return userService.findUserByEmailOrElseThrow(email);
  }

//  @GetMapping(value = "/{path:[^\\.]*}")
//  public String redirect() {
//    return "forward:/";
//  }

  @PostMapping("/users/register")
  public UserDTO registerUser(@RequestBody RegistrationRequest registrationRequest) {
    return userService.registerUser(registrationRequest);
  }

}
