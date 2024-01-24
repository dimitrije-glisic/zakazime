package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.repository.PredefinedCategoryRepository;
import com.dglisic.zakazime.business.service.ImageStorage;
import com.dglisic.zakazime.business.service.PredefinedCategoryService;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.PredefinedCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredefinedCategoryServiceImpl implements PredefinedCategoryService {

  private final PredefinedCategoryRepository predefinedCategoryRepository;
  private final BusinessTypeRepository businessTypeRepository;
  private final ImageStorage imageStorage;

  @Override
  public PredefinedCategory create(final CreateServiceCategoryRequest createServiceCategoryRequest) {
    validateOnSave(createServiceCategoryRequest);
    PredefinedCategory toBeCreated = fromRequest(createServiceCategoryRequest);
    return predefinedCategoryRepository.store(toBeCreated);
  }

  @Override
  @Transactional
  public PredefinedCategory createWithImage(final CreateServiceCategoryRequest createRequest, final MultipartFile image) {
    validateOnSave(createRequest);
    final PredefinedCategory toBeCreated = fromRequest(createRequest);
    final PredefinedCategory newCategory = predefinedCategoryRepository.store(toBeCreated);
    final String url = makeUrl(newCategory.getId(), image);
    predefinedCategoryRepository.updateImage(newCategory.getId(), url);
    storeImage(url, image);
    newCategory.setImageUrl(url);
    return newCategory;
  }

  @Override
  public PredefinedCategory requireById(final Integer id) {
    return predefinedCategoryRepository.findById(id).orElseThrow(
        () -> new ApplicationException("Service category with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
  }

  @Override
  public void update(final UpdateServiceCategoryRequest updateServiceCategoryRequest, final Integer categoryId) {
    validateOnUpdate(updateServiceCategoryRequest, categoryId);
    final PredefinedCategory category = new PredefinedCategory();
    category.setId(categoryId);
    category.setTitle(updateServiceCategoryRequest.title());
    category.setBusinessTypeId(updateServiceCategoryRequest.businessTypeId());
    predefinedCategoryRepository.update(category);
  }

  @Override
  public void delete(final Integer id) {
    predefinedCategoryRepository.delete(id);
  }

  @Override
  public List<PredefinedCategory> getAll() {
    return predefinedCategoryRepository.getAll();
  }

  @Override
  public byte[] getImage(Integer id) {
    final PredefinedCategory category = requireById(id);
    if (category.getImageUrl() == null) {
      throw new ApplicationException("Category id " + id + " does not have an image",
          HttpStatus.NOT_FOUND);
    }
    try {
      return imageStorage.getImage(category.getImageUrl());
    } catch (IOException e) {
      log.error("Failed to read image", e);
      throw new ApplicationException("Failed to read image", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public String uploadImage(Integer id, MultipartFile file) {
    final String url = makeUrl(id, file);
    storeImage(url, file);
    predefinedCategoryRepository.updateImage(id, url);
    return url;
  }

  private void validateOnSave(final CreateServiceCategoryRequest createServiceCategoryRequest) {
    requireUniqueCategoryTitle(createServiceCategoryRequest.title());
    requireBusinessTypeExists(createServiceCategoryRequest.businessTypeId());
  }

  private PredefinedCategory fromRequest(CreateServiceCategoryRequest request) {
    PredefinedCategory category = new PredefinedCategory();
    category.setTitle(request.title());
    final String slug = request.title().toLowerCase().replace(" ", "-");
    category.setSlug(slug);
    category.setBusinessTypeId(request.businessTypeId());
    return category;
  }

  private void validateOnUpdate(final UpdateServiceCategoryRequest updateServiceCategoryRequest, final Integer categoryId) {
    requireCategoryExists(categoryId);
  }

  private void requireBusinessTypeExists(final Integer businessTypeId) {
    businessTypeRepository.findById(businessTypeId).orElseThrow(
        () -> new ApplicationException("Business type with id " + businessTypeId + " does not exist.", HttpStatus.BAD_REQUEST));
  }

  private void requireUniqueCategoryTitle(String title) {
    predefinedCategoryRepository.findByTitle(title).ifPresent(category -> {
      throw new ApplicationException("Service category with title " + title + " already exists.", HttpStatus.BAD_REQUEST);
    });
  }

  private void requireCategoryExists(final Integer categoryId) {
    predefinedCategoryRepository.findById(categoryId).orElseThrow(
        () -> new ApplicationException("Service category with id " + categoryId + " does not exist.", HttpStatus.BAD_REQUEST));
  }

  private @NotNull String makeUrl(final Integer id, final MultipartFile file) {
    final String idPartOfPath = String.format("id_%d", id);
    return "categories" + "/" + idPartOfPath + "/" + file.getOriginalFilename();
  }

  private void storeImage(final String url, final MultipartFile file) {
    try {
      imageStorage.storeImage(url, file);
    } catch (IOException e) {
      log.error("Failed to store image", e);
      throw new ApplicationException("Failed to store image", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
