package com.dglisic.zakazime.business.repository;

import static model.tables.ServiceCategory.SERVICE_CATEGORY;

import com.dglisic.zakazime.business.domain.Category;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import model.tables.records.ServiceCategoryRecord;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final DSLContext dsl;

  @Override
  @Cacheable("categories")
  public Optional<Category> findCategory(String categoryName) {
    ServiceCategoryRecord categoryRecord = dsl.selectFrom(SERVICE_CATEGORY)
        .where(SERVICE_CATEGORY.NAME.eq(categoryName))
        .fetchOne();
    return Optional.ofNullable(categoryRecord).map(Category::new);
  }

}
