package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.BusinessImage;

public interface BusinessImageRepository {

  BusinessImage storeImage(Integer businessId, String imageUrl);

  boolean imageBelongsToBusiness(Integer imageId, Integer businessId);

  void deleteImage(Integer imageId);

  List<BusinessImage> getImages(Integer businessId);

}
