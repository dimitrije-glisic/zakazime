package com.dglisic.zakazime.business.repository.impl;

import com.dglisic.zakazime.business.repository.ServiceCategoryRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.ServiceCategoryDao;
import jooq.tables.pojos.ServiceCategory;
import jooq.tables.records.ServiceCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceCategoryRepositoryImpl implements ServiceCategoryRepository {

  private final ServiceCategoryDao serviceCategoryDao;

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
    final ServiceCategoryRecord serviceCategoryRecord = new ServiceCategoryRecord(serviceCategory);
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
}
