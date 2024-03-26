package com.dglisic.zakazime.business.service;

import com.dglisic.zakazime.business.controller.dto.ReviewRequest;
import java.util.List;
import jooq.tables.pojos.Review;

public interface ReviewService {

  Review createReview(ReviewRequest review);

  List<Review> getReviewsForUser(Integer userId);

  void updateReview(ReviewRequest request);

  void deleteReview(Integer id);
}
