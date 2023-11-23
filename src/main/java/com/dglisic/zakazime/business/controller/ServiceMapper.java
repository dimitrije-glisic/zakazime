package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.service.BusinessService;
import java.util.List;
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
    return ServiceMapperUtil.mapToServiceDTO(service);
  }

  public List<ServiceDTO> mapToServiceDTOs(List<Service> services) {
    return ServiceMapperUtil.mapToServiceDTOs(services);
  }
}
