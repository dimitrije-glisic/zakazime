package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import com.dglisic.zakazime.business.repository.BusinessTypeRepository;
import com.dglisic.zakazime.business.service.BusinessTypeService;
import com.dglisic.zakazime.business.service.ImageStorage;
import com.dglisic.zakazime.common.ApplicationException;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessTypeServiceImpl implements BusinessTypeService {

  private final BusinessTypeRepository businessTypeRepository;
  private final ImageStorage imageStorage;

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
  @Transactional
  public BusinessType createWithFile(final CreateBusinessTypeRequest createRequest, final MultipartFile file) throws IOException {
    validateOnCreate(createRequest);
    final BusinessType toBeCreated = new BusinessType();
    toBeCreated.setTitle(createRequest.title());
    final BusinessType newBType = businessTypeRepository.create(toBeCreated);
    final String url = makeUrl(newBType.getId(), file);
    storeImage(url, file);
    businessTypeRepository.updateImage(newBType.getId(), url);
    newBType.setImageUrl(url);
    return newBType;
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
  @Transactional
  public void update(final Integer id, final UpdateBusinessTypeRequest businessType, final MultipartFile file)
      throws IOException {
    final BusinessType inUpdate = validateOnUpdate(id);
    if (businessType.title().equalsIgnoreCase(inUpdate.getTitle()) && file.isEmpty()) {
      // nothing to update
      return;
    }
    final String url = makeUrl(id, file);
    if (url.equalsIgnoreCase(inUpdate.getImageUrl())) {
      // nothing to update
      return;
    }
    storeImage(url, file);
    inUpdate.setTitle(businessType.title());
    inUpdate.setImageUrl(url);
    businessTypeRepository.update(inUpdate);
  }

  @Override
  public void delete(final Integer id) {
    validateOnDelete(id);
    businessTypeRepository.deleteById(id);
  }

  final static String IMAGE_DIRECTORY_ROOT = "C:\\Users\\dglisic\\personal-projects\\storage\\images\\";

  @Override
  @Transactional
  public String uploadImage(final Integer id, final MultipartFile file) throws IOException {
    final String url = makeUrl(id, file);
    storeImage(url, file);
    businessTypeRepository.updateImage(id, url);
    return url;
  }

  @Override
  public byte[] getImage(final Integer id) {
    final BusinessType businessType = requireById(id);
    if (businessType.getImageUrl() == null) {
      throw new ApplicationException("Business type with id " + id + " does not have an image",
          HttpStatus.NOT_FOUND);
    }
    try {
      return imageStorage.getImage(businessType.getImageUrl());
    } catch (IOException e) {
      log.error("Failed to read image", e);
      throw new ApplicationException("Failed to read image", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void storeImage(final String url, final MultipartFile file) {
    try {
      imageStorage.storeImage(url, file);
    } catch (IOException e) {
      log.error("Failed to store image", e);
      throw new ApplicationException("Failed to store image", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private @NotNull String makeUrl(final Integer id, final MultipartFile file) {
    final String idPartOfPath = String.format("id_%d", id);
    return "business-types" + "/" + idPartOfPath + "/" + file.getOriginalFilename();
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
