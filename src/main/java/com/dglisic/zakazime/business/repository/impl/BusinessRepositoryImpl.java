package com.dglisic.zakazime.business.repository.impl;


import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Business.BUSINESS;
import static jooq.tables.BusinessAccountMap.BUSINESS_ACCOUNT_MAP;
import static jooq.tables.BusinessPredefinedCategoryMap.BUSINESS_PREDEFINED_CATEGORY_MAP;
import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static jooq.tables.PredefinedCategory.PREDEFINED_CATEGORY;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.UserDefinedCategory.USER_DEFINED_CATEGORY;
import static org.jooq.impl.DSL.upper;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import jooq.tables.records.BusinessRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

  private final DSLContext dsl;

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

  @Override
  public void linkPredefined(List<Integer> categoryIds, Integer businessId) {
    List<Query> queries = new ArrayList<>();

    for (Integer categoryId : categoryIds) {
      queries.add(
          dsl.insertInto(BUSINESS_PREDEFINED_CATEGORY_MAP)
              .set(BUSINESS_PREDEFINED_CATEGORY_MAP.BUSINESS_ID, businessId)
              .set(BUSINESS_PREDEFINED_CATEGORY_MAP.CATEGORY_ID, categoryId)
      );
    }
    dsl.batch(queries).execute();
  }

  @Override
  public List<PredefinedCategory> getPredefinedCategories(Integer businessId) {
    return dsl.select()
        .from(PREDEFINED_CATEGORY)
        .join(BUSINESS_PREDEFINED_CATEGORY_MAP).on(PREDEFINED_CATEGORY.ID.eq(BUSINESS_PREDEFINED_CATEGORY_MAP.CATEGORY_ID))
        .where(BUSINESS_PREDEFINED_CATEGORY_MAP.BUSINESS_ID.eq(businessId))
        .fetchInto(PredefinedCategory.class);
  }

  @Override
  public List<UserDefinedCategory> getUserDefinedCategories(Integer businessId) {
    return dsl.selectFrom(USER_DEFINED_CATEGORY)
        .where(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchInto(UserDefinedCategory.class);
  }

  @Override
  public void createUserDefinedCategory(UserDefinedCategory category) {
    dsl.insertInto(USER_DEFINED_CATEGORY)
        .set(USER_DEFINED_CATEGORY.TITLE, category.getTitle())
        .set(USER_DEFINED_CATEGORY.BUSINESS_ID, category.getBusinessId())
        .execute();
  }

  @Override
  public List<Service> getServicesOfBusiness(Integer businessId) {
    return dsl.select(SERVICE.ID, SERVICE.TITLE, SERVICE.CATEGORY_ID, SERVICE.PRICE, SERVICE.DESCRIPTION, SERVICE.AVG_DURATION)
        .from(SERVICE)
        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
        .where(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchInto(Service.class);
  }

  @Override
  public boolean serviceBelongsToBusiness(Integer serviceId, Integer businessId) {
    Integer count = dsl.selectCount()
        .from(SERVICE)
        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
        .where(SERVICE.ID.eq(serviceId))
        .and(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchOneInto(Integer.class);

    return count != null && count > 0;
  }

}
