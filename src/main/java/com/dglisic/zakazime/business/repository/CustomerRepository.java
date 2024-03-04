package com.dglisic.zakazime.business.repository;

import java.util.Optional;
import jooq.tables.pojos.Customer;

public interface CustomerRepository {

  Customer save(Customer customer);

  Customer update(Customer customer);

  Optional<Customer> findCustomerByEmail(String email);

  Optional<Customer> findCustomerById(Integer id);

}
