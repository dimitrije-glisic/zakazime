package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Business.BUSINESS;
import static jooq.tables.BusinessImage.BUSINESS_IMAGE;
import static org.jooq.impl.DSL.asterisk;

import com.dglisic.zakazime.business.repository.BusinessImageRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.BusinessImage;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BusinessImageRepositoryImpl implements BusinessImageRepository {

  private final DSLContext dsl;

  @Override
  public BusinessImage storeImage(Integer businessId, String imageUrl) {
    return dsl.insertInto(BUSINESS_IMAGE)
        .set(BUSINESS_IMAGE.BUSINESS_ID, businessId)
        .set(BUSINESS_IMAGE.IMAGE_URL, imageUrl)
        .set(BUSINESS_IMAGE.CREATED_ON, LocalDateTime.now())
        .returning(asterisk())
        .fetchOneInto(BusinessImage.class);
  }

  @Override
  public boolean imageBelongsToBusiness(Integer imageId, Integer businessId) {
    final var condition = BUSINESS_IMAGE.ID.eq(imageId).and(BUSINESS_IMAGE.BUSINESS_ID.eq(businessId));
    return dsl.fetchExists(BUSINESS_IMAGE, condition);
  }

  @Override
  public void deleteImage(Integer imageId) {
    dsl.deleteFrom(BUSINESS_IMAGE).where(BUSINESS_IMAGE.ID.eq(imageId)).execute();
  }

  @Override
  public List<BusinessImage> getImages(Integer businessId) {
    return dsl.select(BUSINESS_IMAGE)
        .from(BUSINESS_IMAGE)
        .where(BUSINESS_IMAGE.BUSINESS_ID.eq(businessId))
        .fetchInto(BusinessImage.class);
  }

}
