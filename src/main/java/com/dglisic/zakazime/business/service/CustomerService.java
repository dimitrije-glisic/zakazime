package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.Optional;
import jooq.tables.pojos.Customer;

public interface CustomerService {

  Customer createCustomer(Customer customer);

  Customer requireCustomerByEmail(String email) throws ApplicationException;

  Optional<Customer> findCustomerByEmail(String email);

  Customer requireCustomerById(Integer id) throws ApplicationException;

  Optional<Customer> findCustomerById(Integer id);

  Customer handleCustomerDataOnAppointmentCreation(Integer businessId, CustomerData customerData);

}
