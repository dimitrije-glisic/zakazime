package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.ServiceMapper;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.repository.ServiceRepository;
import com.dglisic.zakazime.business.repository.UserDefinedCategoryRepository;
import com.dglisic.zakazime.business.service.ServiceManagement;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jooq.tables.pojos.Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class ServiceManagementImpl implements ServiceManagement {

  private final ServiceRepository serviceRepository;
  private final UserDefinedCategoryRepository userDefinedCategoryRepository;
  private final ServiceMapper serviceMapper;
  private final BusinessValidator businessValidator;

  @Override
  public Service addServiceToBusiness(final CreateServiceRequest serviceRequest, final Integer businessId) {
    validateOnSaveService(serviceRequest, businessId);
    final Service serviceToBeSaved = fromRequest(serviceRequest);
    return serviceRepository.create(serviceToBeSaved);
  }

  @Override
  @Transactional
  public List<Service> addServicesToBusiness(final List<CreateServiceRequest> createServiceRequestList,
                                             final Integer businessId) {
    validateOnSaveServices(createServiceRequestList, businessId);
    final List<Service> servicesToBeSaved = fromRequest(createServiceRequestList, businessId);
    return serviceRepository.saveServices(servicesToBeSaved);
  }

  @Override
  public void updateService(final int businessId, final int serviceId, final UpdateServiceRequest updateServiceRequest) {
    validateOnUpdateService(serviceId, businessId, updateServiceRequest);
    final Service service = serviceMapper.map(updateServiceRequest);
    service.setId(serviceId);
    serviceRepository.update(service);
  }

  @Override
  public Service getServiceById(Integer serviceId) {
    final Optional<Service> service = serviceRepository.findServiceById(serviceId);
    if (service.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    }
    return service.get();
  }

  @Override
  public void deleteService(Integer businessId, Integer serviceId) {
    validateOnDeleteService(businessId, serviceId);
    serviceRepository.delete(serviceId);
  }

  private List<Service> fromRequest(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    return createServiceRequestList.stream()
        .map(this::fromRequest)
        .toList();
  }

  private Service fromRequest(final CreateServiceRequest req) {
    return serviceMapper.map(req);
  }

  private void validateOnSaveServices(final List<CreateServiceRequest> createServiceRequestList, int businessId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    requireAllCategoriesExist(createServiceRequestList);
    requireAllTitlesUnique(createServiceRequestList, businessId);
  }

  private void validateOnSaveService(final CreateServiceRequest serviceRequest, final Integer businessId) {
    businessValidator.requireBusinessExists(businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
    requireCategoryExists(serviceRequest.categoryId());
    requireUniqueServiceTitle(serviceRequest.title(), businessId);
  }

  private void validateOnUpdateService(final Integer serviceId, final Integer businessId,
                                       final UpdateServiceRequest changeServiceRequest) {
    businessValidator.requireBusinessExists(businessId);
    requireCategoryExists(changeServiceRequest.categoryId());
    requireServiceExists(serviceId);
    businessValidator.requireServiceBelongsToBusiness(serviceId, businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
  }

  private void validateOnDeleteService(Integer businessId, Integer serviceId) {
    businessValidator.requireBusinessExists(businessId);
    requireServiceExists(serviceId);
    businessValidator.requireServiceBelongsToBusiness(serviceId, businessId);
    businessValidator.requireCurrentUserPermittedToChangeBusiness(businessId);
  }

  private void requireAllCategoriesExist(List<CreateServiceRequest> createServiceRequestList) {
    for (CreateServiceRequest createServiceRequest : createServiceRequestList) {
      requireCategoryExists(createServiceRequest.categoryId());
    }
  }

  private void requireAllTitlesUnique(List<CreateServiceRequest> createServiceRequestList, int businessId) {
    final var incomingTitles = createServiceRequestList.stream()
        .map(CreateServiceRequest::title)
        .collect(Collectors.toSet());

    boolean incomingTitlesAreUnique = incomingTitles.size() == createServiceRequestList.size();

    if (!incomingTitlesAreUnique) {
      throw new ApplicationException("Service titles must be unique", HttpStatus.BAD_REQUEST);
    }

    for (String title : incomingTitles) {
      requireUniqueServiceTitle(title, businessId);
    }
  }

  private void requireUniqueServiceTitle(String title, Integer businessId) {
    final boolean exists = serviceRepository.existsByTitleAndBusinessId(title, businessId);
    if (exists) {
      throw new ApplicationException("Service with title " + title + " already exists, nothing was saved",
          HttpStatus.BAD_REQUEST);
    }
  }

  private void requireServiceExists(final Integer serviceId) {
    final Optional<Service> serviceFromDb = serviceRepository.findServiceById(serviceId);
    if (serviceFromDb.isEmpty()) {
      throw new ApplicationException("Service with id " + serviceId + " does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  private void requireCategoryExists(final Integer categoryId) {
    final boolean categoryExists = userDefinedCategoryRepository.exists(categoryId);
    if (!categoryExists) {
      throw new ApplicationException("Category with id" + categoryId + "not exists", HttpStatus.BAD_REQUEST);
    }
  }


}
