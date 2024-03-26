package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Customer;

public interface CustomerService {

  Optional<Customer> findCustomerOfBusinessByEmail(Integer businessId, String email);

  /*
    * Find customers by email. One email can be associated with multiple customer entries since
    * one user can be a customer of multiple businesses.
   */
  List<Customer> findCustomersByEmail(String email);

  Optional<Customer> findCustomerById(Integer id);

  Customer handleCustomerDataOnAppointmentCreation(Integer businessId, CustomerData customerData);

  Customer requireCustomerExistsAndReturn(Integer customerId);

  Customer getCustomer(Integer businessId, Integer customerId);

  List<Customer> getAllCustomersForBusiness(Integer businessId);

  Customer createCustomer(Integer businessId, CustomerData request);

  Customer updateCustomer(Integer businessId, Integer customerId, CustomerData request);
}
