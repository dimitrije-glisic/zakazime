package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Appointment.APPOINTMENT;
import static jooq.tables.Customer.CUSTOMER;
import static jooq.tables.Review.REVIEW;

import com.dglisic.zakazime.business.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import jooq.tables.pojos.Review;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

  private final DSLContext jooq;

  @Override
  public Review createReview(Review review) {
    final var record = jooq.newRecord(REVIEW, review);
    record.store();
    return record.into(Review.class);
  }

  @Override
  public void updateReview(Integer reviewId, Review newReview) {
    jooq.update(REVIEW)
        .set(REVIEW.SERVICE, newReview.getService())
        .set(REVIEW.PRICE_QUALITY, newReview.getPriceQuality())
        .set(REVIEW.HYGIENE, newReview.getHygiene())
        .set(REVIEW.AMBIENCE, newReview.getAmbience())
        .set(REVIEW.COMMENT, newReview.getComment())
        .where(REVIEW.ID.eq(reviewId))
        .execute();
  }

  @Override
  public Optional<Review> findReviewForAppointment(Integer appointmentId) {
    Review review = jooq.selectFrom(REVIEW)
        .where(REVIEW.APPOINTMENT_ID.eq(appointmentId))
        .fetchOneInto(Review.class);
    return Optional.ofNullable(review);
  }

  @Override
  public Optional<Review> findReviewById(Integer reviewId) {
    Review review = jooq.selectFrom(REVIEW)
        .where(REVIEW.ID.eq(reviewId))
        .fetchOneInto(Review.class);
    return Optional.ofNullable(review);
  }

  @Override
  public void deleteReview(Integer id) {
    jooq.deleteFrom(REVIEW)
        .where(REVIEW.ID.eq(id))
        .execute();
  }

}
