package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.ServiceCategory.SERVICE_CATEGORY;
import static jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY;
import static org.jooq.impl.DSL.upper;

import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.ServiceDao;
import jooq.tables.pojos.Service;
import jooq.tables.records.ServiceRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;
  private final ServiceDao serviceDao;

  @Override
  public List<Service> searchServiceTemplates(final @Nullable String businessType, final @Nullable String category,
                                              final @Nullable String subcategory) {

    final Condition businessTypeCondition =
        StringUtils.isBlank(businessType) ? DSL.trueCondition() : upper(BUSINESS_TYPE.TITLE).eq(upper(businessType));
    final Condition categoryCondition =
        StringUtils.isBlank(category) ? DSL.trueCondition() : upper(SERVICE_CATEGORY.TITLE).eq(upper(category));
    final Condition subcategoryCondition =
        StringUtils.isBlank(subcategory) ? DSL.trueCondition() : upper(SERVICE_SUBCATEGORY.TITLE).eq(upper(subcategory));

    return dsl.select(SERVICE)
        .from(SERVICE)
        .join(SERVICE_SUBCATEGORY).on(SERVICE.SUBCATEGORY_ID.eq(SERVICE_SUBCATEGORY.ID))
        .join(SERVICE_CATEGORY).on(SERVICE_SUBCATEGORY.SERVICE_CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(SERVICE.TEMPLATE.eq(true))
        .and(businessTypeCondition)
        .and(categoryCondition)
        .and(subcategoryCondition)
        .fetchInto(Service.class);
  }

  @Override
  public List<Service> getServicesOfBusiness(final Integer businessId) {
    return dsl.select(SERVICE)
        .from(SERVICE)
        .where(SERVICE.BUSINESS_ID.eq(businessId))
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
              .set(SERVICE.BUSINESS_ID, service.getBusinessId())
              .set(SERVICE.SUBCATEGORY_ID, service.getSubcategoryId())
              .set(SERVICE.TITLE, service.getTitle())
              .set(SERVICE.NOTE, service.getNote())
              .set(SERVICE.PRICE, service.getPrice())
              .set(SERVICE.AVG_DURATION, service.getAvgDuration())
              .set(SERVICE.DESCRIPTION, service.getDescription())
              .set(SERVICE.TEMPLATE, service.getTemplate())
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
        .set(SERVICE.NOTE, service.getNote())
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
        .set(SERVICE.SUBCATEGORY_ID, request.getSubcategoryId())
        .set(SERVICE.NOTE, request.getNote())
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
