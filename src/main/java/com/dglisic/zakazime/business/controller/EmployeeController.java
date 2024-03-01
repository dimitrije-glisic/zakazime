package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateEmployeeRequest;
import com.dglisic.zakazime.business.service.EmployeeService;
import com.dglisic.zakazime.common.MessageResponse;
import java.util.List;
import jooq.tables.pojos.Employee;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/{businessId}/employee")
@AllArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<Employee> createEmployee(@PathVariable Integer businessId,
                                                 @RequestBody CreateEmployeeRequest request) {
    return ResponseEntity.status(201).body(employeeService.createEmployee(businessId, request));
  }

  @GetMapping("/all")
  public ResponseEntity<List<Employee>> getAll(@PathVariable Integer businessId) {
    return ResponseEntity.status(200).body(employeeService.getAll(businessId));
  }

  @GetMapping("/{employeeId}")
  public ResponseEntity<Employee> findById(@PathVariable Integer businessId, @PathVariable Integer employeeId) {
    return ResponseEntity.status(200).body(employeeService.findById(businessId, employeeId));
  }

  @PatchMapping("/{employeeId}/activate")
  public ResponseEntity<MessageResponse> activate(@PathVariable Integer businessId, @PathVariable Integer employeeId) {
    employeeService.activate(businessId, employeeId);
    return ResponseEntity.status(200).body(new MessageResponse("Employee activated"));
  }

  @PatchMapping("/{employeeId}/deactivate")
  public ResponseEntity<MessageResponse> deactivate(@PathVariable Integer businessId, @PathVariable Integer employeeId) {
    employeeService.deactivate(businessId, employeeId);
    return ResponseEntity.status(200).body(new MessageResponse("Employee deactivated"));
  }

  @PutMapping("/{employeeId}")
  public ResponseEntity<MessageResponse> update(@PathVariable Integer businessId, @PathVariable Integer employeeId,
                                                @RequestBody CreateEmployeeRequest request) {

    employeeService.update(businessId, employeeId, request);
    return ResponseEntity.status(200).body(new MessageResponse("Employee updated"));
  }

}
