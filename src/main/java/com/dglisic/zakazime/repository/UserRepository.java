package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.dto.UserDTO;

import java.util.Optional;

import model.tables.records.AccountsRecord;

public interface UserRepository {

  void saveUser(UserDTO user);

  Optional<AccountsRecord> findUserByEmail(String email);

}
