package com.dglisic.zakazime.business.repository;

import static model.tables.Service.SERVICE;
import static model.tables.ServiceCategory.SERVICE_CATEGORY;

import com.dglisic.zakazime.business.domain.Category;
import com.dglisic.zakazime.business.domain.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import model.tables.records.ServiceCategoryRecord;
import model.tables.records.ServiceRecord;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Result;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;

  @Override
  public List<Service> getServicesOfBusiness(int businessId) {
    Result<ServiceRecord> result = dsl.selectFrom(SERVICE)
        .where(SERVICE.BUSINESS_ID.eq(businessId))
        .fetch();
    return result.map(Service::new);
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
  @Cacheable("categories")
  public Optional<Category> findCategoryByName(String categoryName) {
    ServiceCategoryRecord categoryRecord = dsl.selectFrom(SERVICE_CATEGORY)
        .where(SERVICE_CATEGORY.NAME.eq(categoryName))
        .fetchOne();

    if (categoryRecord == null) {
      return Optional.empty();
    } else {
      return Optional.of(new Category(categoryRecord));
    }
  }

}
