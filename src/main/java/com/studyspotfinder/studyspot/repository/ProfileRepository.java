package com.studyspotfinder.studyspot.repository;
import com.studyspotfinder.studyspot.model.Profile;
import com.studyspotfinder.studyspot.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
}
