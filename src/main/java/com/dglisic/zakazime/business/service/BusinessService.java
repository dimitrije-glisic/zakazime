package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.CreateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.CreateUserDefinedCategoryRequest;
import com.dglisic.zakazime.business.controller.dto.ImageType;
import com.dglisic.zakazime.business.controller.dto.UpdateServiceRequest;
import com.dglisic.zakazime.business.controller.dto.UpdateUserDefinedCategoryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import org.springframework.web.multipart.MultipartFile;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  List<Business> getAll();

  void updateService(@NotBlank final int businessId, @NotBlank final int serviceId,
                     final @Valid UpdateServiceRequest updateServiceRequest);

  List<Service> getServicesOfBusiness(Integer businessId);

  List<Service> addServicesToBusiness(@NotEmpty @Valid final List<CreateServiceRequest> createServiceRequestList,
                                      @NotNull final Integer businessId);

  Business create(final CreateBusinessProfileRequest createBusinessProfileRequest);

  Optional<Business> findBusinessById(@NotNull final Integer businessId);

  Service addServicesToBusiness(final @NotNull @Valid CreateServiceRequest serviceRequest, @NotNull final Integer businessId);

  void linkPredefinedCategories(List<Integer> categoryIds, Integer businessId);

  List<PredefinedCategory> getPredefinedCategories(Integer businessId);

  List<UserDefinedCategory> getUserDefinedCategories(Integer businessId);

  void createUserDefinedCategory(CreateUserDefinedCategoryRequest categoryRequest, Integer businessId);

  void updateUserDefinedCategory(Integer businessId, Integer categoryId, UpdateUserDefinedCategoryRequest categoryRequest);

  void deleteService(Integer businessId, Integer serviceId);

  List<Business> searchBusinesses(String city, String businessType, String category);

  List<Business> getAllBusinessesInCity(String city);

  String uploadImage(Integer id, MultipartFile image, ImageType imageType);

  void deleteImage(Integer businessId, Integer imageId);

  List<BusinessImage> getImages(Integer businessId);

  BusinessImage getProfileImage(Integer businessId);
}
