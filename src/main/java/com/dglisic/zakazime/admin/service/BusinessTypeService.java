package com.dglisic.zakazime.admin.service;

import com.dglisic.zakazime.business.domain.BusinessType;
import java.util.List;

public interface BusinessTypeService {

  List<BusinessType> getAllBusinessTypes();

  BusinessType getBusinessTypeById(int businessTypeId);

  BusinessType createBusinessType(BusinessType request);

  void updateBusinessType(BusinessType request);

  void deleteBusinessType(int businessTypeId);

}
