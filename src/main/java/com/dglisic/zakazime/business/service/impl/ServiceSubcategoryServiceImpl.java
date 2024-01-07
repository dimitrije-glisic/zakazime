package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceSubcategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceSubcategoryRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.repository.ServiceSubcategoryRepository;
import com.dglisic.zakazime.business.service.ServiceSubcategoryService;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import jooq.tables.pojos.ServiceSubcategory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

// TODO: all the methods can be executed only by admin - not all only create, update and delete
@Service
@RequiredArgsConstructor
public class ServiceSubcategoryServiceImpl implements ServiceSubcategoryService {

  private final ServiceSubcategoryRepository serviceSubcategoryRepository;
  private final BusinessTypeRepository businessTypeRepository;

  @Override
  public ServiceSubcategory save(final CreateServiceSubcategoryRequest createRequest) {
    validateOnSave(createRequest);
    final ServiceSubcategory serviceCategory = new ServiceSubcategory();
    serviceCategory.setTitle(createRequest.title());
    serviceCategory.setServiceCategoryId(createRequest.serviceCategoryId());
    return serviceSubcategoryRepository.store(serviceCategory);
  }

  @Override
  public ServiceSubcategory findById(final Integer id) {
    return serviceSubcategoryRepository.findById(id).orElseThrow(
        () -> new ApplicationException("Service subcategory with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
  }

  @Override
  public ServiceSubcategory update(final UpdateServiceSubcategoryRequest updateRequest, final Integer subcategoryId) {
    validateOnUpdate(updateRequest, subcategoryId);
    final ServiceSubcategory serviceCategory = new ServiceSubcategory();
    serviceCategory.setId(subcategoryId);
    serviceCategory.setTitle(updateRequest.title());
    serviceCategory.setServiceCategoryId(updateRequest.serviceCategoryId());
    return serviceSubcategoryRepository.store(serviceCategory);
  }

  @Override
  public void delete(final Integer id) {
    serviceSubcategoryRepository.delete(id);
  }

  @Override
  public List<ServiceSubcategory> getAll() {
    return serviceSubcategoryRepository.getAll();
  }

  private void validateOnSave(final CreateServiceSubcategoryRequest createRequest) {
    requireUniqueSubcategoryTitle(createRequest.title());
    requireServiceCategoryExists(createRequest.serviceCategoryId());
  }

  private void validateOnUpdate(final UpdateServiceSubcategoryRequest updateRequest, final Integer subcategoryId) {
    requireSubcategoryExists(subcategoryId);
    if (StringUtils.isNotBlank(updateRequest.title())) {
      requireUniqueSubcategoryTitle(updateRequest.title());
    }
    if (updateRequest.serviceCategoryId() != null) {
      requireServiceCategoryExists(updateRequest.serviceCategoryId());
    }
  }

  private void requireServiceCategoryExists(final Integer categoryId) {
    businessTypeRepository.findById(categoryId).orElseThrow(
        () -> new ApplicationException("Service Category with id " + categoryId + " does not exist.", HttpStatus.BAD_REQUEST));
  }

  private void requireUniqueSubcategoryTitle(final String title) {
    serviceSubcategoryRepository.findByTitle(title).ifPresent(serviceCategory -> {
      throw new ApplicationException("Service subcategory with title " + title + " already exists.", HttpStatus.BAD_REQUEST);
    });
  }

  private void requireSubcategoryExists(final Integer subcategoryId) {
    serviceSubcategoryRepository.findById(subcategoryId).orElseThrow(
        () -> new ApplicationException("Service subcategory with id " + subcategoryId + " does not exist.",
            HttpStatus.BAD_REQUEST));
  }

}
