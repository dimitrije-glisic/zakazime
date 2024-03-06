package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.business.repository.CustomerRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.service.UserService;
import com.dglisic.zakazime.user.service.UserServiceImpl;
import java.util.Optional;
import jooq.tables.pojos.Customer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final UserService userService;

  @Override
  public Customer createCustomer(Customer customer) {
    final var customerByEmail = customerRepository.findCustomerByEmail(customer.getEmail());
    if (customerByEmail.isPresent()) {
      throw new ApplicationException("Customer with email " + customer.getEmail() + " already exists", HttpStatus.BAD_REQUEST);
    }
    return customerRepository.save(customer);
  }

  @Override
  public Customer requireCustomerByEmail(String email) throws ApplicationException {
    return customerRepository.findCustomerByEmail(email)
        .orElseThrow(() -> new ApplicationException("Customer with email " + email + " not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public Optional<Customer> findCustomerByEmail(String email) {
    return customerRepository.findCustomerByEmail(email);
  }

  @Override
  public Customer requireCustomerById(Integer id) throws ApplicationException {
    return customerRepository.findCustomerById(id)
        .orElseThrow(() -> new ApplicationException("Customer with id " + id + " not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public Optional<Customer> findCustomerById(Integer id) {
    return customerRepository.findCustomerById(id);
  }

  @Override
  public Customer handleCustomerDataOnAppointmentCreation(Integer businessId, CustomerData customerData) {
    final String email = customerData.email();
    final Optional<Customer> customerOptional = findCustomerByEmail(email);
    // if not present create new customer
    final Customer customer = customerOptional.orElseGet(() -> createCustomer(new Customer()
        .setBusinessId(businessId)
        .setFirstName(customerData.firstName())
        .setLastName(customerData.lastName())
        .setEmail(email)
        .setPhone(customerData.phone())));
    final String password = customerData.password();
    if (StringUtils.isNotBlank(password)) {
      // create account for customer
      final RegistrationRequest registrationRequest = new RegistrationRequest(
          customer.getFirstName(), customer.getLastName(), customer.getEmail(), password,
          UserServiceImpl.RoleName.USER.toString());
      userService.registerUser(registrationRequest);
    }
    // if passed data contains password, create an account for the customer if one does not exist
    return customer;
  }
}