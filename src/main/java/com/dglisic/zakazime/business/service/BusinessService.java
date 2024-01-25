package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  List<Business> getAll();

  void updateService(@NotBlank final int businessId, @NotBlank final int serviceId,
                     final @Valid UpdateServiceRequest updateServiceRequest);

  List<Service> getServicesOfBusiness(int businessId);

  void addServiceToBusiness(@NotEmpty @Valid final List<CreateServiceRequest> createServiceRequestList,
                            @NotNull final Integer businessId);

  Business create(final CreateBusinessProfileRequest createBusinessProfileRequest);

  Optional<Business> findBusinessById(@NotNull final Integer businessId);

  void addServiceToBusiness(final @NotNull @Valid CreateServiceRequest serviceRequest, @NotNull final Integer businessId);

  void linkPredefinedCategories(List<Integer> categoryIds, Integer businessId);

  List<PredefinedCategory> getPredefinedCategories(Integer businessId);
}
