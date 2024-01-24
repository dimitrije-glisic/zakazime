package com.dglisic.zakazime.business.repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(@NotNull final Integer businessId);

  Optional<Service> findServiceById(@NotNull final Integer serviceId);

  void saveServices(@NotNull final List<Service> services);

  Service create(@NotNull final Service service);

  void update(@NotNull final Service service);

  Optional<Service> findByTitle(@NotBlank final String title);

  void updateServiceTemplate(final Service request);

  void deleteServiceTemplate(final Integer id);
}
