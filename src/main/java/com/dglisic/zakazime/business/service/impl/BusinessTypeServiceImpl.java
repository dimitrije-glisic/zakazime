package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessTypeServiceImpl implements BusinessTypeService {

  private final BusinessTypeRepository businessTypeRepository;

  @Override
  public List<BusinessType> getAll() {
    return businessTypeRepository.getAll();
  }

  @Override
  public BusinessType requireById(final Integer id) {
    return businessTypeRepository.findById(id).orElseThrow(() ->
        new ApplicationException("Business type with id " + id + " does not exist", HttpStatus.NOT_FOUND));
  }

  @Override
  public BusinessType create(final CreateBusinessTypeRequest createRequest) {
    validateOnCreate(createRequest);
    final BusinessType businessType = new BusinessType();
    businessType.setTitle(createRequest.title());
    return businessTypeRepository.create(businessType);
  }

  @Override
  public void update(final Integer id, final UpdateBusinessTypeRequest updateRequest) {
    final BusinessType inUpdate = validateOnUpdate(id);
    if (updateRequest.title().equalsIgnoreCase(inUpdate.getTitle())) {
      // nothing to update
      return;
    }
    inUpdate.setTitle(updateRequest.title());
    businessTypeRepository.update(inUpdate);
  }

  @Override
  public void delete(final Integer id) {
    validateOnDelete(id);
    businessTypeRepository.deleteById(id);
  }

  private void validateOnCreate(final CreateBusinessTypeRequest businessType) {
    if (businessTypeRepository.existsByTitle(businessType.title())) {
      throw new ApplicationException("Business type with name " + businessType.title() + " already exists",
          HttpStatus.BAD_REQUEST);
    }
  }

  private BusinessType validateOnUpdate(final Integer id) {
    return requireById(id);
  }

  private void validateOnDelete(final Integer id) {
    requireById(id);
    // todo: check if business type is used by any business and throw exception if it is
  }
}
