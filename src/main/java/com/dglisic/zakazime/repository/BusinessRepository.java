package com.dglisic.zakazime.repository;

import com.dglisic.zakazime.domain.BusinessProfile;
import java.util.Optional;

public interface BusinessRepository {

//  int saveBusinessProfile(BusinessProfile businessProfile);
  Optional<BusinessProfile> getBusinessProfile(int userEmail);

  BusinessProfile createBusinessProfile(BusinessProfile businessProfile);
}
