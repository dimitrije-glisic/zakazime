package com.dglisic.zakazime.business.repository;

import static model.Tables.BUSINESS;
import static model.Tables.BUSINESS_TYPE;
import static model.tables.Service.SERVICE;
import static model.tables.ServiceCategory.SERVICE_CATEGORY;

import com.dglisic.zakazime.business.domain.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import model.tables.records.BusinessRecord;
import model.tables.records.ServiceCategoryRecord;
import model.tables.records.ServiceRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;

  @Override
  public List<Service> getServiceTemplatesOfType(String type) {
    Result<Record2<ServiceRecord, ServiceCategoryRecord>>
        serviceRecords = dsl.select(SERVICE, SERVICE_CATEGORY)
        .from(SERVICE)
        .join(SERVICE_CATEGORY).on(SERVICE.CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
        .where(BUSINESS_TYPE.NAME.eq(type.toUpperCase()))
        .and(SERVICE.TEMPLATE.eq(true))
        .fetch();

    if (serviceRecords.isEmpty()) {
      return new ArrayList<>();
    }

    return serviceRecords.map(
        record -> new Service(record.value1(), record.value2())
    );
  }

  @Override
  public List<Service> getServicesOfBusiness(int businessId) {
    Condition condition = SERVICE.BUSINESS_ID.eq(businessId);
    return findServices(condition);
  }

  @Override
  public Optional<Service> findService(String serviceId) {
    Condition condition = SERVICE.ID.eq(Integer.parseInt(serviceId));
    List<Service> services = findServices(condition);
    return services.isEmpty() ? Optional.empty() : Optional.of(services.get(0));
  }

  private List<Service> findServices(Condition condition) {
    Result<Record3<ServiceRecord, ServiceCategoryRecord, BusinessRecord>>
        serviceRecords = dsl.select(SERVICE, SERVICE_CATEGORY, BUSINESS)
        .from(SERVICE)
        .join(SERVICE_CATEGORY).on(SERVICE.CATEGORY_ID.eq(SERVICE_CATEGORY.ID))
        .join(BUSINESS_TYPE).on(SERVICE_CATEGORY.BUSINESS_TYPE_ID.eq(BUSINESS_TYPE.ID))
        .join(BUSINESS).on(SERVICE.BUSINESS_ID.eq(BUSINESS.ID))
        .where(condition)
        .fetch();

    if (serviceRecords.isEmpty()) {
      return new ArrayList<>();
    }

    return serviceRecords.map(
        record -> new Service(record.value1(), record.value2(), record.value3())
    );
  }

  @Override
  public void saveServices(List<Service> services) {
    List<Query> queries = new ArrayList<>();

    for (Service service : services) {
      queries.add(
          dsl.insertInto(SERVICE)
              .set(SERVICE.BUSINESS_ID, service.getBusiness().getId())
              .set(SERVICE.CATEGORY_ID, service.getCategory().getId())
              .set(SERVICE.NAME, service.getName())
              .set(SERVICE.NOTE, service.getNote())
              .set(SERVICE.PRICE, service.getPrice())
              .set(SERVICE.AVG_DURATION, service.getAvgDuration())
              .set(SERVICE.DESCRIPTION, service.getDescription())
              .set(SERVICE.TEMPLATE, service.isTemplate())
      );
    }

    dsl.batch(queries).execute();
  }

  @Override
  public boolean serviceExists(String serviceId) {
    return dsl.fetchExists(dsl.selectFrom(SERVICE).where(SERVICE.ID.eq(Integer.parseInt(serviceId))));
  }

  @Override
  public void updateService(String serviceId, Service service) {
    dsl.update(SERVICE)
        .set(SERVICE.NAME, service.getName())
        .set(SERVICE.CATEGORY_ID, service.getCategory().getId())
        .set(SERVICE.NOTE, service.getNote())
        .set(SERVICE.DESCRIPTION, service.getDescription())
        .set(SERVICE.PRICE, service.getPrice())
        .set(SERVICE.AVG_DURATION, service.getAvgDuration())
        .set(SERVICE.TEMPLATE, service.isTemplate())
        .where(SERVICE.ID.eq(Integer.parseInt(serviceId)))
        .execute();
  }

}
