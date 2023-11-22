package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.Category;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(int businessId);

  List<Service> getServicesOfType(String type);

  void saveServices(List<Service> services);

}
