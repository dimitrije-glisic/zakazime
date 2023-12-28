package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.service.BusinessTypeService;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
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

}
