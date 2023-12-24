package com.dglisic.zakazime.admin.repository.impl;

import static model.tables.BusinessType.BUSINESS_TYPE;

import com.dglisic.zakazime.admin.repository.BusinessTypeRepository;
import com.dglisic.zakazime.admin.repository2service_mapper.BusinessTypeMapper;
import com.dglisic.zakazime.business.domain.BusinessType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import model.tables.records.BusinessTypeRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BusinessTypeRepositoryImpl implements BusinessTypeRepository {

  private final DSLContext dsl;
  private final BusinessTypeMapper businessTypeMapper;

  @Override
  public List<BusinessType> getAllBusinessTypes() {
    return dsl.selectFrom(BUSINESS_TYPE)
        .fetch()
        .map(businessTypeMapper::toDomain);
  }

  @Override
  public Optional<BusinessType> findBusinessTypeById(int id) {
    return dsl.selectFrom(BUSINESS_TYPE)
        .where(BUSINESS_TYPE.ID.eq(id))
        .fetchOptional()
        .map(businessTypeMapper::toDomain);
  }

  @Override
  public BusinessType save(BusinessType businessType) {
    BusinessTypeRecord record = dsl.insertInto(BUSINESS_TYPE)
        .set(BUSINESS_TYPE.NAME, businessType.getTitle())
        .returning(BUSINESS_TYPE.ID)
        .fetchOne();
    return businessTypeMapper.toDomain(record);
  }

  @Override
  public void update(BusinessType request) {
    dsl.update(BUSINESS_TYPE)
        .set(BUSINESS_TYPE.NAME, request.getTitle())
        .where(BUSINESS_TYPE.ID.eq(request.getId()))
        .execute();
  }

  @Override
  public void delete(int businessTypeId) {
    dsl.deleteFrom(BUSINESS_TYPE)
        .where(BUSINESS_TYPE.ID.eq(businessTypeId))
        .execute();
  }

}
