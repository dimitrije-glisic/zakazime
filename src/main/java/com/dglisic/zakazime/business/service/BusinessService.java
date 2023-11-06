package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.BusinessProfileDTO;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.domain.BusinessProfile;
import com.dglisic.zakazime.user.domain.User;
import java.util.List;

public interface BusinessService {

  BusinessProfile getBusinessProfileForUser(String userEmail);

  BusinessProfile createBusinessProfile(CreateBusinessProfileRequest createBusinessProfileRequest);

  List<BusinessProfile> getAll();

}
