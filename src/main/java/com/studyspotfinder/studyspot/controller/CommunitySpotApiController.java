package com.studyspotfinder.studyspot.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyspotfinder.studyspot.model.Suggestion;
import com.studyspotfinder.studyspot.model.SuggestionStatus;
import com.studyspotfinder.studyspot.repository.SuggestionRepository;

// Sends APPROVED suggestions to the map as GeoJSON (same shape as the cafe/SFU files)
@RestController
public class CommunitySpotApiController {

    private final SuggestionRepository suggestions;

    public CommunitySpotApiController(SuggestionRepository suggestions) {
        this.suggestions = suggestions;
    }

    @GetMapping("/api/community-spots")
    public Map<String, Object> communitySpots() {
        List<Map<String, Object>> features = new ArrayList<>();

        for (Suggestion s : suggestions.findByStatusOrderByCreatedAtAsc(SuggestionStatus.APPROVED)) {
            Map<String, Object> geometry = new LinkedHashMap<>();
            geometry.put("type", "Point");
            // GeoJSON order is [longitude, latitude]
            geometry.put("coordinates", List.of(s.getLongitude(), s.getLatitude()));

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("name", s.getName());
            props.put("type", s.getType());
            props.put("opening_hours", s.getOpeningHours());
            props.put("description", s.getDescription());

            Map<String, Object> feature = new LinkedHashMap<>();
            feature.put("type", "Feature");
            // No "id" yet on purpose: the popup only shows "View Details & Reviews"
            // when there's an id, and the details page doesn't know about suggestions yet,
            // so the link would lead to an error page. Add "community/" + id once it does.
            feature.put("geometry", geometry);
            feature.put("properties", props);
            features.add(feature);
        }

        Map<String, Object> collection = new LinkedHashMap<>();
        collection.put("type", "FeatureCollection");
        collection.put("features", features);
        return collection;
    }
}