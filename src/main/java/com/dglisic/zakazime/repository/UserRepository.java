package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.domain.UserDTO;
import model.tables.records.AccountsRecord;

public interface UserRepository {

  void saveUser(UserDTO user);

  AccountsRecord findUserByEmail(String email);

}
