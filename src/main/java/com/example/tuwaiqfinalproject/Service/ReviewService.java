package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.BookingRepository;
import com.example.tuwaiqfinalproject.Repository.FieldRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import com.example.tuwaiqfinalproject.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FieldRepository fieldRepository;
    private final PlayerRepository playerRepository;
private final BookingRepository bookingRepository;

    public List<Review> getAllReview(){
        return reviewRepository.findAll();
    }

    public void addReview(Integer player_id,Integer field_id,Review review){
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null){
            throw new ApiException("Field Not Found");
        }
        Field field=fieldRepository.findFieldById(field_id);
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        Player playerOnField=playerRepository.findPlayedOnField(player.getId(),field);
        if(playerOnField ==null){
            throw new ApiException("Player has not played on this field ");
        }
        Booking booking=bookingRepository.findByPlayerIdAndMatchFieldId(player.getId(),field.getId());
        if(booking==null){
            throw new ApiException("Booking not found");
        }
        if(! booking.getIs_paid().equals(true) && ! booking.getStatus().equals("CONFIRMED")){
            throw new ApiException("Player has not booked a match on this field");
        }
        review.setField(field);
        fieldRepository.save(field);
        reviewRepository.save(review);
    }

    public void updateReview(Integer player_id,Integer review_id, Review review) {
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null){
            throw new ApiException("Field Not Found");
        }
        Review Old_review=reviewRepository.findReviewById(review_id);
        if (Old_review == null)
            throw new ApiException("Review not found");


Old_review.setComment(review.getComment());
Old_review.setAssessment_Level(review.getAssessment_Level());
        reviewRepository.save(review);
    }

    public void deleteReview(Integer player_id,Integer reviewId) {
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null){
            throw new ApiException("Field Not Found");
        }
        Review review=reviewRepository.findReviewById(reviewId);
        if (review == null)
            throw new ApiException("Organizer not found");

        reviewRepository.delete(review);
    }

    public List<Review> getReviewByFieldId(Integer player_id,Integer field_id){
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null){
            throw new ApiException("Field Not Found");
        }
        Field field=fieldRepository.findFieldById(field_id);
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        return field.getReviews();
    }
}
