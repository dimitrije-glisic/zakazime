package com.dglisic.zakazime.business.repository;

import static jooq.tables.BusinessType.BUSINESS_TYPE;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.ServiceCategory.SERVICE_CATEGORY;
import static jooq.tables.ServiceSubcategory.SERVICE_SUBCATEGORY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;

  @Override
  public List<Service> getServiceTemplatesOfBusinessType(String businessType) {
    return dsl.select(SERVICE)
        .from(SERVICE)
        .join(SERVICE_SUBCATEGORY).on(SERVICE.SUBCATEGORY_ID.eq(SERVICE_SUBCATEGORY.ID))
        .join(SERVICE_CATEGORY).on(SERVICE_SUBCATEGORY.SERVICE_CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(BUSINESS_TYPE.TITLE.eq(businessType.toUpperCase()))
        .and(SERVICE.TEMPLATE.eq(true))
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
  public void updateService(int serviceId, Service service) {
    dsl.update(SERVICE)
        .set(SERVICE.TITLE, service.getTitle())
        .set(SERVICE.SUBCATEGORY_ID, service.getSubcategoryId())
        .set(SERVICE.NOTE, service.getNote())
        .set(SERVICE.DESCRIPTION, service.getDescription())
        .set(SERVICE.PRICE, service.getPrice())
        .set(SERVICE.AVG_DURATION, service.getAvgDuration())
        .set(SERVICE.TEMPLATE, service.getTemplate())
        .where(SERVICE.ID.eq(serviceId))
        .execute();
  }

}
