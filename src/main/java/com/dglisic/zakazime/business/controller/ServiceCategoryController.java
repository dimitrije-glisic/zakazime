package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import com.dglisic.zakazime.business.service.ServiceCategoryService;
import com.dglisic.zakazime.common.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/service-category")
@RequiredArgsConstructor
public class ServiceCategoryController {

  private final ServiceCategoryService serviceCategoryService;
  private final ObjectMapper objectMapper;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ServiceCategory> create(
      @RequestBody @Valid final CreateServiceCategoryRequest createServiceCategoryRequest) {
    return ResponseEntity.ok(serviceCategoryService.create(createServiceCategoryRequest));
  }

  @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ServiceCategory createWithImage(
      @RequestPart("category") final String categoryJson,
      @RequestPart("image") final MultipartFile image) throws IOException {
    final CreateServiceCategoryRequest createRequest = objectMapper.readValue(categoryJson, CreateServiceCategoryRequest.class);
    return serviceCategoryService.createWithImage(createRequest, image);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceCategory> get(@PathVariable final Integer id) {
    return ResponseEntity.ok(serviceCategoryService.requireById(id));
  }

  @GetMapping("/{id}/image")
  public byte[] getImage(@PathVariable final Integer id) {
    return serviceCategoryService.getImage(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ServiceCategory> update(@RequestBody final UpdateServiceCategoryRequest updateServiceCategoryRequest,
                                                @PathVariable final Integer id) {
    return ResponseEntity.ok(serviceCategoryService.update(updateServiceCategoryRequest, id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> delete(@PathVariable @NotNull final Integer id) {
    serviceCategoryService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<ServiceCategory>> getAll() {
    return ResponseEntity.ok(serviceCategoryService.getAll());
  }
}