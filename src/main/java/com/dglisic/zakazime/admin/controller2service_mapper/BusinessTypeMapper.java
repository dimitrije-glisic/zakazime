package com.dglisic.zakazime.admin.controller2service_mapper;

import com.dglisic.zakazime.admin.controller.BusinessTypeDTO;
import com.dglisic.zakazime.admin.controller.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.domain.BusinessType;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface BusinessTypeMapper {

  BusinessTypeDTO toDTO(BusinessType businessType);

  List<BusinessTypeDTO> toDTO(List<BusinessType> businessType);

  BusinessType toDomain(CreateBusinessTypeRequest request);
}
