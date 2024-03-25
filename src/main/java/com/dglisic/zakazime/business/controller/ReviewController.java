package com.dglisic.zakazime.business.controller;

import com.dglisic.zakazime.business.controller.dto.ReviewRequest;
import com.dglisic.zakazime.business.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import jooq.tables.pojos.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  //get all reviews for a user
  @GetMapping("/for-user/{userId}")
  public ResponseEntity<List<Review>> getReviewsForUser(@PathVariable Integer userId) {
    return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
  }

}
