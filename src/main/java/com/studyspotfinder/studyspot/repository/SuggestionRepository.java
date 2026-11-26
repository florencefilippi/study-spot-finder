package com.studyspotfinder.studyspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyspotfinder.studyspot.model.Suggestion;
import com.studyspotfinder.studyspot.model.SuggestionStatus;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    // admin queue (PENDING) and the map (APPROVED), oldest first
    List<Suggestion> findByStatusOrderByCreatedAtAsc(SuggestionStatus status);

    // "My suggestions" page, newest first
    List<Suggestion> findBySuggestedByOrderByCreatedAtDesc(String suggestedBy);

    // spam limit: how many pending suggestions this user has
    long countBySuggestedByAndStatus(String suggestedBy, SuggestionStatus status);

    // pending count for the admin dashboard
    long countByStatus(SuggestionStatus status);
}