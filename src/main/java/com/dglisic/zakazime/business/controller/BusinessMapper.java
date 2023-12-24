package com.dglisic.zakazime.business.controller;

import jooq.tables.pojos.Business;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

  Business map(CreateBusinessProfileRequest createBusinessProfileRequest);

}
