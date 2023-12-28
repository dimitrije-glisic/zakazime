package com.dglisic.zakazime.business.repository.impl;

import com.dglisic.zakazime.business.repository.ServiceSubcategoryRepository;
import java.util.List;
import java.util.Set;
import jooq.tables.ServiceSubcategory;
import jooq.tables.daos.ServiceSubcategoryDao;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceSubcategoryRepositoryImpl extends ServiceSubcategoryDao implements ServiceSubcategoryRepository {

  private final DSLContext dsl;

  public boolean allExist(Set<Integer> subCategoryIds) {
    List<Integer> fetch = dsl.select(ServiceSubcategory.SERVICE_SUBCATEGORY.ID)
        .from(ServiceSubcategory.SERVICE_SUBCATEGORY)
        .where(ServiceSubcategory.SERVICE_SUBCATEGORY.ID.in(subCategoryIds))
        .fetchInto(Integer.class);
    return fetch.size() != subCategoryIds.size();
  }

  public boolean exists(Integer integer) {
    return this.findById(integer) != null;
  }
}
