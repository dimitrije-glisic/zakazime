package com.dglisic.zakazime.business.service;


import com.dglisic.zakazime.business.controller.dto.CreateServiceCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceCategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.PredefinedCategory;
import org.springframework.web.multipart.MultipartFile;

public interface PredefinedCategoryService {

  PredefinedCategory create(@NotNull @Valid final CreateServiceCategoryRequest createServiceCategoryRequest);

  PredefinedCategory createWithImage(@NotNull @Valid final CreateServiceCategoryRequest createRequest,
                                     @NotNull MultipartFile image);

  PredefinedCategory requireById(@NotNull final Integer id);

  void update(@NotNull final UpdateServiceCategoryRequest updateServiceCategoryRequest, @NotNull final Integer id);

  void delete(@NotNull final Integer id);

  List<PredefinedCategory> getAll();

  byte[] getImage(Integer id);

  String uploadImage(Integer id, MultipartFile file);
}
