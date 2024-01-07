package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessTypeServiceImpl implements BusinessTypeService {

  private final BusinessTypeRepository businessTypeRepository;

  @Override
  public List<BusinessType> getAll() {
    return businessTypeRepository.getAll();
  }
}
