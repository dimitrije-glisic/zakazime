package com.dglisic.zakazime.business.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.dglisic.zakazime.business.controller.dto.CreatePredefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.ImageUploadResponse;
import com.dglisic.zakazime.business.controller.dto.UpdatePredefinedCategoryRequest;
import com.dglisic.zakazime.business.service.PredefinedCategoryService;
import com.dglisic.zakazime.common.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.PredefinedCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Slf4j
public class PredefinedCategoryController {

  private final PredefinedCategoryService predefinedCategoryService;
  private final ObjectMapper objectMapper;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PredefinedCategory> create(
      @RequestBody @Valid final CreatePredefinedCategoryRequest createPredefinedCategoryRequest) {
    log.info("Creating predefined category: {}", createPredefinedCategoryRequest);
    return ResponseEntity.ok(predefinedCategoryService.create(createPredefinedCategoryRequest));
  }

  @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PredefinedCategory> createWithImage(
      @RequestPart("category") final String categoryJson,
      @RequestPart("image") final MultipartFile image) throws IOException {
    log.info("Creating predefined category with image: {}", categoryJson);
    final CreatePredefinedCategoryRequest createRequest =
        objectMapper.readValue(categoryJson, CreatePredefinedCategoryRequest.class);
    return ResponseEntity.status(CREATED).body(predefinedCategoryService.createWithImage(createRequest, image));
  }

  @PostMapping(value = "/{id}/upload-image", consumes = {"multipart/form-data"})
  @PreAuthorize("hasRole('ADMIN')")
  public ImageUploadResponse uploadImage(@PathVariable final Integer id, @RequestParam("image") final MultipartFile file) {
    log.info("Uploading image for predefined category with id: {}", id);
    final String url = predefinedCategoryService.uploadImage(id, file);
    return new ImageUploadResponse(url);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PredefinedCategory> get(@PathVariable final Integer id) {
    return ResponseEntity.ok(predefinedCategoryService.requireById(id));
  }

  @GetMapping("/{id}/image")
  public byte[] getImage(@PathVariable final Integer id) {
    return predefinedCategoryService.getImage(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> update(@PathVariable final Integer id,
                                                @RequestBody final UpdatePredefinedCategoryRequest updateRequest) {
    log.info("Updating predefined category with id: {} with request: {}", id, updateRequest);
    predefinedCategoryService.update(id, updateRequest);
    return ResponseEntity.ok(new MessageResponse("Service category updated successfully"));
  }

  @PutMapping(value = "/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> updateWithImage(
      @RequestPart("category") final String categoryJson,
      @RequestPart("image") final MultipartFile image,
      @PathVariable final Integer id) throws IOException {
    log.info("Updating predefined category with id: {} with request: {}", id, categoryJson);
    final UpdatePredefinedCategoryRequest updateRequest =
        objectMapper.readValue(categoryJson, UpdatePredefinedCategoryRequest.class);
    predefinedCategoryService.update(id, updateRequest, image);
    return ResponseEntity.ok(new MessageResponse("Service category updated successfully"));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> delete(@PathVariable @NotNull final Integer id) {
    predefinedCategoryService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<PredefinedCategory>> getAll() {
    return ResponseEntity.ok(predefinedCategoryService.getAll());
  }
}