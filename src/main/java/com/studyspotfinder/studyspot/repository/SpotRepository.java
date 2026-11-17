package com.studyspotfinder.studyspot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.studyspotfinder.studyspot.model.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByOwnerUsername(String ownerUsername);
}