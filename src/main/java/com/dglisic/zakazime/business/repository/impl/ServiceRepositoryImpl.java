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
import org.jooq.Query;
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
  public void saveServices(List<Service> services) {
    List<Query> queries = new ArrayList<>();

    for (Service service : services) {
      queries.add(
          dsl.insertInto(SERVICE)
              .set(SERVICE.CATEGORY_ID, service.getCategoryId())
              .set(SERVICE.TITLE, service.getTitle())
              .set(SERVICE.PRICE, service.getPrice())
              .set(SERVICE.AVG_DURATION, service.getAvgDuration())
              .set(SERVICE.DESCRIPTION, service.getDescription())
      );
    }
    dsl.batch(queries).execute();
  }

  @Override
  public Service create(final Service service) {
    final ServiceRecord serviceRecord = dsl.newRecord(SERVICE, service);
    serviceRecord.store();
    return serviceRecord.into(Service.class);
  }

  @Override
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
  public void updateServiceTemplate(final Service request) {
    dsl.update(SERVICE)
        .set(SERVICE.TITLE, request.getTitle())
        .set(SERVICE.CATEGORY_ID, request.getCategoryId())
        .set(SERVICE.PRICE, request.getPrice())
        .set(SERVICE.AVG_DURATION, request.getAvgDuration())
        .set(SERVICE.DESCRIPTION, request.getDescription())
        .where(SERVICE.ID.eq(request.getId()))
        .execute();
  }

  @Override
  public void deleteServiceTemplate(final Integer id) {
    dsl.deleteFrom(SERVICE)
        .where(SERVICE.ID.eq(id))
        .execute();
  }

}
