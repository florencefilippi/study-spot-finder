package com.studyspotfinder.studyspot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Turns an address into coordinates using OpenStreetMap's Nominatim API.
// The suggestion form calls this; our server calls Nominatim.
@RestController
public class GeocodeController {

    // Nominatim's usage policy asks every app to identify itself
    private static final String USER_AGENT = "StudySpotFinder/1.0 (SFU CMPT 276 student project)";

    private final RestClient nominatim = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", USER_AGENT)
            .build();

    @GetMapping("/api/geocode")
    public ResponseEntity<Map<String, Object>> geocode(@RequestParam(defaultValue = "") String address) {
        final String query = address.trim();
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please enter an address."));
        }

        List<Map<String, Object>> results;
        try {
            results = nominatim.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("q", query)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .queryParam("countrycodes", "ca")
                            // prefer results in Metro Vancouver (left, top, right, bottom)
                            .queryParam("viewbox", "-123.3,49.4,-122.5,49.0")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (RestClientException e) {
            return ResponseEntity.status(502)
                    .body(Map.of("error", "Address search is unavailable right now. Place the pin manually."));
        }

        if (results == null || results.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Couldn't find that address. Place the pin manually."));
        }

        Map<String, Object> first = results.get(0);
        // Nominatim sends lat/lon as text, so convert them to numbers
        return ResponseEntity.ok(Map.of(
                "lat", Double.parseDouble(first.get("lat").toString()),
                "lon", Double.parseDouble(first.get("lon").toString()),
                "displayName", first.get("display_name").toString()));
    }
}