package com.dglisic.zakazime.admin.repository;

import com.dglisic.zakazime.business.domain.BusinessType;
import java.util.List;
import java.util.Optional;

public interface BusinessTypeRepository {

  List<BusinessType> getAllBusinessTypes();

  Optional<BusinessType> findBusinessTypeById(int id);

  BusinessType save(BusinessType businessType);

  void update(BusinessType request);

  void delete(int businessTypeId);
}
