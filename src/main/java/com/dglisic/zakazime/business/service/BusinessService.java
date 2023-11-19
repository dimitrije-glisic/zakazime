package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import java.util.List;

public interface BusinessService {

  BusinessProfile getBusinessProfileForUser(String userEmail);

  BusinessProfile createBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest);

  List<BusinessProfile> getAll();

  List<BusinessType> getBusinessTypes();

  List<Service> getServicesForType(String type);
}
