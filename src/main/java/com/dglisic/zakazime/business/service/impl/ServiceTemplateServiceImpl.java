package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.ServiceMapper;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.repository.UserDefinedCategoryRepository;
import com.dglisic.zakazime.business.service.ServiceTemplateService;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.annotation.Nullable;
import java.util.List;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceTemplateServiceImpl implements ServiceTemplateService {

  private final ServiceRepository serviceRepository;
  //  private final ServiceSubcategoryRepository serviceSubcategoryRepository;
  private final UserDefinedCategoryRepository userDefinedCategoryRepository;
  private final ServiceMapper serviceMapper;

  @Override
  public Service getService(final Integer id) {
    return serviceRepository.findServiceById(id)
        .orElseThrow(() -> new ApplicationException("Service template with id " + id + " does not exist", HttpStatus.NOT_FOUND));
  }

  @Override
  public Service createService(final CreateServiceRequest createServiceRequest) {
    validateCreateRequest(createServiceRequest);
    final Service service = serviceMapper.map(createServiceRequest);
    return serviceRepository.create(service);
  }

  @Override
  public void updateServiceTemplate(final Integer id, final UpdateServiceRequest updateServiceRequest) {
    final Service existing = requireServiceExists(id);
    validateUpdateRequest(existing, updateServiceRequest);
    final Service request = serviceMapper.map(updateServiceRequest);
    if (existing.equals(request)) {
      return;
    }
    request.setId(id);
    serviceRepository.updateServiceTemplate(request);
  }

  @Override
  public void deleteServiceTemplate(final Integer id) {
    validateOnDelete(id);
    serviceRepository.deleteServiceTemplate(id);
  }

  private void validateCreateRequest(final CreateServiceRequest createServiceRequest) {
    requireUniqueTitle(createServiceRequest.title());
    requireCategoryExists(createServiceRequest.subcategoryId());
  }

  private void validateUpdateRequest(final Service existing, final UpdateServiceRequest updateServiceRequest) {
    requireUniqueTitleWithAllowedOwner(updateServiceRequest.title(), existing.getId());
    requireCategoryExists(updateServiceRequest.subcategoryId());
  }

  private void validateOnDelete(final Integer serviceId) {
    requireServiceExists(serviceId);
  }

  private void requireUniqueTitle(final String title) {
    serviceRepository.findByTitle(title).ifPresent(
        service -> {
          throw new ApplicationException("Service template with title [" + title + "] already exists",
              HttpStatus.BAD_REQUEST);
        });
  }

  private void requireUniqueTitleWithAllowedOwner(final String title, final Integer id) {
    serviceRepository.findByTitle(title)
        .filter(service -> !service.getId().equals(id))
        .ifPresent(
            service -> {
              throw new ApplicationException("Service template with title [" + title + "] already exists",
                  HttpStatus.BAD_REQUEST);
            });
  }

  private void requireCategoryExists(final Integer integer) {
    userDefinedCategoryRepository.findById(integer).orElseThrow(() ->
        new ApplicationException("Service subcategory with id [" + integer + "] does not exist",
            HttpStatus.BAD_REQUEST));
  }

  private Service requireServiceExists(final Integer serviceId) {
    return serviceRepository.findServiceById(serviceId).orElseThrow(() ->
        new ApplicationException("Service template with id [" + serviceId + "] does not exist",
            HttpStatus.BAD_REQUEST));
  }
}
