package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.ServiceCategory;

public interface ServiceCategoryRepository {

  @NotNull List<ServiceCategory> getAll();

  Optional<ServiceCategory> findById(final Integer id);

  @NotNull ServiceCategory store(final ServiceCategory serviceCategory);

  void delete(final Integer id);

  Optional<Object> findByTitle(@NotBlank final String title);

  void updateImage(@NotNull final Integer id, @NotNull final String url);
}
