package com.studyspotfinder.studyspot.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.studyspotfinder.studyspot.model.LocationSummary;
import com.studyspotfinder.studyspot.service.LocationLookup;
import com.studyspotfinder.studyspot.service.LocationNotFoundException;


@Controller 
public class LocationController {
    
    private final LocationLookup locationLookup;

    // constructor injection
    public LocationController(LocationLookup locationLookup) {
        this.locationLookup = locationLookup;
    }

    @GetMapping("/locations/details")
    public String locationDetails(@RequestParam(required = false) String id, Model model) {
        LocationSummary location = locationLookup.findById(id).orElseThrow(() -> new LocationNotFoundException(id));

        model.addAttribute("location", location);

        return "location-details";
    }
    
}