package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.service.ServiceService;
import jakarta.annotation.Nullable;
import java.util.List;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;

  @Override
  public List<Service> searchServiceTemplates(@Nullable final String businessType, @Nullable final String category,
                                              @Nullable final String subcategory) {
    return serviceRepository.searchServiceTemplates(businessType, category, subcategory);
  }
}
