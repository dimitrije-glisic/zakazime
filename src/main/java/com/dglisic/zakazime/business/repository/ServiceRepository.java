package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;

public interface ServiceRepository {

  Optional<Service> findServiceById(@NotNull final Integer serviceId);

  List<Service> saveServices(@NotNull final List<Service> services);

  Service create(@NotNull final Service service);

  void update(@NotNull final Service service);

  boolean existsByTitleAndBusinessId(String title, Integer businessId);

  void delete(Integer serviceId);

  List<Service> findByEmployeeId(Integer employeeId);
}
