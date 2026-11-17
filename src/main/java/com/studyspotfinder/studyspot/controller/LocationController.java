package com.studyspotfinder.studyspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.studyspotfinder.studyspot.model.LocationSummary;
import com.studyspotfinder.studyspot.model.ReviewService;
import com.studyspotfinder.studyspot.service.LocationLookup;
import com.studyspotfinder.studyspot.service.LocationNotFoundException;


@Controller 
public class LocationController {
    
    private final LocationLookup locationLookup;
    private final ReviewService reviewService;

    // constructor injection
    public LocationController(LocationLookup locationLookup, ReviewService reviewService) {
        this.locationLookup = locationLookup;
        this.reviewService = reviewService;
    }

    @GetMapping("/locations/details")
    public String locationDetails(@RequestParam(required = false) String id, Model model) {
        LocationSummary location = locationLookup.findById(id).orElseThrow(() -> new LocationNotFoundException(id));

        model.addAttribute("location", location);

        // newest first, alreaded formatted for display
        model.addAttribute("reviews", reviewService.getReviewsForLocation(id));

        return "location-details";
    }
    
}