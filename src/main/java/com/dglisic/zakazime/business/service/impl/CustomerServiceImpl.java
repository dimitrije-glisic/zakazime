package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.business.repository.CustomerRepository;
import com.dglisic.zakazime.business.service.CustomerService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.controller.RegistrationRequest;
import com.dglisic.zakazime.user.service.UserService;
import com.dglisic.zakazime.user.service.UserServiceImpl;
import java.util.List;
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
  public Optional<Customer> findCustomerOfBusinessByEmail(Integer businessId, String email) {
    return customerRepository.findCustomerOfBusinessByEmail(businessId, email);
  }

  @Override
  public List<Customer> findCustomersByEmail(String email) {
    return customerRepository.findCustomersByEmail(email);
  }

  @Override
  public Optional<Customer> findCustomerById(Integer id) {
    return customerRepository.findCustomerById(id);
  }

  @Override
  public Customer handleCustomerDataOnAppointmentCreation(Integer businessId, CustomerData customerData) {
    final String email = customerData.email();
    final Optional<Customer> customerOptional = customerRepository.findCustomerOfBusinessByEmail(businessId, email);
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
          customer.getFirstName(), customer.getLastName(), customer.getPhone(), customer.getEmail(), password,
          UserServiceImpl.RoleName.USER.toString());
      userService.registerUser(registrationRequest);
    }
    // if passed data contains password, create an account for the customer if one does not exist
    return customer;
  }

  @Override
  public Customer requireCustomerExistsAndReturn(Integer customerId) {
    return customerRepository.findCustomerById(customerId)
        .orElseThrow(() -> new ApplicationException("Customer with id " + customerId + " not found", HttpStatus.BAD_REQUEST));
  }

  @Override
  public Customer getCustomer(Integer businessId, Integer customerId) {
    //todo: check if customer belongs to business
    return findCustomerById(customerId)
        .orElseThrow(() -> new ApplicationException("Customer with id " + customerId + " not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public List<Customer> getAllCustomersForBusiness(Integer businessId) {
    return customerRepository.getAllCustomersForBusiness(businessId);
  }

  @Override
  public Customer createCustomer(Integer businessId, CustomerData request) {
    final var customer = new Customer()
        .setBusinessId(businessId)
        .setFirstName(request.firstName())
        .setLastName(request.lastName())
        .setEmail(request.email())
        .setPhone(request.phone());
    return createCustomer(customer);
  }

  @Override
  public Customer updateCustomer(Integer businessId, Integer customerId, CustomerData request) {
    final var customer = requireCustomerExistsAndReturn(customerId);
    customer.setFirstName(request.firstName());
    customer.setLastName(request.lastName());
    customer.setEmail(request.email());
    customer.setPhone(request.phone());
    return customerRepository.update(customer);
  }

  private Customer createCustomer(Customer customer) {
    final var customerByEmail = customerRepository.findCustomerOfBusinessByEmail(customer.getBusinessId(), customer.getEmail());
    if (customerByEmail.isPresent()) {
      throw new ApplicationException("Customer with email " + customer.getEmail() + " already exists", HttpStatus.BAD_REQUEST);
    }
    return customerRepository.save(customer);
  }

}
