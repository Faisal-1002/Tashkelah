package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Review;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Review findReviewById(Integer review);
}
