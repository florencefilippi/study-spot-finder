package com.studyspotfinder.studyspot.service;

import java.util.Optional;

import com.studyspotfinder.studyspot.model.LocationSummary;

// finds locations by their id
public interface LocationLookup {

    // returns empty for an unknown or null id
    Optional<LocationSummary> findById(String id);
}