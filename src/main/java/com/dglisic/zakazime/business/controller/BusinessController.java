package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.domain.Business;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.business.domain.Service;
import com.dglisic.zakazime.business.service.BusinessService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {
  private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

  private final BusinessService businessService;
  private final BusinessMapper businessMapper;
  private final ServiceMapper serviceMapper;

  @PostMapping
  public ResponseEntity<CreateBusinessProfileResponse> createBusinessProfile(
      @RequestBody @Valid CreateBusinessProfileRequest createBusinessProfileRequest) {
    logger.info("Creating business profile {}", createBusinessProfileRequest);
    Business created = businessService.createBusinessProfile(createBusinessProfileRequest);
    return ResponseEntity.ok(businessMapper.mapToCreateBusinessProfileResponse(created));
  }

  @GetMapping
  public BusinessDTO getBusinessProfileForUser(Principal user) {
    logger.info("Getting business profile for user {}", user.getName());
    String userEmail = user.getName();
    Business business = businessService.getBusinessProfileForUser(userEmail);
    return businessMapper.mapToBusinessProfileDTO(business);
  }

  @GetMapping("types")
  public List<String> getBusinessTypes() {
    logger.info("Getting business types");
    return businessService.getBusinessTypes().stream()
        .map(BusinessType::getTitle)
        .toList();
  }

  @GetMapping("types/{businessType}/services")
  public List<ServiceDTO> getServicesOfType(@PathVariable String businessType) {
    logger.info("Getting services of type {}", businessType);
    List<ServiceDTO> serviceDTOS = serviceMapper.mapToServiceDTOs(businessService.getServiceTemplatesOfType(businessType));
    logger.info("Found {} services of type {}", serviceDTOS.size(), businessType);
    return serviceDTOS;
  }

  @GetMapping("{businessName}/services")
  public List<ServiceDTO> getServicesForBusiness(@PathVariable @Valid @NotBlank String businessName) {
    List<Service> servicesOfBusiness = businessService.getServicesOfBusiness(businessName);
    logger.info("Getting Services ({}) of business {}: {}", servicesOfBusiness.size(), businessName, servicesOfBusiness);
    return servicesOfBusiness.stream()
        .map(serviceMapper::mapToServiceDTO)
        .toList();
  }

  @PostMapping("{businessName}/services")
  public void saveServicesForBusiness(@PathVariable @Valid @NotBlank String businessName,
                                      @RequestBody List<CreateServiceRequest> serviceRequests) {
    logger.info("Saving services {} for business {}", serviceRequests, businessName);
    List<Service> servicesToBeSaved = serviceRequests.stream()
        .map(request -> serviceMapper.mapToService(request, businessName))
        .toList();
    businessService.saveServices(servicesToBeSaved);
  }

  @PutMapping("{businessName}/services/{serviceId}")
  public void updateService(@PathVariable @Valid @NotBlank String businessName,
                            @PathVariable @Valid @NotBlank String serviceId,
                            @RequestBody CreateServiceRequest serviceRequest) {
    logger.info("Updating service {} for business {}", serviceRequest, businessName);
    Service service = serviceMapper.mapToService(serviceRequest, businessName);
    businessService.updateService(serviceId, service);
  }

}
