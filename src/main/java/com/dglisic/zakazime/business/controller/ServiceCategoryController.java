package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import com.dglisic.zakazime.business.service.ServiceCategoryService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-category")
@RequiredArgsConstructor
public class ServiceCategoryController {

  private final ServiceCategoryService serviceCategoryService;


  @PostMapping
  public ResponseEntity<ServiceCategory> create(
      @RequestBody @Valid final CreateServiceCategoryRequest createServiceCategoryRequest) {
    return ResponseEntity.ok(serviceCategoryService.save(createServiceCategoryRequest));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceCategory> get(@PathVariable final Integer id) {
    return ResponseEntity.ok(serviceCategoryService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ServiceCategory> update(@RequestBody final UpdateServiceCategoryRequest updateServiceCategoryRequest,
                                                @PathVariable final Integer id) {
    return ResponseEntity.ok(serviceCategoryService.update(updateServiceCategoryRequest, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponse> delete(@PathVariable @NotNull final Integer id) {
    serviceCategoryService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<ServiceCategory>> getAll() {
    return ResponseEntity.ok(serviceCategoryService.getAll());
  }
}