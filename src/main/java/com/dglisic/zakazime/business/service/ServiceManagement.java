package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jooq.tables.pojos.Service;

public interface ServiceManagement {

  List<Service> addServicesToBusiness(@NotEmpty @Valid final List<CreateServiceRequest> createServiceRequestList,
                                      @NotNull final Integer businessId);
  Service addServiceToBusiness(final @NotNull @Valid CreateServiceRequest serviceRequest, @NotNull final Integer businessId);

  void deleteService(Integer businessId, Integer serviceId);

  void updateService(@NotBlank final int businessId, @NotBlank final int serviceId,
                     final @Valid UpdateServiceRequest updateServiceRequest);


  Service getServiceById(Integer serviceId);

  List<Service> getAllForEmployee(Integer businessId, Integer employeeId);
}
