package com.dglisic.zakazime.business.service;

import java.util.List;
import jooq.tables.pojos.BusinessType;

public interface BusinessTypeService {

  List<BusinessType> getAll();

}
