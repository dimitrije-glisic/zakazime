package com.dglisic.zakazime.business.service;


import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.ServiceCategory;
import org.springframework.web.multipart.MultipartFile;

public interface ServiceCategoryService {

  ServiceCategory create(@NotNull @Valid final CreateServiceCategoryRequest createServiceCategoryRequest);

  ServiceCategory createWithImage(@NotNull @Valid final CreateServiceCategoryRequest createRequest, @NotNull MultipartFile image);

  ServiceCategory requireById(@NotNull final Integer id);

  ServiceCategory update(@NotNull final UpdateServiceCategoryRequest updateServiceCategoryRequest, @NotNull final Integer id);

  void delete(@NotNull final Integer id);

  List<ServiceCategory> getAll();

  byte[] getImage(Integer id);
}
