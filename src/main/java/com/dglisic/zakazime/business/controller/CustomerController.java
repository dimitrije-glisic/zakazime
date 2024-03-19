package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CustomerData;
import com.dglisic.zakazime.business.controller.dto.CustomerDto;
import com.dglisic.zakazime.business.service.BusinessService;
import com.dglisic.zakazime.business.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import jooq.tables.pojos.Customer;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/{businessId}/customers")
@AllArgsConstructor
public class CustomerController {

  private final BusinessService businessService;
  private final CustomerService customerService;

  // consider removing this and dto
  @GetMapping("/all-full")
  public ResponseEntity<List<CustomerDto>> getAllCustomers(@PathVariable Integer businessId) {
    return ResponseEntity.ok(businessService.getAllCustomersForBusiness(businessId));
  }

  @GetMapping("/all")
  public ResponseEntity<List<Customer>> getAllForBusiness(@PathVariable Integer businessId) {
    return ResponseEntity.ok(customerService.getAllCustomersForBusiness(businessId));
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<Customer> getCustomer(@PathVariable Integer businessId, @PathVariable Integer customerId) {
    return ResponseEntity.ok(customerService.getCustomer(businessId, customerId));
  }

  @PostMapping
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<Customer> createCustomer(@PathVariable Integer businessId,
                                                 @RequestBody @Valid CustomerData request) {
    return ResponseEntity.status(201).body(customerService.createCustomer(businessId, request));
  }

  @PutMapping("/{customerId}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<Customer> updateCustomer(@PathVariable Integer businessId,
                                                 @PathVariable Integer customerId,
                                                 @RequestBody @Valid CustomerData request) {
    return ResponseEntity.ok(customerService.updateCustomer(businessId, customerId, request));
  }

}
