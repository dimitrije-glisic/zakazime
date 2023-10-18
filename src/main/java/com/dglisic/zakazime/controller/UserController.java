package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.config.JwtProvider;
import com.dglisic.zakazime.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import model.tables.records.AccountRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;
  private final BusinessProfileMapper businessProfileMapper;
  private final JwtProvider jwtProvider;

  public UserController(UserService userService, UserMapper userMapper, BusinessProfileMapper businessProfileMapper,
                        JwtProvider jwtProvider) {
    this.userService = userService;
    this.userMapper = userMapper;
    this.businessProfileMapper = businessProfileMapper;
    this.jwtProvider = jwtProvider;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO user) {
    AccountRecord account = userService.registerUser(userMapper.mapToAccount(user));
    String token = jwtProvider.generateToken(account.getEmail());
    UserDTO userDTO = userMapper.mapToUserDTOWithToken(account, token);
    return ResponseEntity.created(URI.create("/users/" + userDTO.email())).body(userDTO);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody CredentialsDTO credentials) {
    AccountRecord account = userService.loginUser(credentials.email(), credentials.password());
    String token = jwtProvider.generateToken(account.getEmail());
    UserDTO userDTO = userMapper.mapToUserDTOWithToken(account, token);
    return ResponseEntity.ok(userDTO);
  }

  @GetMapping("/users/{email}")
  public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
    AccountRecord userByEmail = userService.findUserByEmailOrElseThrow(email);
    return ResponseEntity.ok(userMapper.mapToUserDTO(userByEmail));
  }

  @PostMapping("/users/{email}/finish-registration")
  public ResponseEntity<MessageDTO> finishRegistration(@PathVariable String email,
                                                       @Valid @RequestBody BusinessProfileDTO businessProfileDTO) {
    userService.finishBusinessUserRegistration(email, businessProfileMapper.mapToBusinessProfile(businessProfileDTO));
    return ResponseEntity.ok(new MessageDTO("Registration finished successfully"));
  }

}
