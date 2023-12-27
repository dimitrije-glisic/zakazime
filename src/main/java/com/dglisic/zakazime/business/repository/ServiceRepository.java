package com.dglisic.zakazime.business.repository;


import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(int businessId);

  List<Service> getServiceTemplatesOfBusinessType(String type);

  Optional<Service> findServiceById(int serviceId);

  void saveServices(List<Service> services);

  void updateService(int serviceId, Service service);
}
