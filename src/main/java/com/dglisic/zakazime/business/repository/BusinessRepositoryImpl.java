package com.dglisic.zakazime.business.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.BUSINESS;
import static model.Tables.BUSINESS_ACCOUNT_MAP;
import static model.Tables.BUSINESS_TYPE;

import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import model.tables.records.BusinessRecord;
import model.tables.records.BusinessTypeRecord;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;

  public Optional<Business> getBusinessProfile(int userId) {
    //this implicates that there is only one business profile per user
    Record2<BusinessRecord, BusinessTypeRecord>
        record = dsl.select(BUSINESS, BUSINESS_TYPE)
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(BUSINESS).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(BUSINESS.ID))
        .join(BUSINESS_TYPE).on(BUSINESS.TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOne();

    if (record == null) {
      return Optional.empty();
    }

    return Optional.of(new Business(record.value1(), record.value2()));
  }

  @Override
  @Transactional
  public Business createBusinessProfile(
      final Business business) {

    BusinessRecord businessProfileRecord =
        dsl.insertInto(BUSINESS)
            .set(BUSINESS.STATUS, business.getStatus())
            .set(BUSINESS.NAME, business.getName())
            .set(BUSINESS.TYPE_ID, business.getType().getId())
            .set(BUSINESS.PHONE_NUMBER, business.getPhoneNumber())
            .set(BUSINESS.CITY, business.getCity())
            .set(BUSINESS.POSTAL_CODE, business.getPostalCode())
            .set(BUSINESS.ADDRESS, business.getAddress())
            .set(BUSINESS.CREATED_ON, LocalDateTime.now())
            .returning(BUSINESS.ID)
            .fetchOne();

    if (businessProfileRecord == null) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    int ownerId = business.getOwner().getId();
    int businessId = businessProfileRecord.getId();

    int rowsAffected = dsl.insertInto(BUSINESS_ACCOUNT_MAP)
        .set(BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessId)
        .set(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, ownerId)
        .execute();

    if (rowsAffected == 0) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return business.toBuilder()
        .id(businessId)
        .build();
  }

  @Override
  public List<Business> getAll() {
    Result<BusinessRecord> fetch = dsl.selectFrom(BUSINESS).fetch();
    return fetch.map(Business::new);
  }

  @Override
  public List<BusinessType> getBusinessTypes() {
    return dsl.selectDistinct(BUSINESS_TYPE)
        .from(BUSINESS_TYPE)
        .fetchInto(BusinessType.class);
  }

  @Override
  public Optional<Business> findBusinessByName(String businessName) {
    BusinessRecord businessProfileRecord = dsl.selectFrom(BUSINESS)
        .where(BUSINESS.NAME.eq(businessName))
        .fetchOne();

    if (businessProfileRecord == null) {
      return Optional.empty();
    }

    return Optional.of(new Business(businessProfileRecord));
  }

}
