package com.studyspotfinder.studyspot.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.studyspotfinder.studyspot.model.LocationSummary;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

// Reads both GeoJSON files at startup
@Service 
public class GeoJsonLocationLookup implements LocationLookup {
    
    private static final Logger log = LoggerFactory.getLogger(GeoJsonLocationLookup.class);

    // Paths for feature locations
    private static final String SFU_PATH = "static/data/sfu-studySpots.geojson";
    private static final String CAFE_PATH = "static/data/vancouver-cafes.geojson";

    private final JsonMapper mapper;
    private final Map<String, LocationSummary> locations;

    // constructor
    public GeoJsonLocationLookup(JsonMapper mapper) {
        this.mapper = mapper; // must be set before loadFile() runs

        Map<String, LocationSummary> loaded = new HashMap<>();
        int sfuCount = loadFile(SFU_PATH, loaded);
        int cafeCount = loadFile(CAFE_PATH, loaded);

        // freeze
        this.locations = Map.copyOf(loaded);

        // log locations
        log.info("Loaded {} locations ({} SFU, {} cafes)", locations.size(), sfuCount, cafeCount);
    }

    // returns empty for an unknown or null id
    @Override
    public Optional<LocationSummary> findById(String id) {
        if (id == null) {
            return  Optional.empty();
        }

        // returns empty if map doesnt contain ID
        return Optional.ofNullable(locations.get(id));
    }

    // returns fields value if exists otherwise null
    private String textOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || !value.isString()) {
            return null;
        }

        // get text from value
        String text = value.asString().trim();

        if (text.isEmpty()) {
            return null;
        }

        return text;
    }

    // take entire GeoJson feature, returns summary
    private LocationSummary toSummary(JsonNode feature) {
        // get id
        String id = textOrNull(feature, "id");

        JsonNode properties = feature.path("properties");

        // get property info
        String name = textOrNull(properties, "name");
        String type = textOrNull(properties, "type");
        String description = textOrNull(properties, "description");

        if (name == null) {
            name = "Unnamed Location";
        }

        return new LocationSummary(id, name, type, description);
    }

    // Open and parse one file, and features into map, log warnings, return how many were added
    private int loadFile(String path, Map<String, LocationSummary> map) {
        JsonNode root;

        // try with resources closes the stream automatically
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            root = mapper.readTree(in);
        } catch (IOException e) {
            // missing file
            throw new UncheckedIOException("Could not read location file: " + path, e);
        }

        int added = 0;
        // if file has no features loop doesnt run
        for (JsonNode feature : root.path("features")) {
            LocationSummary summary = toSummary(feature);

            if (summary.id() == null) {
                log.warn("Skipping a feature in {} with no id (name: {})", path, summary.name());
                continue;
            }
            if (map.containsKey(summary.id())) {
                log.warn("Duplicate location id '{}' in {}; keeping the first one", summary.id(), path);
                continue;
            }

            map.put(summary.id(), summary);
            added++;
        }
        return added;
    }
}
