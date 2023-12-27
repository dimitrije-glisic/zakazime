package com.dglisic.zakazime.business.controller;

import jooq.tables.pojos.Service;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ServiceMapper {
  Service map(final CreateServiceRequest request);

  Service map(final UpdateServiceRequest updateServiceRequest);
}
