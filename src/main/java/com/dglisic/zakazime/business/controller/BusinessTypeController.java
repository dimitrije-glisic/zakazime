package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.ImageUploadResponse;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import com.dglisic.zakazime.common.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
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
@RequestMapping("business-types")
@RequiredArgsConstructor
public class BusinessTypeController {
  private static final Logger logger = getLogger(BusinessTypeController.class);

  private final BusinessTypeService businessTypeService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public List<BusinessType> getBusinessTypes() {
    logger.info("Getting business types");
    return businessTypeService.getAll();
  }

  @GetMapping("/{id}")
  public BusinessType getBusinessTypeById(@PathVariable final Integer id) {
    logger.info("Getting business type with id {}", id);
    return businessTypeService.requireById(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public BusinessType createBusinessType(@Valid @RequestBody final CreateBusinessTypeRequest createRequest) {
    logger.info("Creating business type {}", createRequest);
    return businessTypeService.create(createRequest);
  }

  @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public BusinessType createBusinessTypeWithImage(
      @RequestPart("businessType") String businessTypeJson,
      @RequestPart("image") MultipartFile image) throws IOException {
    final CreateBusinessTypeRequest createRequest = objectMapper.readValue(businessTypeJson, CreateBusinessTypeRequest.class);
    return businessTypeService.createWithFile(createRequest, image);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse updateBusinessType(@PathVariable final Integer id,
                                            @RequestBody final UpdateBusinessTypeRequest businessType) {
    logger.info("Updating business type with id {} to {}", id, businessType);
    businessTypeService.update(id, businessType);
    return new MessageResponse("Business type updated successfully");
  }

  @PutMapping(value = "/{id}/with-image", consumes = {"multipart/form-data", "application/json"})
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse updateBusinessType(@PathVariable final Integer id,
                                            @RequestBody final UpdateBusinessTypeRequest businessType,
                                            @RequestParam("image") final MultipartFile file) throws IOException {
    logger.info("Updating business type with id {} to {}", id, businessType);
    businessTypeService.update(id, businessType, file);
    return new MessageResponse("Business type updated successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse deleteBusinessType(@PathVariable final Integer id) {
    logger.info("Deleting business type with id {}", id);
    businessTypeService.delete(id);
    return new MessageResponse("Business type deleted successfully");
  }

  @PostMapping(value = "/{id}/upload-image", consumes = {"multipart/form-data"})
  @PreAuthorize("hasRole('ADMIN')")
  public ImageUploadResponse uploadImage(@PathVariable final Integer id, @RequestParam("image") final MultipartFile file)
      throws IOException {
    final String url = businessTypeService.uploadImage(id, file);
    return new ImageUploadResponse(url);
  }

  @GetMapping("/{id}/image")
  public byte[] getImage(@PathVariable final Integer id) throws IOException {
    return businessTypeService.getImage(id);
  }

}
