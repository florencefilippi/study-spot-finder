package com.studyspotfinder.studyspot.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyspotfinder.studyspot.repository.ReviewRepository;
import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.service.LocationLookup;
import com.studyspotfinder.studyspot.service.LocationNotFoundException;
import com.studyspotfinder.studyspot.service.ReviewValidationException;



// all reviews here so the controller share them
@Service 
public class ReviewService {
    
    private static final String ALREADY_REVIEWED = "You've already reviewed this spot.";

    // dates are shown at b.c. time
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH).withZone(ZoneId.of("America/Vancouver"));

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final LocationLookup locationLookup;

    // constructor
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, LocationLookup locationLookup) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.locationLookup = locationLookup;
    }

    // newest first
    // mine is set for current users review
    @Transactional(readOnly = true)
    public List<ReviewView> getReviewsForLocation(String locationId, String currentUsername) {
        // convert list into a stream
        return reviewRepository.findByLocationIdOrderByCreatedAtDesc(locationId).stream()
            .map(review -> toView(review, currentUsername)).toList();
    }

    public boolean hasReviewed(String username, String locationId) {
        return reviewRepository.existsByAuthor_UsernameAndLocationId(username, locationId);
    }

    // username always comes from the logged in user
    public Review addReview(String locationId, String username, String rawText) {
        // location must exist
        if (locationLookup.findById(locationId).isEmpty()) {
            // no location was found
            throw new LocationNotFoundException(locationId);
        }

        String text = cleanText(rawText);

        // one review per user per location
        if (hasReviewed(username, locationId)) {
            throw new ReviewValidationException(ALREADY_REVIEWED);
        }

        // load logged in user
        User author = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("Logged in user not found: " + username));
        
        // save
        // if two submits arrive at same moment both could pass step 3 might need fix
        return reviewRepository.saveAndFlush(new Review(locationId, author, text, Instant.now()));
    }

    // helper to map into ReviewView
    private ReviewView toView(Review review, String currentUsername) {
        String authorName = review.getAuthor().getUsername();
        boolean mine = currentUsername != null && currentUsername.equals(authorName);

        return new ReviewView(
            review.getId(),
            authorName, 
            review.getText(), 
            DATE_FORMAT.format(review.getCreatedAt()),
            mine
        );
    }

    // Shared by add and edit
    // convert \r\n to \n before submitting
    private String cleanText(String rawText) {
        // Text. trim so surrounding spaces dont count
        String normalized = (rawText == null) ? "" : rawText.replace("\r\n", "\n");
        String cleaned = normalized.strip();

        if (cleaned.isEmpty()) {
            throw new ReviewValidationException("Review can't be empty.");
        }
        if (cleaned.length() > Review.MAX_TEXT_LENGTH) {
            throw new ReviewValidationException("Review must be " + Review.MAX_TEXT_LENGTH + " characters or fewer.");
        }

        return cleaned;
    }
}
