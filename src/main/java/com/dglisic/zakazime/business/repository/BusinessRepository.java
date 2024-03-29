package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.controller.dto.CustomerDto;
import com.dglisic.zakazime.business.service.impl.BusinessStatus;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;

public interface BusinessRepository {

  Optional<Business> getBusinessProfile(final Integer userEmail);

  Business storeBusinessProfile(final Business business);

  List<Business> getAll();

  Optional<Business> findById(final Integer businessId);

  Optional<Business> findBusinessByName(final String name);

  Optional<Business> findBusinessByCityAndName(final String city, final String name);

  boolean isUserRelatedToBusiness(final Integer id, final Integer businessId);

  void linkPredefined(List<Integer> categoryIds, Integer businessId);

  List<PredefinedCategory> getPredefinedCategories(Integer businessId);

  List<UserDefinedCategory> getUserDefinedCategories(Integer businessId);


  List<Service> getServicesOfBusiness(Integer businessId);

  Optional<Service> findServiceOfBusiness(Integer serviceId, Integer businessId);

  boolean serviceBelongsToBusiness(Integer serviceId, Integer businessId);

  List<Business> searchBusinesses(String city, String businessType, String category);

  List<Business> getAllBusinessesInCity(String city);

  void updateProfileImageUrl(Integer businessId, String imageUrl);

  Optional<BusinessImage> getProfileImage(Integer businessId);

  @NotNull List<Business> getAllWithStatus(BusinessStatus businessStatus);

  void updateStatus(Integer businessId, BusinessStatus businessStatus);

  void changeStatus(Integer businessId, String string);

  List<Employee> getEmployees(Integer businessId);

  List<Employee> getEmployeesForService(Integer serviceId);

  List<Customer> getAllCustomersForBusiness(Integer businessId);
}
