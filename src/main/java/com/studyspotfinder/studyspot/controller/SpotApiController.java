package com.studyspotfinder.studyspot.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyspotfinder.studyspot.model.Spot;
import com.studyspotfinder.studyspot.repository.SpotRepository;

@RestController
public class SpotApiController {

    private final SpotRepository spots;

    public SpotApiController(SpotRepository spots) {
        this.spots = spots;
    }

    @GetMapping("/api/spots")
    public Map<String, Object> allSpots() {
        List<Map<String, Object>> features = new ArrayList<>();

        for (Spot s : spots.findAll()) {
            Map<String, Object> geometry = new LinkedHashMap<>();
            geometry.put("type", "Point");
            geometry.put("coordinates", List.of(s.getLongitude(), s.getLatitude()));

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("name", s.getName());
            props.put("type", s.getType());
            props.put("description", s.getDescription());
            props.put("opening_hours", s.getOpeningHours());
            props.put("owner", s.getOwnerUsername());

            Map<String, Object> feature = new LinkedHashMap<>();
            feature.put("type", "Feature");
            feature.put("id", "spot/" + s.getId());
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