package com.studyspotfinder.studyspot.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyspotfinder.studyspot.model.LocationSummary;
import com.studyspotfinder.studyspot.model.Review;
import com.studyspotfinder.studyspot.model.ReviewService;
import com.studyspotfinder.studyspot.service.LocationLookup;
import com.studyspotfinder.studyspot.service.LocationNotFoundException;
import com.studyspotfinder.studyspot.service.ReviewValidationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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

    @PostMapping("/locations/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        // throws 404 or 403 if review is missing or not yours
        String locationId = reviewService.deleteReview(id, principal.getName());

        // refreshing the next page repats the GET
        redirectAttributes.addAttribute("id", locationId);
        redirectAttributes.addFlashAttribute("success", "Review deleted.");
        
        return "redirect:/locations/details";
    }

    @GetMapping("/locations/reviews/{id}/edit")
    public String editReviewForm(@PathVariable Long id, Principal principal, Model model) {
        // throws 404 or 403 if review is missing or not yours
        Review review = reviewService.findOwnedReview(id, principal.getName());

        showEditForm(review, review.getText(), model);

        return "review-form";
    }

    @PostMapping("/locations/reviews/{id}/edit")
    public String updateReview(
        @PathVariable Long id,
        @RequestParam(defaultValue = "") String text,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        String username = principal.getName();

        try {
            Review review = reviewService.updateReview(id, username, text);

            redirectAttributes.addAttribute("id", review.getLocationId());
            redirectAttributes.addFlashAttribute("success", "Review updated.");

            return "redirect:/locations/details";

        } catch (ReviewValidationException e) {
            // show the edit page again with the error keep what was type
            Review review = reviewService.findOwnedReview(id, username);

            model.addAttribute("error", e.getMessage());
            showEditForm(review, text, model);

            return "review-form";
        }
    }
    

    // everything the details page needs
    private String showDetails(LocationSummary location, String username, Model model) {
        model.addAttribute("location", location);
        model.addAttribute("reviews", reviewService.getReviewsForLocation(location.id(), username));
        model.addAttribute("alreadyReviewed", reviewService.hasReviewed(username, location.id()));
        model.addAttribute("maxLength", Review.MAX_TEXT_LENGTH);
        return "location-details";
    }

    // everything edit page needs
    private void showEditForm(Review review, String text, Model model) {
        model.addAttribute("reviewId", review.getId());
        model.addAttribute("locationId", review.getLocationId());
        model.addAttribute("text", text);
        model.addAttribute("maxLength", Review.MAX_TEXT_LENGTH);
    }
}