package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.service.ServiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service-templates")
@RequiredArgsConstructor

public class ServiceTemplateController {
  private static final Logger logger = getLogger(ServiceTemplateController.class);

  private final ServiceService serviceService;

  @GetMapping
  public List<Service> getServiceTemplates(@RequestParam(required = false) @Valid @NotBlank String businessType,
                                           @RequestParam(required = false) @Valid @NotBlank String category,
                                           @RequestParam(required = false) @Valid @NotBlank String subcategory) {
    logger.info("Fetching services of type {}, category {}, subcategory {}", businessType, category, subcategory);
    List<Service> serviceTemplates = serviceService.searchServiceTemplates(businessType, category, subcategory);
    logger.info("Found {} services of type {}, category {}, subcategory {}", serviceTemplates.size(), businessType, category,
        subcategory);
    return serviceTemplates;
  }

}
