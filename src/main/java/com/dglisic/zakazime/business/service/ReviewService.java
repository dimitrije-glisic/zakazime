package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.ReviewRequest;
import jooq.tables.pojos.Review;

public interface ReviewService {

  Review createReview(ReviewRequest review);

  void updateReview(ReviewRequest request);

  void deleteReview(Integer id);
}
