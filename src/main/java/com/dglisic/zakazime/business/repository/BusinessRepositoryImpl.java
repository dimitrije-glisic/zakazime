package com.dglisic.zakazime.business.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.BUSINESS_ACCOUNT_MAP;
import static model.tables.BusinessProfile.BUSINESS_PROFILE;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.Tables;
import model.tables.records.BusinessProfileRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;

  public BusinessRepositoryImpl(DSLContext create) {
    this.dsl = create;
  }

  public Optional<com.dglisic.zakazime.business.domain.BusinessProfile> getBusinessProfile(int userId) {
    BusinessProfileRecord businessProfileRecord = dsl.select()
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(Tables.BUSINESS_PROFILE).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(Tables.BUSINESS_PROFILE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(BusinessProfileRecord.class);

    if (businessProfileRecord == null) {
      return Optional.empty();
    }

    return Optional.of(new com.dglisic.zakazime.business.domain.BusinessProfile(businessProfileRecord));
  }

  @Override
  @Transactional
  public com.dglisic.zakazime.business.domain.BusinessProfile createBusinessProfile(
      final com.dglisic.zakazime.business.domain.BusinessProfile businessProfile) {

    BusinessProfileRecord businessProfileRecord =
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

  @Override
  public List<BusinessProfile> getAll() {
    Result<BusinessProfileRecord> fetch = dsl.selectFrom(BUSINESS_PROFILE).fetch();
    return fetch.map(BusinessProfile::new);
  }

}
