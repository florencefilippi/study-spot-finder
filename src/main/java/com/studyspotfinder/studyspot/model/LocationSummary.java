package com.studyspotfinder.studyspot.model;

// Basic info about one location so the rest of the app never touches GeoJSON
// id and name are never null
public record LocationSummary(
    String id,
    String name,
    String type,
    String description
) {}