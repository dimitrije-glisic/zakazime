package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.BusinessRichObject;
import com.dglisic.zakazime.business.controller.dto.CreateBusinessProfileRequest;
import com.dglisic.zakazime.business.controller.dto.ImageType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.BusinessImage;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.PredefinedCategory;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;
import org.springframework.web.multipart.MultipartFile;

public interface BusinessService {

  Business getBusinessProfileForUser(String userEmail);

  List<Business> getAll();

  List<Service> getServicesOfBusiness(Integer businessId);


  Business create(final CreateBusinessProfileRequest createBusinessProfileRequest);

  Optional<Business> findBusinessById(@NotNull final Integer businessId);

  void linkPredefinedCategories(List<Integer> categoryIds, Integer businessId);

  List<PredefinedCategory> getPredefinedCategories(Integer businessId);

  List<UserDefinedCategory> getUserDefinedCategories(Integer businessId);

  List<Business> searchBusinesses(String city, String businessType, String category);

  List<Business> getAllBusinessesInCity(String city);

  String uploadImage(Integer id, MultipartFile image, ImageType imageType);

  void deleteImage(Integer businessId, Integer imageId);

  List<BusinessImage> getImages(Integer businessId);

  BusinessImage getProfileImage(Integer businessId);

  BusinessRichObject getCompleteBusinessData(String city, String businessName);

  void submitBusiness(Integer businessId);

  List<Business> getAllActive();

  List<Employee> getEmployees(Integer businessId);
}
