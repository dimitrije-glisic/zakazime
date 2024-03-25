package com.dglisic.zakazime.business.repository;

import java.util.List;
import jooq.tables.pojos.Review;

public interface ReviewRepository {

    Review createReview(Review review);

  List<Review> getReviewsForUser(Integer userId);
}
