package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;

public interface CategoryService {

  void createUserDefinedCategory(CreateUserDefinedCategoryRequest categoryRequest, Integer businessId);

  void updateUserDefinedCategory(Integer businessId, Integer categoryId, UpdateUserDefinedCategoryRequest categoryRequest);


}
