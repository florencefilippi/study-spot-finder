package com.studyspotfinder.studyspot.repository;

import com.studyspotfinder.studyspot.model.SavedSpot;
import com.studyspotfinder.studyspot.model.User;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedSpotRepository extends JpaRepository<SavedSpot, Long>{
    List<SavedSpot> findByUser(User user);
    boolean existsByUserAndSpotId(User user, String spotId);
    long countBySpotId(String spotId);
}