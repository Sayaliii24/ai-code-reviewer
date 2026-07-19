package com.example.aicodereview.controller;

import com.example.aicodereview.model.Review;
import com.example.aicodereview.model.User;
import com.example.aicodereview.repository.ReviewRepository;
import com.example.aicodereview.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getMyReviews(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userOpt.get().getId());
        return ResponseEntity.ok(reviews);
    }
}
