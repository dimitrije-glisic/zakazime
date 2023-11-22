package com.dglisic.zakazime.business.repository;

import com.dglisic.zakazime.business.domain.Category;
import java.util.Optional;

public interface CategoryRepository {

  Optional<Category> findCategory(String categoryName);
}
