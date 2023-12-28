package com.dglisic.zakazime.business.repository;

import java.util.List;
import jooq.tables.pojos.BusinessType;

public interface BusinessTypeRepository {

  List<BusinessType> getAll();

}
