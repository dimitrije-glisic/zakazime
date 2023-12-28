package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.repository.ServiceCategoryRepository;
import com.dglisic.zakazime.business.service.ServiceCategoryService;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import jooq.tables.pojos.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

// TODO: all the methods can be executed only by admin
@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

  private final ServiceCategoryRepository serviceCategoryRepository;
  private final BusinessTypeRepository businessTypeRepository;

  @Override
  public ServiceCategory save(final CreateServiceCategoryRequest createServiceCategoryRequest) {
    validateOnSave(createServiceCategoryRequest);
    final ServiceCategory serviceCategory = new ServiceCategory();
    serviceCategory.setTitle(createServiceCategoryRequest.title());
    serviceCategory.setBusinessTypeId(createServiceCategoryRequest.businessTypeId());
    return serviceCategoryRepository.store(serviceCategory);
  }

  @Override
  public ServiceCategory findById(final Integer id) {
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

}
