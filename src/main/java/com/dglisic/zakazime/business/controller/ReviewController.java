package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.ReviewRequest;
import com.dglisic.zakazime.business.service.ReviewService;
import com.dglisic.zakazime.common.MessageResponse;
import jakarta.validation.Valid;
import java.util.List;
import jooq.tables.pojos.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {

  private ReviewService reviewService;

  @PostMapping
  public ResponseEntity<Review> createReview(@RequestBody @Valid ReviewRequest request) {
    log.debug("Creating review: {}", request);
    return ResponseEntity.status(201).body(reviewService.createReview(request));
  }

  @PutMapping
  public ResponseEntity<MessageResponse> updateReview(@RequestBody @Valid ReviewRequest request) {
    log.debug("Updating review: {}", request);
    reviewService.updateReview(request);
    return ResponseEntity.ok(new MessageResponse("Review updated successfully"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponse> deleteReview(@PathVariable Integer id) {
    log.debug("Deleting review with id: {}", id);
    reviewService.deleteReview(id);
    return ResponseEntity.ok(new MessageResponse("Review deleted successfully"));
  }

  // delete this method it is not used
  //get all reviews for a user
  @GetMapping("/for-user/{userId}")
  public ResponseEntity<List<Review>> getReviewsForUser(@PathVariable Integer userId) {
    return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
  }

}
