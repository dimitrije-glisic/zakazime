package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.ServiceCategory.SERVICE_CATEGORY;

import com.dglisic.zakazime.business.repository.ServiceCategoryRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.ServiceCategoryDao;
import jooq.tables.pojos.ServiceCategory;
import jooq.tables.records.ServiceCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceCategoryRepositoryImpl implements ServiceCategoryRepository {

  private final ServiceCategoryDao serviceCategoryDao;
  private final DSLContext dsl;

  @Override
  public List<ServiceCategory> getAll() {
    return serviceCategoryDao.findAll();
  }

  @Override
  public Optional<ServiceCategory> findById(Integer id) {
    return Optional.ofNullable(serviceCategoryDao.fetchOneById(id));
  }

  @Override
  public ServiceCategory store(final ServiceCategory serviceCategory) {
    final ServiceCategoryRecord serviceCategoryRecord = dsl.newRecord(SERVICE_CATEGORY, serviceCategory);
    serviceCategoryRecord.store();
    return serviceCategoryRecord.into(ServiceCategory.class);
  }

  @Override
  public void delete(final Integer id) {
    serviceCategoryDao.deleteById(id);
  }

  @Override
  public Optional<Object> findByTitle(final String title) {
    final ServiceCategory serviceCategories = serviceCategoryDao.fetchOneByTitle(title.toUpperCase());
    return Optional.ofNullable(serviceCategories);
  }

  @Override
  public void updateImage(final Integer id, final String url) {
    dsl.update(SERVICE_CATEGORY)
        .set(SERVICE_CATEGORY.IMAGE_URL, url)
        .where(SERVICE_CATEGORY.ID.eq(id))
        .execute();
  }

}
