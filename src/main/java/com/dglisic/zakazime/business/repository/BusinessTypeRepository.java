package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.BusinessType;

public interface BusinessTypeRepository {

  List<BusinessType> getAll();

  Optional<Object> findById(final @NotNull Integer businessTypeId);
}
