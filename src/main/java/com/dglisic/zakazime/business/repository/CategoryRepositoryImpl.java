package com.dglisic.zakazime.business.repository;

import static jooq.tables.ServiceCategory.SERVICE_CATEGORY;

import java.util.Optional;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final DSLContext dsl;

  @Override
  @Cacheable("categories")
  public Optional<ServiceCategory> findCategory(String categoryName) {
    ServiceCategory category = dsl.selectFrom(SERVICE_CATEGORY)
        .where(SERVICE_CATEGORY.TITLE.eq(categoryName))
        .fetchOneInto(ServiceCategory.class);
    return Optional.ofNullable(category);
  }

}
