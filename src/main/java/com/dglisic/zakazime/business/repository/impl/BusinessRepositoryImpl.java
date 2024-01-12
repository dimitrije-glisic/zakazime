package com.dglisic.zakazime.business.repository.impl;


import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Business.BUSINESS;
import static jooq.tables.BusinessAccountMap.BUSINESS_ACCOUNT_MAP;
import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static org.jooq.impl.DSL.upper;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.user.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.records.BusinessRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;
  private final RoleRepository roleRepository;

  @Override
  public Optional<Business> getBusinessProfile(final Integer userId) {
    // this implicates that there is only one business profile per user - is this ok?
    Business record = dsl.select(BUSINESS)
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(BUSINESS).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(BUSINESS.ID))
        .join(BUSINESS_TYPE).on(BUSINESS.TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(record);
  }

  @Override
  public Business storeBusinessProfile(final Business business, final Account owner) {
    final BusinessRecord businessRecord = dsl.newRecord(BUSINESS, business);
    businessRecord.store();
    return businessRecord.into(Business.class);
  }

  @Override
  public void linkBusinessToOwner(final Integer businessId, final Integer ownerId) {
    dsl.insertInto(BUSINESS_ACCOUNT_MAP)
        .set(BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessId)
        .set(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, ownerId)
        .execute();
  }

  @Override
  public List<Business> getAll() {
    return dsl.selectFrom(BUSINESS).fetchInto(Business.class);
  }

  @Override
  public List<BusinessType> getBusinessTypes() {
    return dsl.selectDistinct(BUSINESS_TYPE)
        .from(BUSINESS_TYPE)
        .fetchInto(BusinessType.class);
  }

  @Override
  public Optional<Business> findBusinessById(final Integer businessId) {
    Business businessProfileRecord = dsl.selectFrom(BUSINESS)
        .where(BUSINESS.ID.eq(businessId))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(businessProfileRecord);

  }

  @Override
  public Optional<Business> findBusinessByName(String name) {
    Business business = dsl.selectFrom(BUSINESS)
        .where(upper(BUSINESS.NAME).eq(upper(name)))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(business);
  }

  @Override
  public boolean isUserRelatedToBusiness(final Integer id, final Integer businessId) {
    Integer count = dsl.selectCount()
        .from(BUSINESS_ACCOUNT_MAP)
        .where(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(businessId))
        .and(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID.eq(id))
        .fetchOneInto(Integer.class);

    return count != null && count > 0;
  }

}
