package com.dglisic.zakazime.repository;

import java.util.Optional;
import model.tables.records.BusinessProfileRecord;

public interface BusinessRepository {

  int saveBusinessProfile(BusinessProfileRecord businessProfile);
  Optional<BusinessProfileRecord> getBusinessProfile(int userEmail);

}
