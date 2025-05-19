package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Review;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/review")

public class ReviewController {
 private final ReviewService reviewService;
@GetMapping("/getAll")
 public ResponseEntity<?> getAllReview(){
     return ResponseEntity.status(200).body(reviewService.getAllReview());
 }
 @PostMapping("/add/{fieldId}")
 public ResponseEntity<?> addReview(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @RequestBody @Valid Review review){
 reviewService.addReview(user.getId(),fieldId,review);
return ResponseEntity.status(200).body(new ApiResponse("Review added successful"));
 }
 @PutMapping("/update/{reviewId}")
 public ResponseEntity<?> updateReview(@AuthenticationPrincipal User user,@PathVariable Integer reviewId, @RequestBody @Valid Review review){
 reviewService.updateReview(user.getId(),reviewId,review);
  return ResponseEntity.status(200).body(new ApiResponse("Review updated successful"));

 }
 @DeleteMapping("/delete/{reviewId}")
 public ResponseEntity<?> deleteReview(@AuthenticationPrincipal User user,@PathVariable Integer reviewId){
reviewService.deleteReview(user.getId(),reviewId);
  return ResponseEntity.status(200).body(new ApiResponse("Review deleted successful"));
 }
 @GetMapping("/getByField/{fieldId}")
 public ResponseEntity<?> getReviewByField(@AuthenticationPrincipal User user,@PathVariable Integer fieldId){
  return ResponseEntity.status(200).body(reviewService.getReviewByFieldId(user.getId(),fieldId));
 }

}
