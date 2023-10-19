package com.dglisic.zakazime.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.BUSINESS_ACCOUNT_MAP;
import static model.Tables.BUSINESS_PROFILE;

import java.time.LocalDateTime;
import java.util.Optional;
import model.tables.BusinessProfile;
import model.tables.records.BusinessProfileRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext create;

  public BusinessRepositoryImpl(DSLContext create) {
    this.create = create;
  }

  @Override
  public int saveBusinessProfile(BusinessProfileRecord businessProfile) {
    BusinessProfileRecord businessProfileRecord = create.insertInto(BusinessProfile.BUSINESS_PROFILE)
        .set(BusinessProfile.BUSINESS_PROFILE.STATUS, businessProfile.getStatus())
        .set(BusinessProfile.BUSINESS_PROFILE.NAME, businessProfile.getName())
        .set(BusinessProfile.BUSINESS_PROFILE.EMAIL, businessProfile.getEmail())
        .set(BusinessProfile.BUSINESS_PROFILE.PHONE_NUMBER, businessProfile.getPhoneNumber())
        .set(BusinessProfile.BUSINESS_PROFILE.CITY, businessProfile.getCity())
        .set(BusinessProfile.BUSINESS_PROFILE.POSTAL_CODE, businessProfile.getPostalCode())
        .set(BusinessProfile.BUSINESS_PROFILE.ADDRESS, businessProfile.getAddress())
        .set(BusinessProfile.BUSINESS_PROFILE.CREATED_ON, LocalDateTime.now())
        .returning(BusinessProfile.BUSINESS_PROFILE.ID)
        .fetchOne();
    if (businessProfileRecord != null) {
      return businessProfileRecord.getId();
    } else {
      throw new RuntimeException("Business profile not saved");
    }
  }

  public Optional<BusinessProfileRecord> getBusinessProfile(int userId) {
    BusinessProfileRecord businessProfileRecord = create.select()
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(BUSINESS_PROFILE).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(BUSINESS_PROFILE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(BusinessProfileRecord.class);

    return Optional.ofNullable(businessProfileRecord);

  }

}
