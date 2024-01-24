package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.service.ServiceTemplateService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service-templates")
@RequiredArgsConstructor

public class ServiceTemplateController {
  private static final Logger logger = getLogger(ServiceTemplateController.class);

  private final ServiceTemplateService serviceTemplateService;

  @GetMapping("/{id}")
  public Service getServiceTemplate(@PathVariable final Integer id) {
    logger.info("Fetching service template with id {}", id);
    Service serviceTemplate = serviceTemplateService.getService(id);
    logger.info("Found {} service template with id {}", serviceTemplate, id);
    return serviceTemplate;
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Service createServiceTemplate(@RequestBody @Valid final CreateServiceRequest createRequest) {
    logger.info("Creating service template {}", createRequest);
    return serviceTemplateService.createService(createRequest);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse updateServiceTemplate(@PathVariable final Integer id,
                                               @RequestBody @Valid final UpdateServiceRequest updateRequest) {
    serviceTemplateService.updateServiceTemplate(id, updateRequest);
    return new MessageResponse("Service template updated successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse deleteServiceTemplate(@PathVariable final Integer id) {
    serviceTemplateService.deleteServiceTemplate(id);
    return new MessageResponse("Service template deleted successfully");
  }

}
