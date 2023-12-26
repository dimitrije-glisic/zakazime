package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;

public interface BusinessRepository {

  Optional<Business> getBusinessProfile(int userEmail);

  Business storeBusinessProfile(Business business, Account owner);

  void linkBusinessToOwner(int businessId, int ownerId);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  Optional<Business> findBusinessById(int businessId);

  Optional<Business> findBusinessByName(String name);
}
