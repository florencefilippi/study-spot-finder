package com.studyspotfinder.studyspot.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyspotfinder.studyspot.model.LocationSummary;
import com.studyspotfinder.studyspot.model.Review;
import com.studyspotfinder.studyspot.model.ReviewService;
import com.studyspotfinder.studyspot.service.LocationLookup;
import com.studyspotfinder.studyspot.service.LocationNotFoundException;
import com.studyspotfinder.studyspot.service.ReviewValidationException;

import org.springframework.web.bind.annotation.PostMapping;


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
    public String locationDetails(@RequestParam(required = false) String id, Model model, Principal principal) {
        LocationSummary location = locationLookup.findById(id).orElseThrow(() -> new LocationNotFoundException(id));

        return showDetails(location, principal.getName(), model);
    }

    @PostMapping("/locations/reviews")
    public String addReview(@RequestParam(required = false) String locationId, 
                                        @RequestParam(defaultValue = "") 
                                        String text, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        // author is the logged in user
        String username = principal.getName();

        try {
            reviewService.addReview(locationId, username, text);
        } catch (ReviewValidationException e) {
            // show same page again with the error
            // keeps what was typed
            LocationSummary location = locationLookup.findById(locationId).orElseThrow(() -> new LocationNotFoundException(locationId));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("text", text);
            return showDetails(location, username, model);
        }
        // locationNotFOundException is not caught here

        // refreshing the next page repeats the GET, not this POST
        redirectAttributes.addAttribute("id", locationId);
        redirectAttributes.addFlashAttribute("success", "Review posted.");
        return "redirect:/locations/details";
    }

    // everything the details page needs
    private String showDetails(LocationSummary location, String username, Model model) {
        model.addAttribute("location", location);
        model.addAttribute("reviews", reviewService.getReviewsForLocation(location.id(), username));
        model.addAttribute("alreadyReviewed", reviewService.hasReviewed(username, location.id()));
        model.addAttribute("maxLength", Review.MAX_TEXT_LENGTH);
        return "location-details";
    } 
}