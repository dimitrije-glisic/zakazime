package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.service.ServiceManagement;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import jooq.tables.pojos.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("business/{businessId}")
@Slf4j
@AllArgsConstructor
public class ServiceController {

  private final ServiceManagement serviceManagement;

  @PostMapping("services")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<List<Service>> addServicesToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                             @RequestBody @Valid List<CreateServiceRequest> serviceRequests) {
    log.info("Saving services {} for business {}", serviceRequests, businessId);
    List<Service> services = serviceManagement.addServicesToBusiness(serviceRequests, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(services);
  }

  @PostMapping("single-service")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<Service> addServiceToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                      @RequestBody @Valid CreateServiceRequest serviceRequest) {
    log.info("Saving service {} for business {}", serviceRequest, businessId);
    Service service = serviceManagement.addServiceToBusiness(serviceRequest, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(service);
  }

  @PutMapping("{businessId}/services/{serviceId}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> updateService(@PathVariable @Valid @NotBlank final Integer businessId,
                                                       @PathVariable @Valid @NotBlank final Integer serviceId,
                                                       @RequestBody @Valid final UpdateServiceRequest updateRequest) {
    log.info("Updating service {} for business {}", updateRequest, businessId);
    serviceManagement.updateService(businessId, serviceId, updateRequest);
    return ResponseEntity.ok(new MessageResponse("Service updated successfully"));
  }

  @DeleteMapping("{businessId}/services/{serviceId}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> deleteService(@PathVariable @Valid @NotBlank final Integer businessId,
                                                       @PathVariable @Valid @NotBlank final Integer serviceId) {
    log.info("Deleting service {} for business {}", serviceId, businessId);
    serviceManagement.deleteService(businessId, serviceId);
    return ResponseEntity.ok(new MessageResponse("Service deleted successfully"));
  }

}
