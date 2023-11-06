package com.dglisic.zakazime.service;

import com.dglisic.zakazime.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.domain.BusinessProfile;

public interface BusinessService {

  BusinessProfile getBusinessProfileForUser(String userEmail);

  BusinessProfile createBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest);
}
