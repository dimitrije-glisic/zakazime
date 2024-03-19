package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Customer;

public interface CustomerService {

  Customer createCustomer(Customer customer);

  Customer requireCustomerByEmail(String email) throws ApplicationException;

  Optional<Customer> findCustomerByEmail(String email);

  Customer requireCustomerById(Integer id) throws ApplicationException;

  Optional<Customer> findCustomerById(Integer id);

  Customer handleCustomerDataOnAppointmentCreation(Integer businessId, CustomerData customerData);

  Customer requireCustomerExistsAndReturn(Integer customerId);

  Customer getCustomer(Integer businessId, Integer customerId);

  List<Customer> getAllCustomersForBusiness(Integer businessId);

  Customer createCustomer(Integer businessId, CustomerData request);

  Customer updateCustomer(Integer businessId, Integer customerId, CustomerData request);
}
