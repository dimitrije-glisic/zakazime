package com.dglisic.zakazime.business.repository.impl;


import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Business.BUSINESS;
import static jooq.tables.BusinessAccountMap.BUSINESS_ACCOUNT_MAP;
import static jooq.tables.BusinessImage.BUSINESS_IMAGE;
import static jooq.tables.BusinessPredefinedCategoryMap.BUSINESS_PREDEFINED_CATEGORY_MAP;
import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static jooq.tables.PredefinedCategory.PREDEFINED_CATEGORY;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.UserDefinedCategory.USER_DEFINED_CATEGORY;
import static org.jooq.impl.DSL.upper;

import com.dglisic.zakazime.business.repository.BusinessRepository;
import com.dglisic.zakazime.business.service.impl.BusinessStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import jooq.tables.records.BusinessRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
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
        .where(ACCOUNT.ID.eq(userId))
        .fetchOneInto(Business.class);

    return Optional.ofNullable(record);
  }

  @Override
  public Business storeBusinessProfile(final Business business) {
    final BusinessRecord businessRecord = dsl.newRecord(BUSINESS, business);
    businessRecord.store();
    return businessRecord.into(Business.class);
  }


  @Override
  public List<Business> getAll() {
    return dsl.selectFrom(BUSINESS).fetchInto(Business.class);
  }

  @Override
  public Optional<Business> findById(final Integer businessId) {
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
  public Optional<Business> findBusinessByCityAndName(String city, String name) {
    Business business = dsl.selectFrom(BUSINESS)
        .where(upper(BUSINESS.NAME).eq(upper(name)))
        .and(upper(BUSINESS.CITY).eq(upper(city)))
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
  public List<Service> getServicesOfBusiness(Integer businessId) {
    return dsl.select(SERVICE.ID, SERVICE.TITLE, SERVICE.CATEGORY_ID, SERVICE.PRICE, SERVICE.DESCRIPTION, SERVICE.AVG_DURATION)
        .from(SERVICE)
        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
        .where(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchInto(Service.class);
  }

  @Override
  public Optional<Service> findServiceOfBusiness(Integer serviceId, Integer businessId) {
    final Service service = dsl.select(SERVICE)
        .from(SERVICE)
        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
        .where(SERVICE.ID.eq(serviceId))
        .and(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchOneInto(Service.class);
    return Optional.ofNullable(service);
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

  @Override
  public List<Business> searchBusinesses(String city, String businessType, String category) {
    // city is sent from the frontend as a lowercase string separated by a hyphen (e.g. new-york)
    final var denormalizedCity = city.replace("-", " ");
    final var cityCondition = StringUtils.isBlank(city) ? DSL.trueCondition() : upper(BUSINESS.CITY).eq(upper(denormalizedCity));
    final var businessTypeCondition =
        StringUtils.isBlank(businessType) ? DSL.trueCondition() : BUSINESS_TYPE.SLUG.eq(businessType);
    final var categoryCondition =
        StringUtils.isBlank(category) ? DSL.trueCondition() : PREDEFINED_CATEGORY.SLUG.eq(category);

    return dsl.selectDistinct(BUSINESS)
        .from(BUSINESS)
        .join(BUSINESS_PREDEFINED_CATEGORY_MAP).on(BUSINESS.ID.eq(BUSINESS_PREDEFINED_CATEGORY_MAP.BUSINESS_ID))
        .join(PREDEFINED_CATEGORY).on(BUSINESS_PREDEFINED_CATEGORY_MAP.CATEGORY_ID.eq(PREDEFINED_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(BUSINESS_TYPE.ID.eq(PREDEFINED_CATEGORY.BUSINESS_TYPE_ID))
        .where(cityCondition)
        .and(businessTypeCondition)
        .and(categoryCondition)
        .fetchInto(Business.class);
  }

  @Override
  public List<Business> getAllBusinessesInCity(String city) {
    final var denormalizedCity = city.replace("-", " ");
    return dsl.selectFrom(BUSINESS)
        .where(upper(BUSINESS.CITY).eq(upper(denormalizedCity)))
        .fetchInto(Business.class);
  }

  @Override
  public void updateProfileImageUrl(Integer businessId, String imageUrl) {
    dsl.update(BUSINESS)
        .set(BUSINESS.PROFILE_IMAGE_URL, imageUrl)
        .where(BUSINESS.ID.eq(businessId))
        .execute();
  }

  @Override
  public Optional<BusinessImage> getProfileImage(Integer businessId) {
    BusinessImage businessImage = dsl.select(BUSINESS_IMAGE)
        .from(BUSINESS_IMAGE)
        .join(BUSINESS).on(BUSINESS_IMAGE.BUSINESS_ID.eq(BUSINESS.ID))
        .where(BUSINESS.ID.eq(businessId))
        .and(BUSINESS_IMAGE.IMAGE_URL.eq(BUSINESS.PROFILE_IMAGE_URL))
        .fetchOneInto(BusinessImage.class);
    return Optional.ofNullable(businessImage);
  }

  @Override
  public List<Business> getAllWithStatus(BusinessStatus businessStatus) {
    return dsl.selectFrom(BUSINESS)
        .where(BUSINESS.STATUS.eq(businessStatus.name()))
        .fetchInto(Business.class);
  }

  @Override
  public void updateStatus(Integer businessId, BusinessStatus businessStatus) {
    dsl.update(BUSINESS)
        .set(BUSINESS.STATUS, businessStatus.name())
        .where(BUSINESS.ID.eq(businessId))
        .execute();
  }

  @Override
  public void changeStatus(Integer businessId, String string) {
    dsl.update(BUSINESS)
        .set(BUSINESS.STATUS, string)
        .where(BUSINESS.ID.eq(businessId))
        .execute();
  }

  @Override
  public List<Employee> getEmployees(Integer businessId) {
    return dsl.selectFrom(jooq.tables.Employee.EMPLOYEE)
        .where(jooq.tables.Employee.EMPLOYEE.BUSINESS_ID.eq(businessId))
        .fetchInto(Employee.class);
  }

}
