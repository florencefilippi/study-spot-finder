package com.studyspotfinder.studyspot.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyspotfinder.studyspot.model.Suggestion;
import com.studyspotfinder.studyspot.model.SuggestionForm;
import com.studyspotfinder.studyspot.model.SuggestionStatus;
import com.studyspotfinder.studyspot.repository.SuggestionRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/suggestions")
public class SuggestionController {

    // same list as ContributorSpotController, so suggested spots filter the same way
    private static final List<String> TYPES = List.of(
            "Cafe", "Library", "Academic & Study Space",
            "Indoor Seating", "Technical Seating", "Social Lounge");

    // a user can have at most this many suggestions waiting for review
    static final int MAX_PENDING = 3;

    // rough box around Metro Vancouver
    private static final double MIN_LAT = 49.0, MAX_LAT = 49.45;
    private static final double MIN_LON = -123.3, MAX_LON = -122.4;

    private final SuggestionRepository suggestions;

    public SuggestionController(SuggestionRepository suggestions) {
        this.suggestions = suggestions;
    }

    @GetMapping("/new")
    public String newForm(Model model, Principal principal) {
        showForm(model, new SuggestionForm(), principal.getName());
        return "suggestion-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("suggestionForm") SuggestionForm form,
                         BindingResult result,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirect) {
        String username = principal.getName();

        // type must be one from the list (someone could edit the HTML)
        if (form.getType() != null && !form.getType().isBlank() && !TYPES.contains(form.getType())) {
            result.rejectValue("type", "type.invalid", "Pick a type from the list");
        }

        // pin must be in Metro Vancouver
        if (form.getLatitude() != null && form.getLongitude() != null && !insideArea(form)) {
            result.rejectValue("latitude", "latitude.outside",
                    "That pin is outside Metro Vancouver. Move it to the spot's location.");
        }

        // spam limit
        if (pendingCount(username) >= MAX_PENDING) {
            result.reject("pending.limit", "You already have " + MAX_PENDING
                    + " suggestions waiting for review. Please wait for an admin to review them.");
        }

        if (result.hasErrors()) {
            showForm(model, form, username);
            return "suggestion-form";
        }

        Suggestion suggestion = new Suggestion(username);
        suggestion.setName(form.getName().trim());
        suggestion.setAddress(form.getAddress().trim());
        suggestion.setType(form.getType());
        suggestion.setLatitude(form.getLatitude());
        suggestion.setLongitude(form.getLongitude());
        suggestion.setOpeningHours(blankToNull(form.getOpeningHours()));
        suggestion.setDescription(blankToNull(form.getDescription()));
        suggestion.setReviewText(form.getReviewText().trim());
        suggestions.save(suggestion);

        redirect.addFlashAttribute("success",
                "Thanks! \"" + suggestion.getName() + "\" was sent to an admin for review.");
        return "redirect:/suggestions/new";
    }

    private long pendingCount(String username) {
        return suggestions.countBySuggestedByAndStatus(username, SuggestionStatus.PENDING);
    }

    private boolean insideArea(SuggestionForm form) {
        return form.getLatitude() >= MIN_LAT && form.getLatitude() <= MAX_LAT
                && form.getLongitude() >= MIN_LON && form.getLongitude() <= MAX_LON;
    }

    // optional fields: save "" as null so the map doesn't show empty lines
    private String blankToNull(String text) {
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private void showForm(Model model, SuggestionForm form, String username) {
        model.addAttribute("suggestionForm", form);
        model.addAttribute("types", TYPES);
        model.addAttribute("pendingCount", pendingCount(username));
        model.addAttribute("maxPending", MAX_PENDING);
    }
}