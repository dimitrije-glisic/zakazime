package com.dglisic.zakazime.business.repository;


import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Business.BUSINESS;
import static jooq.tables.BusinessAccountMap.BUSINESS_ACCOUNT_MAP;
import static jooq.tables.BusinessType.BUSINESS_TYPE;

import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;

  public Optional<Business> getBusinessProfile(int userId) {
    // this implicates that there is only one business profile per user - is this ok?
    Business record = dsl.select(BUSINESS, BUSINESS_TYPE)
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(BUSINESS).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(BUSINESS.ID))
        .join(BUSINESS_TYPE).on(BUSINESS.TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(record);
  }

  @Override
  @Transactional
  public Business createBusinessProfile(
      final Business business, final int ownerId) {

    Business created =
        dsl.insertInto(BUSINESS)
            .set(BUSINESS.STATUS, business.getStatus())
            .set(BUSINESS.NAME, business.getName())
            .set(BUSINESS.TYPE_ID, business.getTypeId())
            .set(BUSINESS.PHONE_NUMBER, business.getPhoneNumber())
            .set(BUSINESS.CITY, business.getCity())
            .set(BUSINESS.POSTAL_CODE, business.getPostalCode())
            .set(BUSINESS.ADDRESS, business.getAddress())
            .set(BUSINESS.CREATED_ON, LocalDateTime.now())
            .returning(BUSINESS.ID)
            .fetchOneInto(Business.class);

    if (created == null) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    int businessId = created.getId();

    int rowsAffected = dsl.insertInto(BUSINESS_ACCOUNT_MAP)
        .set(BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessId)
        .set(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, ownerId)
        .execute();

    if (rowsAffected == 0) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return created;
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
  public Optional<Business> findBusinessById(int businessId) {
    Business businessProfileRecord = dsl.selectFrom(BUSINESS)
        .where(BUSINESS.ID.eq(businessId))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(businessProfileRecord);

  }

}
