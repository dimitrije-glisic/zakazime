package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import org.springframework.transaction.annotation.Transactional;

public interface BusinessRepository {

  Optional<Business> getBusinessProfile(int userEmail);

  @Transactional
  Business createBusinessProfile(Business business, int ownerId);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  Optional<Business> findBusinessById(int businessId);

}
