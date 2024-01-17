package com.dglisic.zakazime.business.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.ImageUploadResponse;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("business-types")
@RequiredArgsConstructor
public class BusinessTypeController {
  private static final Logger logger = getLogger(BusinessTypeController.class);

  private final BusinessTypeService businessTypeService;

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

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse updateBusinessType(@PathVariable final Integer id,
                                            @RequestBody final UpdateBusinessTypeRequest businessType) {
    logger.info("Updating business type with id {} to {}", id, businessType);
    businessTypeService.update(id, businessType);
    return new MessageResponse("Business type updated successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public MessageResponse deleteBusinessType(@PathVariable final Integer id) {
    logger.info("Deleting business type with id {}", id);
    businessTypeService.delete(id);
    return new MessageResponse("Business type deleted successfully");
  }

  final static String UPLOAD_DIRECTORY = "C:\\Users\\dglisic\\personal-projects\\storage\\images\\";

  @PostMapping(value = "/upload-image", consumes = {"multipart/form-data"}, produces = {"application/json"})
  public ImageUploadResponse uploadImage(@RequestParam("image") final MultipartFile file) throws IOException {
    final Path directoryPath = Paths.get(UPLOAD_DIRECTORY, "business-types");
    Files.createDirectories(directoryPath);
    final Path fileNameAndPath = directoryPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
    logger.info("Saving image to: {}", fileNameAndPath);
    Files.write(fileNameAndPath, file.getBytes(), StandardOpenOption.CREATE);
    return new ImageUploadResponse("business-types/" + file.getOriginalFilename());
  }

}
