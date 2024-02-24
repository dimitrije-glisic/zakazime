package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;

public record BusinessRichObject(
    Integer id,
    String name,
    String phoneNumber,
    String city,
    String postalCode,
    String address,
    String description,
    String profileImageUrl,
    List<Service> services,
    List<UserDefinedCategory> userDefinedCategories
) {


  public BusinessRichObject(Business business, List<Service> services, List<UserDefinedCategory> userDefinedCategories) {
    this(business.getId(), business.getName(), business.getPhoneNumber(), business.getCity(), business.getPostalCode(),
        business.getAddress(), business.getDescription(), business.getProfileImageUrl(), services, userDefinedCategories);
  }

}




