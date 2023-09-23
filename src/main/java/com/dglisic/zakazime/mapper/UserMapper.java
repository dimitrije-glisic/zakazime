package com.dglisic.zakazime.mapper;

import com.dglisic.zakazime.dto.UserDTO;

import org.springframework.stereotype.Component;

import model.tables.records.AccountsRecord;

@Component
public class UserMapper {

  public UserDTO mapToUserDTO(AccountsRecord user) {
    return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
  }

}
