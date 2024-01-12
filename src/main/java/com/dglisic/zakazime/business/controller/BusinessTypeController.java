package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import java.util.List;
import jooq.tables.pojos.BusinessType;
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
@RequestMapping("business-types")
@RequiredArgsConstructor
public class BusinessTypeController {
  private static final Logger logger = getLogger(BusinessTypeController.class);

  private final BusinessTypeService businessTypeService;

  @GetMapping
  public List<BusinessType> getBusinessTypes() {
    logger.info("Getting business types");
    return businessTypeService.getAll();
  }

  @GetMapping("/{id}")
  public BusinessType getBusinessTypeById(@PathVariable final Integer id) {
    logger.info("Getting business type with id {}", id);
    return businessTypeService.requireById(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public BusinessType createBusinessType(@Valid @RequestBody final CreateBusinessTypeRequest createRequest) {
    logger.info("Creating business type {}", createRequest);
    return businessTypeService.create(createRequest);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public MessageResponse updateBusinessType(@PathVariable final Integer id,
                                            @RequestBody final UpdateBusinessTypeRequest businessType) {
    logger.info("Updating business type with id {} to {}", id, businessType);
    businessTypeService.update(id, businessType);
    return new MessageResponse("Business type updated successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse deleteBusinessType(@PathVariable final Integer id) {
    logger.info("Deleting business type with id {}", id);
    businessTypeService.delete(id);
    return new MessageResponse("Business type deleted successfully");
  }

}
