package com.dglisic.zakazime.admin.controller;

import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

//  @GetMapping("/admin/users/{id}")
//  public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
//    return ResponseEntity.ok(userService.getUserById(id));
//  }
//
//  @PutMapping("/admin/users/{id}")
//  public ResponseEntity<UserDTO> updateUser(@PathVariable String id, UserDTO userDTO) {
//    UserDTO updated = userService.updateUser(id, userDTO);
//    return ResponseEntity.ok(updated);
//  }
//
//  @DeleteMapping("/admin/users/{id}")
//  public ResponseEntity<MessageDTO> deleteUser(@PathVariable String id) {
//    return ResponseEntity.ok(userService.deleteUser(id));
//  }
//
//  @PostMapping("/admin/users")
//  public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
//    return ResponseEntity.ok(userService.createUser(userDTO));
//  }


}
