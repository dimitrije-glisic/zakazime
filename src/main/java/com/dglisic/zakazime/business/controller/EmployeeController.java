package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateEmployeeRequest;
import com.dglisic.zakazime.business.controller.dto.EmployeeRichObject;
import com.dglisic.zakazime.business.controller.dto.WorkingHoursRequest;
import com.dglisic.zakazime.business.service.EmployeeService;
import com.dglisic.zakazime.common.MessageResponse;
import java.util.List;
import jooq.tables.pojos.Employee;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/{businessId}/employees")
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

  @GetMapping("/for-service/{serviceId}")
  public ResponseEntity<List<Employee>> getAllForService(@PathVariable Integer businessId, @PathVariable Integer serviceId) {
    return ResponseEntity.status(200).body(employeeService.getAllForService(businessId, serviceId));
  }

  @GetMapping("/{employeeId}")
  public ResponseEntity<Employee> findById(@PathVariable Integer businessId, @PathVariable Integer employeeId) {
    return ResponseEntity.status(200).body(employeeService.findById(businessId, employeeId));
  }

  @GetMapping("/{employeeId}/full")
  public ResponseEntity<EmployeeRichObject> findByIdFull(@PathVariable Integer businessId, @PathVariable Integer employeeId) {
    return ResponseEntity.status(200).body(employeeService.findByIdFull(businessId, employeeId));
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

  // ========================================
  // =========== Services ====================
  // ========================================

  @PostMapping("/{employeeId}/services")
  public ResponseEntity<MessageResponse> addServices(@PathVariable Integer businessId, @PathVariable Integer employeeId,
                                                     @RequestBody List<Integer> serviceIds) {
    employeeService.addServices(businessId, employeeId, serviceIds);
    return ResponseEntity.status(201).body(new MessageResponse("Services added"));
  }

  @DeleteMapping("/{employeeId}/services/{serviceId}")
  public ResponseEntity<MessageResponse> deleteService(@PathVariable Integer businessId, @PathVariable Integer employeeId,
                                                       @PathVariable Integer serviceId) {
    employeeService.deleteService(businessId, employeeId, serviceId);
    return ResponseEntity.status(200).body(new MessageResponse("Service deleted"));
  }

  @GetMapping("/{employeeId}/services")
  public ResponseEntity<List<jooq.tables.pojos.Service>> getAllServices(@PathVariable Integer businessId,
                                                                        @PathVariable Integer employeeId) {
    return ResponseEntity.status(200).body(employeeService.getAllServices(businessId, employeeId));
  }

  // ========================================
  // =========== Working hours ===============
  // ========================================

  @PostMapping("/{employeeId}/working-hours")
  public ResponseEntity<MessageResponse> setWorkingHours(@PathVariable Integer businessId, @PathVariable Integer employeeId,
                                                         @RequestBody WorkingHoursRequest request) {
    employeeService.setWorkingHours(businessId, employeeId, request);
    return ResponseEntity.status(201).body(new MessageResponse("Working hours set"));
  }

}
