package com.dglisic.zakazime.business.service;


import com.dglisic.zakazime.business.controller.dto.CreatePredefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdatePredefinedCategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import jooq.tables.pojos.PredefinedCategory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface PredefinedCategoryService {

  PredefinedCategory create(@NotNull @Valid final CreatePredefinedCategoryRequest createPredefinedCategoryRequest);

  PredefinedCategory createWithImage(@NotNull @Valid final CreatePredefinedCategoryRequest createRequest,
                                     @NotNull MultipartFile image);

  PredefinedCategory requireById(@NotNull final Integer id);

  void update(@NotNull Integer categoryId, @NotNull UpdatePredefinedCategoryRequest updatePredefinedCategoryRequest);

  @Transactional
  void update(Integer id, UpdatePredefinedCategoryRequest updateRequest, MultipartFile file)
      throws IOException;

  void delete(@NotNull final Integer id);

  List<PredefinedCategory> getAll();

  byte[] getImage(Integer id);

  String uploadImage(Integer id, MultipartFile file);
}
