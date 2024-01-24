package com.dglisic.zakazime.business.repository.impl;


import static jooq.tables.PredefinedCategory.PREDEFINED_CATEGORY;

import com.dglisic.zakazime.business.repository.PredefinedCategoryRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.PredefinedCategoryDao;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.records.PredefinedCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PredefinedCategoryRepositoryImpl implements PredefinedCategoryRepository {

  private final PredefinedCategoryDao categoryDao;
  private final DSLContext dsl;

  @Override
  public List<PredefinedCategory> getAll() {
    return categoryDao.findAll();
  }

  @Override
  public Optional<PredefinedCategory> findById(Integer id) {
    return Optional.ofNullable(categoryDao.fetchOneById(id));
  }

  @Override
  public PredefinedCategory store(final PredefinedCategory category) {
    final PredefinedCategoryRecord serviceCategoryRecord = dsl.newRecord(PREDEFINED_CATEGORY, category);
    serviceCategoryRecord.store();
    return serviceCategoryRecord.into(PredefinedCategory.class);
  }

  @Override
  public void delete(final Integer id) {
    categoryDao.deleteById(id);
  }

  @Override
  public Optional<Object> findByTitle(final String title) {
    final PredefinedCategory serviceCategories = categoryDao.fetchOneByTitle(title.toUpperCase());
    return Optional.ofNullable(serviceCategories);
  }

  @Override
  public void updateImage(final Integer id, final String url) {
    dsl.update(PREDEFINED_CATEGORY)
        .set(PREDEFINED_CATEGORY.IMAGE_URL, url)
        .where(PREDEFINED_CATEGORY.ID.eq(id))
        .execute();
  }

  @Override
  public void update(PredefinedCategory category) {
    categoryDao.update(category);
  }

}
