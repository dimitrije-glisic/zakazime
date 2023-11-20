package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.Category;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

  List<Service> getServicesOfBusiness(int businessId);

  void saveServices(List<Service> services);

  Optional<Category> findCategoryByName(String categoryName);
}
