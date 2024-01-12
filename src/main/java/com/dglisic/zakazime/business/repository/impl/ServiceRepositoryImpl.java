package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.ServiceCategory.SERVICE_CATEGORY;
import static jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY;
import static org.jooq.impl.DSL.upper;

import com.dglisic.zakazime.business.repository.ServiceRepository;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;
import jooq.tables.records.ServiceRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;

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
  public List<Service> getServicesOfBusiness(int businessId) {
    return dsl.select(SERVICE)
        .from(SERVICE)
        .where(SERVICE.BUSINESS_ID.eq(businessId))
        .fetchInto(Service.class);
  }

  @Override
  public Optional<Service> findServiceById(int serviceId) {
    Service service = dsl.select(SERVICE)
        .from(SERVICE)
        .where(SERVICE.ID.eq(serviceId))
        .fetchOneInto(Service.class);

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
  public void create(final Service service) {
    final ServiceRecord serviceRecord = dsl.newRecord(SERVICE, service);
    serviceRecord.store();
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

}
