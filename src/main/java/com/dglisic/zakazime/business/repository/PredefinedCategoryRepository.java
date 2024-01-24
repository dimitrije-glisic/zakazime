package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.PredefinedCategory;

public interface PredefinedCategoryRepository {

  @NotNull List<PredefinedCategory> getAll();

  Optional<PredefinedCategory> findById(final Integer id);

  @NotNull PredefinedCategory store(final PredefinedCategory serviceCategory);

  void delete(final Integer id);

  Optional<Object> findByTitle(@NotBlank final String title);

  void updateImage(@NotNull final Integer id, @NotNull final String url);

  void update(PredefinedCategory category);

}
