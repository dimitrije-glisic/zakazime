package com.dglisic.zakazime.admin.repository2service_mapper;

import com.dglisic.zakazime.business.domain.BusinessType;
import model.tables.records.BusinessTypeRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BusinessTypeMapper {
  @Mapping(target = "id", expression = "java(record.getId())")
  @Mapping(target = "title", expression = "java(record.getName())")
  BusinessType toDomain(BusinessTypeRecord record);
}
