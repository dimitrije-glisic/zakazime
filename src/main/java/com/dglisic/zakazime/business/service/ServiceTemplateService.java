package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.Service;

public interface ServiceTemplateService {

  List<Service> searchServiceTemplates(@Nullable final String businessType, @Nullable final String category,
                                       @Nullable final String subcategory);

  Service getServiceTemplate(@NotNull final Integer id);


  Service createServiceTemplate(@NotNull @Valid final CreateServiceRequest createServiceRequest);

  void updateServiceTemplate(@NotNull final Integer id, @NotNull final UpdateServiceRequest createServiceRequest);

  void deleteServiceTemplate(@NotNull final Integer id);
}
