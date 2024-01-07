package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateServiceSubcategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceSubcategoryRequest;
import com.dglisic.zakazime.business.service.ServiceSubcategoryService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.ServiceSubcategory;
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
@RequestMapping("/service-subcategory")
@RequiredArgsConstructor
public class ServiceSubcategoryController {

  private final ServiceSubcategoryService serviceSubcategoryService;


  @PostMapping
  public ResponseEntity<ServiceSubcategory> create(
      @RequestBody @Valid final CreateServiceSubcategoryRequest createRequest) {
    return ResponseEntity.ok(serviceSubcategoryService.save(createRequest));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceSubcategory> get(@PathVariable final Integer id) {
    return ResponseEntity.ok(serviceSubcategoryService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ServiceSubcategory> update(@RequestBody final UpdateServiceSubcategoryRequest updateRequest,
                                                   @PathVariable final Integer id) {
    return ResponseEntity.ok(serviceSubcategoryService.update(updateRequest, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponse> delete(@PathVariable @NotNull final Integer id) {
    serviceSubcategoryService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<ServiceSubcategory>> getAll() {
    return ResponseEntity.ok(serviceSubcategoryService.getAll());
  }
}