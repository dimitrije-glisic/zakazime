package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Category;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  Business createBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  List<Service> getServiceTemplatesOfType(String type);

  List<Service> getServicesOfBusiness(String businessName);

  void saveServices(List<Service> services);

  Business getBusinessOrThrow(String businessName);

  Category getCategoryOrThrow(String categoryName);

  void updateService(String serviceId, Service service);
}
