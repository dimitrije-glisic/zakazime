package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.BusinessProfile;
import java.util.List;
import java.util.Optional;

public interface BusinessRepository {

//  int saveBusinessProfile(BusinessProfile businessProfile);
  Optional<BusinessProfile> getBusinessProfile(int userEmail);

  BusinessProfile createBusinessProfile(BusinessProfile businessProfile);

  List<BusinessProfile> getAll();
}
