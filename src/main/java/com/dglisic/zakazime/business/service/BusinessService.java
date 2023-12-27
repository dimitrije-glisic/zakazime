package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.pojos.Service;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  void updateService(int serviceId, Service service, int businessId);

  List<Service> getServiceTemplatesOfType(String type);

  List<Service> getServicesOfBusiness(int businessId);

  void saveServicesForBusiness(List<Service> services,int businessId);

  Business create(CreateBusinessProfileRequest createBusinessProfileRequest);

  Optional<Business> findBusinessById(int businessId);
}
