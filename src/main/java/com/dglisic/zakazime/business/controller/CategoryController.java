package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.service.CategoryService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/{businessId}/categories")
@Slf4j
@AllArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("{businessId}/categories")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> addUserDefinedCategoryToBusiness(@PathVariable @Valid @NotBlank Integer businessId,
                                                                          @RequestBody
                                                                          @Valid CreateUserDefinedCategoryRequest categoryRequest) {
    log.info("Saving category {} for business {}", categoryRequest, businessId);
    categoryService.createUserDefinedCategory(categoryRequest, businessId);
    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Category saved successfully"));
  }

  @PutMapping("{businessId}/categories/{categoryId}")
  @PreAuthorize("hasRole('SERVICE_PROVIDER')")
  public ResponseEntity<MessageResponse> updateUserDefinedCategory(@PathVariable @Valid @NotBlank final Integer businessId,
                                                                   @PathVariable @Valid @NotBlank final Integer categoryId,
                                                                   @RequestBody @Valid
                                                                   final UpdateUserDefinedCategoryRequest categoryRequest) {
    log.info("Updating category {} for business {}", categoryRequest, businessId);
    categoryService.updateUserDefinedCategory(businessId, categoryId, categoryRequest);
    return ResponseEntity.ok(new MessageResponse("Category updated successfully"));
  }

}
