package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.repository.ServiceCategoryRepository;
import com.dglisic.zakazime.business.service.ImageStorage;
import com.dglisic.zakazime.business.service.ServiceCategoryService;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

  private final ServiceCategoryRepository serviceCategoryRepository;
  private final BusinessTypeRepository businessTypeRepository;
  private final ImageStorage imageStorage;

  @Override
  public ServiceCategory create(final CreateServiceCategoryRequest createServiceCategoryRequest) {
    validateOnSave(createServiceCategoryRequest);
    final ServiceCategory serviceCategory = new ServiceCategory();
    serviceCategory.setTitle(createServiceCategoryRequest.title());
    serviceCategory.setBusinessTypeId(createServiceCategoryRequest.businessTypeId());
    return serviceCategoryRepository.store(serviceCategory);
  }

  @Override
  @Transactional
  public ServiceCategory createWithImage(final CreateServiceCategoryRequest createRequest, final MultipartFile image) {
    validateOnSave(createRequest);
    final ServiceCategory toBeCreated = new ServiceCategory();
    toBeCreated.setTitle(createRequest.title());
    toBeCreated.setBusinessTypeId(createRequest.businessTypeId());
    final ServiceCategory newCategory = serviceCategoryRepository.store(toBeCreated);
    final String url = makeUrl(newCategory.getId(), image);
    storeImage(url, image);
    serviceCategoryRepository.updateImage(newCategory.getId(), url);
    newCategory.setImageUrl(url);
    return newCategory;
  }

  @Override
  public ServiceCategory requireById(final Integer id) {
    return serviceCategoryRepository.findById(id).orElseThrow(
        () -> new ApplicationException("Service category with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
  }

  @Override
  public ServiceCategory update(final UpdateServiceCategoryRequest updateServiceCategoryRequest, final Integer categoryId) {
    validateOnUpdate(updateServiceCategoryRequest, categoryId);
    final ServiceCategory serviceCategory = new ServiceCategory();
    serviceCategory.setId(categoryId);
    serviceCategory.setTitle(updateServiceCategoryRequest.title());
    serviceCategory.setBusinessTypeId(updateServiceCategoryRequest.businessTypeId());
    return serviceCategoryRepository.store(serviceCategory);
  }

  @Override
  public void delete(final Integer id) {
    serviceCategoryRepository.delete(id);
  }

  @Override
  public List<ServiceCategory> getAll() {
    return serviceCategoryRepository.getAll();
  }

  @Override
  public byte[] getImage(Integer id) {
    final ServiceCategory category = requireById(id);
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

  private void validateOnSave(final CreateServiceCategoryRequest createServiceCategoryRequest) {
    requireUniqueCategoryTitle(createServiceCategoryRequest.title());
    requireBusinessTypeExists(createServiceCategoryRequest.businessTypeId());
  }

  private void validateOnUpdate(final UpdateServiceCategoryRequest updateServiceCategoryRequest, final Integer categoryId) {
    requireCategoryExists(categoryId);
  }

  private void requireBusinessTypeExists(final Integer businessTypeId) {
    businessTypeRepository.findById(businessTypeId).orElseThrow(
        () -> new ApplicationException("Business type with id " + businessTypeId + " does not exist.", HttpStatus.BAD_REQUEST));
  }

  private void requireUniqueCategoryTitle(String title) {
    serviceCategoryRepository.findByTitle(title).ifPresent(serviceCategory -> {
      throw new ApplicationException("Service category with title " + title + " already exists.", HttpStatus.BAD_REQUEST);
    });
  }

  private void requireCategoryExists(final Integer categoryId) {
    serviceCategoryRepository.findById(categoryId).orElseThrow(
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
