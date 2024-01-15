package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.ServiceMapper;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.repository.ServiceSubcategoryRepository;
import com.dglisic.zakazime.business.service.ServiceTemplateService;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceTemplateServiceImpl implements ServiceTemplateService {

  private final ServiceRepository serviceRepository;
  private final ServiceSubcategoryRepository serviceSubcategoryRepository;
  private final ServiceMapper serviceMapper;

  @Override
  public List<Service> searchServiceTemplates(@Nullable final String businessType, @Nullable final String category,
                                              @Nullable final String subcategory) {
    return serviceRepository.searchServiceTemplates(businessType, category, subcategory);
  }

  @Override
  public Service getServiceTemplate(final Integer id) {
    return serviceRepository.findServiceById(id)
        .filter(Service::getTemplate)
        .orElseThrow(() -> new ApplicationException("Service template with id " + id + " does not exist", HttpStatus.NOT_FOUND));
  }

  @Override
  public Service createServiceTemplate(final CreateServiceRequest createServiceRequest) {
    validateCreateRequest(createServiceRequest);
    final Service service = serviceMapper.map(createServiceRequest);
    service.setTemplate(true);
    return serviceRepository.create(service);
  }

  @Override
  public void updateServiceTemplate(final Integer id, final UpdateServiceRequest updateServiceRequest) {
    final Service existing = requireServiceTemplateExists(id);
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
    requireSubcategoryExists(createServiceRequest.subcategoryId());
  }

  private void validateUpdateRequest(final Service existing, final UpdateServiceRequest updateServiceRequest) {
    requireUniqueTitleWithAllowedOwner(updateServiceRequest.title(), existing.getId());
    requireSubcategoryExists(updateServiceRequest.subcategoryId());
  }

  private void validateOnDelete(final Integer serviceId) {
    requireServiceTemplateExists(serviceId);
  }

  private void requireUniqueTitle(final String title) {
    serviceRepository.findByTitle(title).ifPresent(
        service -> {
          throw new ApplicationException("Service template with title [" + title + "] already exists",
              HttpStatus.BAD_REQUEST);
        });
  }

  private void requireUniqueTitleWithAllowedOwner(final String title, final Integer id) {
    serviceRepository.findByTitle(title).ifPresent(
        service -> {
          if (!service.getId().equals(id)) {
            throw new ApplicationException("Service template with title [" + title + "] already exists",
                HttpStatus.BAD_REQUEST);
          }
        });
  }

  private void requireSubcategoryExists(final Integer integer) {
    serviceSubcategoryRepository.findById(integer).orElseThrow(
        () -> new ApplicationException("Service subcategory with id [" + integer + "] does not exist",
            HttpStatus.BAD_REQUEST));
  }

  private Service requireServiceTemplateExists(final Integer serviceId) {
    final Optional<Service> serviceById = serviceRepository.findServiceById(serviceId);
    if (serviceById.isEmpty() || !serviceById.get().getTemplate()) {
      throw new ApplicationException("Service template with id [" + serviceId + "] does not exist",
          HttpStatus.BAD_REQUEST);
    }
    return serviceById.get();
  }
}
