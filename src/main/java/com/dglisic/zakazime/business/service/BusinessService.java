package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.UpdateServiceRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessType;
import jooq.tables.pojos.Service;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  List<Business> getAll();

  List<BusinessType> getBusinessTypes();

  void updateService(@NotBlank final int businessId, @NotBlank final int serviceId,
                     final @Valid UpdateServiceRequest updateServiceRequest);

  List<Service> searchServiceTemplates(@Nullable String businessType, @Nullable String category,
                                       @Nullable String subcategory);

  List<Service> getServicesOfBusiness(int businessId);

  void addServicesToBusiness(@NotEmpty @Valid final List<CreateServiceRequest> createServiceRequestList,
                             @NotNull final Integer businessId);

  Business create(final CreateBusinessProfileRequest createBusinessProfileRequest);

  Optional<Business> findBusinessById(int businessId);
}
