package com.studyspotfinder.studyspot.model;

// Basic info about one location so the rest of the app never touches GeoJSON
// id and name are never null
public record LocationSummary(
    String id,
    String name,
    String type,
    String description
) {
    // fix for EL1008E bug in the location-details html
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }    
}