package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import jooq.tables.pojos.Business;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(source = "serviceKinds", target = "serviceKind", qualifiedByName = "mapServiceKinds")
  Business map(final CreateBusinessProfileRequest createBusinessProfileRequest);

  @Named("mapServiceKinds")
  default String mapServiceKinds(List<ServiceKind> serviceKinds) {
    if (serviceKinds.size() > 1) {
      return "Frizersko-kozmetički salon";
    } else {
      return serviceKinds.get(0).getValue();
    }
  }

}
