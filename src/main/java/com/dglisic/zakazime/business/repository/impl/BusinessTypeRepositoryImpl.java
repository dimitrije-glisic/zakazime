package com.dglisic.zakazime.business.repository.impl;

import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import java.util.List;
import jooq.tables.daos.BusinessTypeDao;
import jooq.tables.pojos.BusinessType;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessTypeRepositoryImpl extends BusinessTypeDao implements BusinessTypeRepository {

  @Override
  public List<BusinessType> getAll() {
    return super.findAll();
  }

}
