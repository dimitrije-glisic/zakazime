package com.dglisic.zakazime.business.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.BusinessType;

public interface BusinessTypeRepository {

  List<BusinessType> getAll();

  Optional<BusinessType> findById(@NotNull final Integer businessTypeId);

  boolean existsByTitle(@NotBlank final String title);

  BusinessType create(@NotNull @Valid final BusinessType businessType);

  void update(@NotNull @Valid final BusinessType inUpdate);

  void deleteById(@NotNull final Integer id);
}
