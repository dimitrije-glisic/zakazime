package com.dglisic.zakazime.business.repository;

import static model.Tables.ACCOUNT;
import static model.Tables.BUSINESS_ACCOUNT_MAP;
import static model.Tables.BUSINESS_TYPE;
import static model.Tables.SERVICE_CATEGORY;
import static model.tables.BusinessProfile.BUSINESS_PROFILE;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.common.ApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.Tables;
import model.tables.records.BusinessProfileRecord;
import model.tables.records.BusinessTypeRecord;
import model.tables.records.ServiceRecord;
import org.jooq.DSLContext;
import org.jooq.Record2;
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

  public Optional<BusinessProfile> getBusinessProfile(int userId) {
    //this implicates that there is only one business profile per user
    Record2<BusinessProfileRecord, BusinessTypeRecord>
        record = dsl.select(BUSINESS_PROFILE, BUSINESS_TYPE)
        .from(ACCOUNT)
        .join(BUSINESS_ACCOUNT_MAP).on(ACCOUNT.ID.eq(BUSINESS_ACCOUNT_MAP.ACCOUNT_ID))
        .join(Tables.BUSINESS_PROFILE).on(BUSINESS_ACCOUNT_MAP.BUSINESS_ID.eq(Tables.BUSINESS_PROFILE.ID))
        .join(BUSINESS_TYPE).on(Tables.BUSINESS_PROFILE.TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(ACCOUNT.ID.eq(userId))
        .fetchOne();

    if (record == null) {
      return Optional.empty();
    }

    return Optional.of(new BusinessProfile(record.value1(), record.value2()));
  }

  @Override
  @Transactional
  public BusinessProfile createBusinessProfile(
      final BusinessProfile businessProfile) {

    BusinessProfileRecord businessProfileRecord =
        dsl.insertInto(BUSINESS_PROFILE)
            .set(BUSINESS_PROFILE.STATUS, businessProfile.getStatus())
            .set(BUSINESS_PROFILE.NAME, businessProfile.getName())
            .set(BUSINESS_PROFILE.TYPE_ID, businessProfile.getType().getId())
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

  @Override
  public List<BusinessType> getBusinessTypes() {
    return dsl.selectDistinct(BUSINESS_TYPE)
        .from(BUSINESS_TYPE)
        .fetchInto(BusinessType.class);
  }

  @Override
  public List<Service> getServicesForType(String type) {
    Result<Record2<ServiceRecord, String>> serviceRecords = dsl.select(Tables.SERVICE, SERVICE_CATEGORY.NAME)
        .from(Tables.SERVICE)
        .join(SERVICE_CATEGORY).on(Tables.SERVICE.CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(BUSINESS_TYPE.NAME.eq(type))
        .fetch();

    if (serviceRecords.isEmpty()) {
      throw new ApplicationException("No services found for type " + type, HttpStatus.NOT_FOUND);
    }

    return serviceRecords.map(
        record -> new Service(record.value1()).toBuilder()
            .categoryName(record.value2())
            .build()
    );
  }

}
