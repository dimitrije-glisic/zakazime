package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.BusinessType.BUSINESS_TYPE;

import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.BusinessTypeDao;
import jooq.tables.pojos.BusinessType;
import jooq.tables.records.BusinessTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BusinessTypeRepositoryImpl implements BusinessTypeRepository {

  private final BusinessTypeDao businessTypeDao;
  private final DSLContext dsl;

  @Override
  public List<BusinessType> getAll() {
    return businessTypeDao.findAll();
  }

  @Override
  public Optional<BusinessType> findById(final Integer businessTypeId) {
    final BusinessType byId = businessTypeDao.findById(businessTypeId);
    return Optional.ofNullable(byId);
  }

  @Override
  public boolean existsByTitle(final String title) {
    return !businessTypeDao.fetchByTitle(title).isEmpty();
  }

  @Override
  public BusinessType create(final BusinessType businessType) {
    final BusinessTypeRecord businessTypeRecord = dsl.newRecord(BUSINESS_TYPE, businessType);
    businessTypeRecord.store();
    return businessTypeRecord.into(BusinessType.class);
  }

  @Override
  public void update(final BusinessType inUpdate) {
    dsl.update(BUSINESS_TYPE)
        .set(BUSINESS_TYPE.TITLE, inUpdate.getTitle())
        .where(BUSINESS_TYPE.ID.eq(inUpdate.getId()))
        .execute();
  }

  @Override
  public void deleteById(Integer id) {
    dsl.deleteFrom(BUSINESS_TYPE)
        .where(BUSINESS_TYPE.ID.eq(id))
        .execute();
  }

}
