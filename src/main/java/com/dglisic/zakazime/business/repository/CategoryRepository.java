package com.dglisic.zakazime.business.repository;

import java.util.Optional;
import jooq.tables.pojos.ServiceCategory;

public interface CategoryRepository {

  Optional<ServiceCategory> findCategory(String categoryName);
}
