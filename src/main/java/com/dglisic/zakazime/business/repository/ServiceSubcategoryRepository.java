package com.dglisic.zakazime.business.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public interface ServiceSubcategoryRepository {

  boolean allExist(@NotNull final Set<Integer> subCategoryIds);

  boolean exists(@NotNull final Integer integer);

}
