package com.dglisic.zakazime.admin.service.impl;

import com.dglisic.zakazime.admin.repository.BusinessTypeRepository;
import com.dglisic.zakazime.admin.service.BusinessTypeService;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.common.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessTypeServiceImpl implements BusinessTypeService {

  private final BusinessTypeRepository businessTypeRepository;

  @Override
  public List<BusinessType> getAllBusinessTypes() {
    return businessTypeRepository.getAllBusinessTypes();
  }

  @Override
  public BusinessType getBusinessTypeById(int id) {
    return businessTypeRepository.findBusinessTypeById(id).orElseThrow(
        () -> new ApplicationException("Business type with id " + id + " does not exist", HttpStatus.NOT_FOUND)
    );
  }

  @Override
  public BusinessType createBusinessType(BusinessType request) {
    validateOnCreate(request);
    return businessTypeRepository.save(request);
  }

  @Override
  public void updateBusinessType(BusinessType request) {
    validateOnUpdate(request);
    businessTypeRepository.update(request);
  }

  @Override
  public void deleteBusinessType(int businessTypeId) {
    businessTypeRepository.delete(businessTypeId);
  }

  private void validateOnCreate(BusinessType request) {
    //todo: check if business type with same name already exists
  }

  private void validateOnUpdate(BusinessType request) {
    // todo: check if business type with same name already exists and if it does, check if it is the same business type
  }

}
