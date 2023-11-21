package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import java.util.Optional;

public interface BusinessRepository {

//  int saveBusinessProfile(BusinessProfile businessProfile);
  Optional<Business> getBusinessProfile(int userEmail);

  Business createBusinessProfile(Business business);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  List<Service> getServicesForType(String type);

  Optional<Business> findBusinessByName(String businessName);

}
