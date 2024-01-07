package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;

public interface BusinessRepository {

  Optional<Business> getBusinessProfile(final Integer userEmail);

  Business storeBusinessProfile(final Business business, final Account owner);

  void linkBusinessToOwner(final Integer businessId, final Integer ownerId);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  Optional<Business> findBusinessById(final Integer businessId);

  Optional<Business> findBusinessByName(final String name);

  boolean isUserRelatedToBusiness(final Integer id, final Integer businessId);
}
