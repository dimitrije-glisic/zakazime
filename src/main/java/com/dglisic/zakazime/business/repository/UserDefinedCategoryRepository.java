package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jooq.tables.pojos.UserDefinedCategory;

public interface UserDefinedCategoryRepository {
  Optional<UserDefinedCategory> findById(@NotNull final Integer id);

  boolean allExist(Set<Integer> categoryIds);

  boolean exists(Integer integer);

  void delete(Integer id);

  UserDefinedCategory store(UserDefinedCategory category);

  List<UserDefinedCategory> getAll();

  void update(UserDefinedCategory category);

  Optional<UserDefinedCategory> findUserDefinedCategoryById(Integer categoryId);

  void createUserDefinedCategory(UserDefinedCategory category);
}
