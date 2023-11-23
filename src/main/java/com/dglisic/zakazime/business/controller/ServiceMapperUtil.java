package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Service;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
class ServiceMapperUtil {

  public ServiceDTO mapToServiceDTO(Service service) {
    return ServiceDTO.builder()
        .id(service.getId())
        .name(service.getName())
        .categoryName(service.getCategory().getName())
        .businessName(service.getBusiness() != null ? service.getBusiness().getName() : null)
        .note(service.getNote())
        .description(service.getDescription())
        .price(service.getPrice())
        .avgDuration(service.getAvgDuration())
        .template(service.isTemplate())
        .build();
  }

  public static List<ServiceDTO> mapToServiceDTOs(List<Service> services) {
    return services.stream()
        .map(ServiceMapperUtil::mapToServiceDTO)
        .toList();
  }
}
