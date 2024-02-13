package com.dglisic.zakazime.business.controller.dto;

import jooq.tables.pojos.Business;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "description", ignore = true)
  Business map(final CreateBusinessProfileRequest createBusinessProfileRequest);

}
