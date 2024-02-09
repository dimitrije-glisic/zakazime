package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Service.SERVICE;
import static jooq.tables.UserDefinedCategory.USER_DEFINED_CATEGORY;

import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.ServiceDao;
import jooq.tables.pojos.Service;
import jooq.tables.records.ServiceRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;
  private final ServiceDao serviceDao;

  // TODO - delete? not needed anymore
  // maybe leave it as an example of how to use JOOQ for more complex queries
//  @Override
//  public List<Service> searchServiceTemplates(final @Nullable String businessType, final @Nullable String category,
//                                              final @Nullable String subcategory) {
//
//    final Condition businessTypeCondition =
//        StringUtils.isBlank(businessType) ? DSL.trueCondition() : upper(BUSINESS_TYPE.TITLE).eq(upper(businessType));
//    final Condition categoryCondition =
//        StringUtils.isBlank(category) ? DSL.trueCondition() : upper(USER_DEFINED_CATEGORY.TITLE).eq(upper(category));
//    final Condition subcategoryCondition =
//        StringUtils.isBlank(subcategory) ? DSL.trueCondition() : upper(USER_DEFINED_CATEGORY.TITLE).eq(upper(subcategory));
//
//    return dsl.select(SERVICE)
//        .from(SERVICE)
//        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
//        .join(SERVICE_CATEGORY).on(SERVICE_SUBCATEGORY.SERVICE_CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
//        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
//        .where(SERVICE.TEMPLATE.eq(true))
//        .and(businessTypeCondition)
//        .and(categoryCondition)
//        .and(subcategoryCondition)
//        .fetchInto(Service.class);
//  }

  @Override
  @Cacheable("services")
  public List<Service> getServicesOfBusiness(final Integer businessId) {
    return dsl.select(SERVICE)
        .from(SERVICE)
        .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
        .where(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
        .fetchInto(Service.class);
  }

  @Override
  public Optional<Service> findServiceById(final Integer serviceId) {
    Service service = serviceDao.findById(serviceId);
    return Optional.ofNullable(service);
  }

  @Override
  @CacheEvict(value = "services", allEntries = true)
  public List<Service> saveServices(List<Service> services) {
    final List<ServiceRecord> serviceRecords = new ArrayList<>();
    for (Service service : services) {
      ServiceRecord serviceRecord = dsl.newRecord(SERVICE, service);
      serviceRecords.add(serviceRecord);
    }

    var insertStepN = dsl.insertInto(SERVICE).set(dsl.newRecord(SERVICE, serviceRecords.get(0)));
    for (var record : serviceRecords.subList(1, serviceRecords.size())) {
      insertStepN = insertStepN.newRecord().set(dsl.newRecord(SERVICE, record));
    }
    return insertStepN.returning().fetch().into(Service.class);
  }

  @Override
  @CacheEvict(value = "services", allEntries = true)
  public Service create(final Service service) {
    final ServiceRecord serviceRecord = dsl.newRecord(SERVICE, service);
    serviceRecord.store();
    return serviceRecord.into(Service.class);
  }

  @Override
  @CacheEvict(value = "services", allEntries = true)
  public void update(final Service service) {
    dsl.update(SERVICE)
        .set(SERVICE.TITLE, service.getTitle())
        .set(SERVICE.PRICE, service.getPrice())
        .set(SERVICE.AVG_DURATION, service.getAvgDuration())
        .set(SERVICE.DESCRIPTION, service.getDescription())
        .where(SERVICE.ID.eq(service.getId()))
        .execute();
  }

  @Override
  public Optional<Service> findByTitle(String title) {
    List<Service> services = serviceDao.fetchByTitle(title);
    if (services.size() > 1) {
      // this should never happen
      throw new ApplicationException("Multiple services with title " + title + " found", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return Optional.ofNullable(services.isEmpty() ? null : services.get(0));
  }

  @Override
  public boolean existsByTitleAndBusinessId(String title, Integer businessId) {
    return dsl.fetchExists(
        dsl.selectOne()
            .from(SERVICE)
            .join(USER_DEFINED_CATEGORY).on(SERVICE.CATEGORY_ID.eq(USER_DEFINED_CATEGORY.ID))
            .where(SERVICE.TITLE.equalIgnoreCase(title).and(USER_DEFINED_CATEGORY.BUSINESS_ID.eq(businessId))
            )
    );
  }

  @Override
  public void delete(Integer serviceId) {
    serviceDao.deleteById(serviceId);
  }

}
