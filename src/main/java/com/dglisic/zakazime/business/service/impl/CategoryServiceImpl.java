package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.repository.UserDefinedCategoryRepository;
import com.dglisic.zakazime.business.service.CategoryService;
import com.dglisic.zakazime.common.ApplicationException;
import jooq.tables.pojos.UserDefinedCategory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


/**
 * Service for managing user defined categories (business specific).
 * This category is meant for categorizing services and products within a business.
 * It is not meant for system-wide categories / search tags.
 */
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final BusinessValidator businessValidator;
    private final UserDefinedCategoryRepository userDefinedCategoryRepository;

  @Override
  public void createUserDefinedCategory(CreateUserDefinedCategoryRequest categoryRequest, Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireUserPermittedToChangeBusiness(businessId);
    final UserDefinedCategory category = new UserDefinedCategory()
        .setTitle(categoryRequest.title())
        .setBusinessId(businessId);
    userDefinedCategoryRepository.createUserDefinedCategory(category);
  }

  @Override
  public void updateUserDefinedCategory(Integer businessId, Integer categoryId,
                                        UpdateUserDefinedCategoryRequest categoryRequest) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireUserPermittedToChangeBusiness(businessId);
    final UserDefinedCategory category = requireUserDefinedCategory(categoryId);

    if (!category.getBusinessId().equals(businessId)) {
      throw new ApplicationException("Category with id " + categoryId + " does not belong to business with id " + businessId,
          HttpStatus.BAD_REQUEST);
    }

    if (category.getTitle().equals(categoryRequest.title())) {
      return;
      // no change
    }
    category.setTitle(categoryRequest.title());
    userDefinedCategoryRepository.update(category);
  }

  private UserDefinedCategory requireUserDefinedCategory(Integer categoryId) {
    return userDefinedCategoryRepository.findUserDefinedCategoryById(categoryId)
        .orElseThrow(
            () -> new ApplicationException("Category with id " + categoryId + " does not exist", HttpStatus.BAD_REQUEST));
  }


}
