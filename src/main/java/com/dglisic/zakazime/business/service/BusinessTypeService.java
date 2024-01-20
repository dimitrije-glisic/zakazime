package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessTypeRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateBusinessTypeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.BusinessType;
import org.springframework.web.multipart.MultipartFile;

public interface BusinessTypeService {

  List<BusinessType> getAll();

  BusinessType requireById(@NotNull final Integer id);

  BusinessType create(@NotNull @Valid final CreateBusinessTypeRequest businessType);

  void update(@NotNull final Integer id, @NotNull @Valid final UpdateBusinessTypeRequest businessType);

  void delete(@NotNull final Integer id);

  String uploadImage(@NotNull final Integer id, @NotNull final MultipartFile file) throws IOException;

  byte[] getImage(@NotNull final Integer id);

  BusinessType createWithImage(@NotNull @Valid final CreateBusinessTypeRequest createRequest, @NotNull final MultipartFile file)
      throws IOException;

  void update(@NotNull final Integer id, @NotNull final UpdateBusinessTypeRequest businessType, @NotNull final MultipartFile file)
      throws IOException;
}
