package com.dglisic.zakazime.repository;


import com.dglisic.zakazime.domain.User;
import java.util.Optional;

public interface UserRepository {

  User saveUser(User account);

  Optional<User> findByEmail(String email);

  void linkBusinessProfileToUser(int userId, int businessProfileId);
}
