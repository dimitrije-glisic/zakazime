package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Customer.CUSTOMER;

import com.dglisic.zakazime.business.repository.CustomerRepository;
import java.util.Optional;
import jooq.tables.pojos.Customer;
import jooq.tables.records.CustomerRecord;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

  private final DSLContext jooq;

  @Override
  public Customer save(Customer customer) {
    final CustomerRecord customerRecord = jooq.newRecord(CUSTOMER, customer);
    customerRecord.store();
    return customerRecord.into(Customer.class);
  }

  @Override
  public Customer update(Customer customer) {
    return jooq.update(CUSTOMER)
        .set(CUSTOMER.FIRST_NAME, customer.getFirstName())
        .set(CUSTOMER.LAST_NAME, customer.getLastName())
        .set(CUSTOMER.EMAIL, customer.getEmail())
        .set(CUSTOMER.PHONE, customer.getPhone())
        .where(CUSTOMER.ID.eq(customer.getId()))
        .returning()
        .fetchOneInto(Customer.class);
  }

  @Override
  public Optional<Customer> findCustomerByEmail(String email) {
    return jooq.selectFrom(CUSTOMER)
        .where(CUSTOMER.EMAIL.eq(email))
        .fetchOptionalInto(Customer.class);
  }

  @Override
  public Optional<Customer> findCustomerById(Integer id) {
    return jooq.selectFrom(CUSTOMER)
        .where(CUSTOMER.ID.eq(id))
        .fetchOptionalInto(Customer.class);
  }

}
