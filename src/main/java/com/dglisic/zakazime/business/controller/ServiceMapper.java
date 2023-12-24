package com.dglisic.zakazime.business.controller;

import jooq.tables.pojos.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
  Service map(CreateServiceRequest request);
}
