package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  Business createBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  List<Service> getServicesForType(String type);

  List<Service> getServicesOfBusiness(String businessName);

  @Transactional
  void saveServices(List<Service> services, String businessName);
}
