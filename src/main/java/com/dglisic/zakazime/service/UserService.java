package com.dglisic.zakazime.service;

import model.tables.records.AccountsRecord;

public interface UserService {

  void registerUser(AccountsRecord account);

  AccountsRecord findUserByEmail(String email);

  AccountsRecord loginUser(String email, String password);
}
