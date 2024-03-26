package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.EmployeeServiceMap.EMPLOYEE_SERVICE_MAP;
import static jooq.tables.Service.SERVICE;
import static jooq.tables.UserDefinedCategory.USER_DEFINED_CATEGORY;

import com.dglisic.zakazime.business.repository.ServiceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jooq.tables.daos.ServiceDao;
import jooq.tables.pojos.Service;
import jooq.tables.records.ServiceRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

  private final DSLContext dsl;
  private final ServiceDao serviceDao;

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

  @Override
  public List<Service> findByEmployeeId(Integer employeeId) {
    return dsl.select(SERVICE)
        .from(SERVICE)
        .join(EMPLOYEE_SERVICE_MAP).on(SERVICE.ID.eq(EMPLOYEE_SERVICE_MAP.SERVICE_ID))
        .where(EMPLOYEE_SERVICE_MAP.EMPLOYEE_ID.eq(employeeId))
        .fetchInto(Service.class);
  }

}
