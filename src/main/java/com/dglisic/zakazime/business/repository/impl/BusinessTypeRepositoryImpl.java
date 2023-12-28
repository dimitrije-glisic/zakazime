package com.dglisic.zakazime.business.repository.impl;

import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.BusinessTypeDao;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BusinessTypeRepositoryImpl implements BusinessTypeRepository {

  private final BusinessTypeDao businessTypeDao;

  @Override
  public List<BusinessType> getAll() {
    return businessTypeDao.findAll();
  }

  @Override
  public Optional<Object> findById(final Integer businessTypeId) {
    final BusinessType byId = businessTypeDao.findById(businessTypeId);
    return Optional.ofNullable(byId);
  }

}
