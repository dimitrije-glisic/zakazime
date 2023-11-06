package com.dglisic.zakazime.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.BUSINESS_ACCOUNT_MAP;
import static model.tables.BusinessProfile.BUSINESS_PROFILE;

import com.dglisic.zakazime.service.ApplicationException;
import java.time.LocalDateTime;
import java.util.Optional;
import model.Tables;
import model.tables.records.BusinessProfile;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;

  public BusinessRepositoryImpl(DSLContext create) {
    this.dsl = create;
  }

  public Optional<com.dglisic.zakazime.domain.BusinessProfile> getBusinessProfile(int userId) {
    BusinessProfile businessProfileRecord = dsl.select()
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(Tables.BUSINESS_PROFILE).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(Tables.BUSINESS_PROFILE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(BusinessProfile.class);

    if (businessProfileRecord == null) {
      return Optional.empty();
    }

    return Optional.of(new com.dglisic.zakazime.domain.BusinessProfile(businessProfileRecord));
  }

  @Override
  @Transactional
  public com.dglisic.zakazime.domain.BusinessProfile createBusinessProfile(
      final com.dglisic.zakazime.domain.BusinessProfile businessProfile) {

    model.tables.records.BusinessProfile businessProfileRecord =
        dsl.insertInto(BUSINESS_PROFILE)
            .set(BUSINESS_PROFILE.STATUS, businessProfile.getStatus())
            .set(BUSINESS_PROFILE.NAME, businessProfile.getName())
            .set(BUSINESS_PROFILE.PHONE_NUMBER, businessProfile.getPhoneNumber())
            .set(BUSINESS_PROFILE.CITY, businessProfile.getCity())
            .set(BUSINESS_PROFILE.POSTAL_CODE, businessProfile.getPostalCode())
            .set(BUSINESS_PROFILE.ADDRESS, businessProfile.getAddress())
            .set(BUSINESS_PROFILE.CREATED_ON, LocalDateTime.now())
            .returning(BUSINESS_PROFILE.ID)
            .fetchOne();

    if (businessProfileRecord == null) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    int ownerId = businessProfile.getOwner().getId();
    int businessId = businessProfileRecord.getId();

    int rowsAffected = dsl.insertInto(BUSINESS_ACCOUNT_MAP)
        .set(BUSINESS_ACCOUNT_MAP.BUSINESS_ID, businessId)
        .set(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID, ownerId)
        .execute();

    if (rowsAffected == 0) {
      throw new ApplicationException("Business profile not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return businessProfile.toBuilder()
        .id(businessId)
        .build();
  }

}
