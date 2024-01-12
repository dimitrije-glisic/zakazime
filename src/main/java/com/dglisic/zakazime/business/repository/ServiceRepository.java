package com.dglisic.zakazime.business.repository;


import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(int businessId);

  List<Service> searchServiceTemplates(final @Nullable String businessType, final @Nullable String category,
                                       final @Nullable String subcategory);

  Optional<Service> findServiceById(int serviceId);

  void saveServices(final List<Service> services);

  void create(final Service service);

  void update(final Service service);
}
