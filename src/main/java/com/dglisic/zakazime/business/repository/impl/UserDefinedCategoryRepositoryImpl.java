package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.UserDefinedCategory.USER_DEFINED_CATEGORY;

import com.dglisic.zakazime.business.repository.UserDefinedCategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jooq.tables.daos.UserDefinedCategoryDao;
import jooq.tables.pojos.UserDefinedCategory;
import jooq.tables.records.UserDefinedCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDefinedCategoryRepositoryImpl implements UserDefinedCategoryRepository {
  private final DSLContext dsl;
  private final UserDefinedCategoryDao dao;

  @Override
  @Cacheable("categories")
  public Optional<UserDefinedCategory> findById(Integer id) {
    return Optional.ofNullable(dsl.selectFrom(USER_DEFINED_CATEGORY)
        .where(USER_DEFINED_CATEGORY.ID.eq(id))
        .fetchOneInto(UserDefinedCategory.class));
  }

  @Override
  public boolean allExist(Set<Integer> categoryIds) {
    List<Integer> fetch = dsl.select(USER_DEFINED_CATEGORY.ID)
        .from(USER_DEFINED_CATEGORY)
        .where(USER_DEFINED_CATEGORY.ID.in(categoryIds))
        .fetchInto(Integer.class);
    return fetch.size() == categoryIds.size();
  }

  @Override
  public boolean exists(final Integer id) {
    return dao.findById(id) != null;
  }

  @Override
  public void delete(final Integer id) {
    dao.deleteById(id);
  }

  @Override
  public UserDefinedCategory store(final UserDefinedCategory category) {
    UserDefinedCategoryRecord categoryRecord = dsl.newRecord(USER_DEFINED_CATEGORY, category);
    categoryRecord.store();
    return categoryRecord.into(UserDefinedCategory.class);
  }

  @Override
  public List<UserDefinedCategory> getAll() {
    return dao.findAll();
  }

  @Override
  public void update(UserDefinedCategory category) {
    dsl.update(USER_DEFINED_CATEGORY)
        .set(USER_DEFINED_CATEGORY.TITLE, category.getTitle())
        .where(USER_DEFINED_CATEGORY.ID.eq(category.getId()))
        .execute();
  }

  @Override
  public Optional<UserDefinedCategory> findUserDefinedCategoryById(Integer categoryId) {
    return Optional.ofNullable(dsl.selectFrom(USER_DEFINED_CATEGORY)
        .where(USER_DEFINED_CATEGORY.ID.eq(categoryId))
        .fetchOneInto(UserDefinedCategory.class));
  }

}
