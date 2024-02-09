package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;

public interface BusinessRepository {

  Optional<Business> getBusinessProfile(final Integer userEmail);

  Business storeBusinessProfile(final Business business, final Account owner);

  void linkBusinessToOwner(final Integer businessId, final Integer ownerId);

  List<Business> getAll();

  Optional<Business> findBusinessById(final Integer businessId);

  Optional<Business> findBusinessByName(final String name);

  boolean isUserRelatedToBusiness(final Integer id, final Integer businessId);

  void linkPredefined(List<Integer> categoryIds, Integer businessId);

  List<PredefinedCategory> getPredefinedCategories(Integer businessId);

  List<UserDefinedCategory> getUserDefinedCategories(Integer businessId);

  void createUserDefinedCategory(UserDefinedCategory category);

  List<Service> getServicesOfBusiness(Integer businessId);

  boolean serviceBelongsToBusiness(Integer serviceId, Integer businessId);
}
