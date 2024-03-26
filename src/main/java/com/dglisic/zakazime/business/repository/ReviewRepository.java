package com.dglisic.zakazime.business.repository;

import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Review;

public interface ReviewRepository {

    Review createReview(Review review);

  void updateReview(Integer id, Review newReview);

  Optional<Review> findReviewForAppointment(Integer integer);

  Optional<Review> findReviewById(Integer reviewId);

  void deleteReview(Integer id);
}
