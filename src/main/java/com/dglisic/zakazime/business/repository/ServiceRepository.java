package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(int businessId);

  List<Service> getServiceTemplatesOfType(String type);

  void saveServices(List<Service> services);

  boolean serviceExists(String serviceId);

  Optional<Service> findService(String serviceId);

  void updateService(String serviceId, Service service);
}
