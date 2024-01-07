package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jooq.tables.pojos.ServiceSubcategory;

public interface ServiceSubcategoryRepository {

  boolean allExist(@NotNull final Set<Integer> subCategoryIds);

  boolean exists(@NotNull final Integer integer);

  void delete(@NotNull final Integer id);

  @NotNull ServiceSubcategory store(@NotNull final ServiceSubcategory serviceSubcategory);

  @NotNull List<ServiceSubcategory> getAll();

  Optional<ServiceSubcategory> findById(@NotNull final Integer id);

  Optional<ServiceSubcategory> findByTitle(@NotNull final String title);

}
