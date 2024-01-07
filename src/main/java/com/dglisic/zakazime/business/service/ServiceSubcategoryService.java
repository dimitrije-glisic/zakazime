package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateServiceSubcategoryRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceSubcategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.ServiceSubcategory;

public interface ServiceSubcategoryService {

  ServiceSubcategory save(@NotNull @Valid final CreateServiceSubcategoryRequest createServiceSubcategoryRequest);

  ServiceSubcategory findById(@NotNull final Integer id);

  ServiceSubcategory update(@NotNull final UpdateServiceSubcategoryRequest updateServiceSubcategoryRequest, @NotNull final Integer id);

  void delete(@NotNull final Integer id);

  List<ServiceSubcategory> getAll();

}
