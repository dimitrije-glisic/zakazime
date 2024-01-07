package com.dglisic.zakazime.business.repository.impl;

import com.dglisic.zakazime.business.repository.ServiceSubcategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jooq.tables.daos.ServiceSubcategoryDao;
import jooq.tables.pojos.ServiceSubcategory;
import jooq.tables.records.ServiceSubcategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceSubcategoryRepositoryImpl implements ServiceSubcategoryRepository {

  private final DSLContext dsl;
  private final ServiceSubcategoryDao serviceSubcategoryDao;

  public boolean allExist(final Set<Integer> subCategoryIds) {
    List<Integer> fetch = dsl.select(jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY.ID)
        .from(jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY)
        .where(jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY.ID.in(subCategoryIds))
        .fetchInto(Integer.class);
    return fetch.size() != subCategoryIds.size();
  }

  public boolean exists(final Integer integer) {
    return serviceSubcategoryDao.findById(integer) != null;
  }

  @Override
  public void delete(Integer id) {
    serviceSubcategoryDao.deleteById(id);
  }

  @Override
  public ServiceSubcategory store(final ServiceSubcategory serviceCategory) {
    ServiceSubcategoryRecord serviceSubcategoryRecord =
        dsl.newRecord(jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY, serviceCategory);
    serviceSubcategoryRecord.store();
    return serviceSubcategoryRecord.into(ServiceSubcategory.class);
  }

  @Override
  public List<ServiceSubcategory> getAll() {
    return serviceSubcategoryDao.findAll();
  }

  @Override
  public Optional<ServiceSubcategory> findById(final Integer id) {
    return Optional.ofNullable(serviceSubcategoryDao.findById(id));
  }

  @Override
  public Optional<ServiceSubcategory> findByTitle(final String title) {
    return Optional.ofNullable(serviceSubcategoryDao.fetchOneByTitle(title));
  }
}
