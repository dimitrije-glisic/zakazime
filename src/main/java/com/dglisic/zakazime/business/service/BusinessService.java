package com.dglisic.zakazime.business.service;

import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.ServiceCategory;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  Business createBusinessProfile(Business toBeCreated);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  void updateService(int serviceId, Service service, int businessId);

  List<Service> getServiceTemplatesOfType(String type);

  List<Service> getServicesOfBusiness(int businessId);

  void saveServicesForBusiness(List<Service> services,int businessId);

  Business getBusinessOrThrow(int businessId);

  ServiceCategory getCategoryOrThrow(String categoryName);

}
