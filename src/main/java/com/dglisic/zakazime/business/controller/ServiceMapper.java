package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceMapper {

  private final BusinessService businessService;

  public Service mapToService(CreateServiceRequest request, String businessName) {
    return Service.builder()
        .name(request.name())
        .category(businessService.getCategoryOrThrow(request.categoryName()))
        .business(businessService.getBusinessOrThrow(businessName))
        .note(request.note())
        .description(request.description())
        .price(request.price())
        .avgDuration(request.avgDuration())
        .template(false)
        .build();
  }

  public ServiceDTO mapToServiceDTO(Service service) {
    return ServiceDTO.builder()
        .id(service.getId())
        .name(service.getName())
        .categoryName(service.getCategory().getName())
        .businessName(service.getBusiness().getName())
        .note(service.getNote())
        .description(service.getDescription())
        .price(service.getPrice())
        .avgDuration(service.getAvgDuration())
        .template(service.isTemplate())
        .build();
  }

}
