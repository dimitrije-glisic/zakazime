package com.dglisic.zakazime.user.repository;


import com.dglisic.zakazime.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  User saveUser(User account);

  Optional<User> findByEmail(String email);

  void linkBusinessProfileToUser(int userId, int businessProfileId);

  List<User> getAllUsers();
}
