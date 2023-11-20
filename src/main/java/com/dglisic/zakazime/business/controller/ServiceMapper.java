package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

  public Service mapToService(CreateServiceRequest request) {
    return Service.builder()
        .name(request.name())
        .categoryName(request.categoryName())
        .note(request.note())
        .description(request.description())
        .price(request.price())
        .avgDuration(request.avgDuration())
        .template(false)
        .build();
  }

}
