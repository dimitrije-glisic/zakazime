package com.dglisic.zakazime.customer.controller;

import com.dglisic.zakazime.business.controller.BusinessDTO;
import com.dglisic.zakazime.business.controller.BusinessMapper;
import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.service.BusinessService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/public")
@RequiredArgsConstructor
public class CustomerController {

  private final BusinessService businessService;
  private final BusinessMapper businessMapper;

  @GetMapping("/service-providers")
  public ResponseEntity<List<BusinessDTO>> getAllServiceProviders() {
    List<Business> all = businessService.getAll();
    List<BusinessDTO> businessProfileDTOs = all.stream().map(businessMapper::mapToBusinessProfileDTO).toList();
    return ResponseEntity.ok(businessProfileDTOs);
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
