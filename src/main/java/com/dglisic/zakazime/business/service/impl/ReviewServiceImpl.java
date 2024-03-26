package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.controller.dto.AppointmentRichObject;
import com.dglisic.zakazime.business.controller.dto.ReviewRequest;
import com.dglisic.zakazime.business.repository.ReviewRepository;
import com.dglisic.zakazime.business.service.AppointmentService;
import com.dglisic.zakazime.business.service.ReviewService;
import com.dglisic.zakazime.common.ApplicationException;
import com.dglisic.zakazime.user.service.UserService;
import jooq.tables.pojos.Account;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

  private final AppointmentService appointmentService;
  private final UserService userService;
  private final ReviewRepository reviewRepository;

  @Override
  public Review createReview(ReviewRequest request) {
    //review for appointment not exists
    if (reviewRepository.findReviewForAppointment(request.appointmentId()).isPresent()) {
      throw new ApplicationException("Review for given appointment already exists", HttpStatus.BAD_REQUEST);
    }

    //appointment exists and current user is the owner of the appointment
    final AppointmentRichObject appointment = appointmentService.requireAppointmentFullInfo(request.appointmentId());
    log.debug("Customer: {}", appointment.customer());
    final Account currentUser = userService.requireLoggedInUser();
    log.debug("Current user: {}", currentUser);
    if (!isAppointmentOwner(appointment.customer(), currentUser)) {
      throw new ApplicationException("User is not the owner of the appointment", HttpStatus.BAD_REQUEST);
    }

    //create review
    final Review newReview = fromRequest(request);
    return reviewRepository.createReview(newReview);
  }

  @Override
  public void updateReview(ReviewRequest request) {
    //review exists
    final Review existingReview = reviewRepository.findReviewForAppointment(request.appointmentId())
        .orElseThrow(() -> new ApplicationException("Review for given appointment not exists", HttpStatus.BAD_REQUEST));

    //appointment exists and current user is the owner of the appointment
    final AppointmentRichObject appointment = appointmentService.requireAppointmentFullInfo(request.appointmentId());
    final Account currentUser = userService.requireLoggedInUser();
    if (!isAppointmentOwner(appointment.customer(), currentUser)) {
      throw new ApplicationException("User is not the owner of the appointment", HttpStatus.BAD_REQUEST);
    }

    //update review
    final Review newReview = fromRequest(request);
    reviewRepository.updateReview(existingReview.getId(), newReview);
  }

  @Override
  public void deleteReview(Integer reviewId) {
    //review exists
    final Review existingReview = reviewRepository.findReviewById(reviewId)
        .orElseThrow(() -> new ApplicationException("Review not exists", HttpStatus.BAD_REQUEST));

    //appointment exists and current user is the owner of the appointment
    final AppointmentRichObject appointment = appointmentService.requireAppointmentFullInfo(existingReview.getAppointmentId());
    final Account currentUser = userService.requireLoggedInUser();
    if (!isAppointmentOwner(appointment.customer(), currentUser)) {
      throw new ApplicationException("User is not the owner of the appointment", HttpStatus.BAD_REQUEST);
    }

    //delete review
    reviewRepository.deleteReview(existingReview.getId());
  }

  private boolean isAppointmentOwner(Customer customer, Account currentUser) {
    //compare emails

    return customer.getEmail().equals(currentUser.getEmail());
  }

  private Review fromRequest(ReviewRequest request) {
    return new Review()
        .setAppointmentId(request.appointmentId())
        .setService(request.service())
        .setPriceQuality(request.priceQuality())
        .setHygiene(request.hygiene())
        .setAmbience(request.ambience())
        .setComment(request.comment());
  }

}
