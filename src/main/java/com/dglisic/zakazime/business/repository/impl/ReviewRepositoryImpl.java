package com.dglisic.zakazime.business.repository.impl;

import static jooq.tables.Account.ACCOUNT;
import static jooq.tables.Appointment.APPOINTMENT;
import static jooq.tables.Customer.CUSTOMER;
import static jooq.tables.Review.REVIEW;

import com.dglisic.zakazime.business.repository.ReviewRepository;
import java.util.List;
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
  public List<Review> getReviewsForUser(Integer userId) {
    return jooq.select(REVIEW)
        .from(REVIEW)
        .join(APPOINTMENT)
        .on(REVIEW.APPOINTMENT_ID.eq(APPOINTMENT.ID))
        .join(CUSTOMER)
        .on(APPOINTMENT.CUSTOMER_ID.eq(CUSTOMER.ID))
        .join(ACCOUNT)
        .on(CUSTOMER.EMAIL.eq(ACCOUNT.EMAIL))
        .where(ACCOUNT.ID.eq(userId))
        .fetchInto(Review.class);
  }

}
