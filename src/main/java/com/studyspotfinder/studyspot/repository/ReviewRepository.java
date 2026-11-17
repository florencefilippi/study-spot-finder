package com.studyspotfinder.studyspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.studyspotfinder.studyspot.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
    
    // all reviews for a location with newest first
    // loads each reviews in same query
    @EntityGraph(attributePaths = "author")
    List<Review> findByLocationIdOrderByCreatedAtDesc(String locationId);

    // has the user reviewed this location
    boolean existsByAuthor_UsernameAndLocationId(String username, String locationId);
}
