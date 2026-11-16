package com.studyspotfinder.studyspot.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.studyspotfinder.studyspot.model.Spot;
import com.studyspotfinder.studyspot.model.SpotForm;
import com.studyspotfinder.studyspot.repository.SpotRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/contributor/spots")
public class ContributorSpotController {

    private static final List<String> TYPES = List.of(
            "Cafe", "Library", "Academic & Study Space",
            "Indoor Seating", "Technical Seating", "Social Lounge");

    private final SpotRepository spots;

    public ContributorSpotController(SpotRepository spots) {
        this.spots = spots;
    }

    @GetMapping
    public String list(Principal principal, Model model) {
        model.addAttribute("spots", spots.findByOwnerUsername(principal.getName()));
        return "contributor-spots";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        showForm(model, new SpotForm(), "/contributor/spots", "Add a study spot");
        return "spot-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("spotForm") SpotForm form, BindingResult result,
                         Principal principal, Model model) {
        checkType(form, result);
        if (result.hasErrors()) {
            showForm(model, form, "/contributor/spots", "Add a study spot");
            return "spot-form";
        }
        Spot spot = new Spot(principal.getName()); // the logged-in user becomes the owner
        copyInto(form, spot);
        spots.save(spot);
        return "redirect:/contributor/spots";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Principal principal, Model model) {
        Spot spot = ownedSpot(id, principal);
        SpotForm form = new SpotForm();
        form.setName(spot.getName());
        form.setType(spot.getType());
        form.setLatitude(spot.getLatitude());
        form.setLongitude(spot.getLongitude());
        form.setDescription(spot.getDescription());
        form.setOpeningHours(spot.getOpeningHours());
        showForm(model, form, "/contributor/spots/" + id, "Edit study spot");
        return "spot-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("spotForm") SpotForm form,
                         BindingResult result, Principal principal, Model model) {
        Spot spot = ownedSpot(id, principal);
        checkType(form, result);
        if (result.hasErrors()) {
            showForm(model, form, "/contributor/spots/" + id, "Edit study spot");
            return "spot-form";
        }
        copyInto(form, spot);
        spots.save(spot);
        return "redirect:/contributor/spots";
    }

    private Spot ownedSpot(Long id, Principal principal) {
        Spot spot = spots.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!spot.getOwnerUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN); // not your spot
        }
        return spot;
    }

    private void checkType(SpotForm form, BindingResult result) {
        if (form.getType() != null && !form.getType().isBlank() && !TYPES.contains(form.getType())) {
            result.rejectValue("type", "type.invalid", "Pick a type from the list");
        }
    }

    private void copyInto(SpotForm form, Spot spot) {
        spot.setName(form.getName().trim());
        spot.setType(form.getType());
        spot.setLatitude(form.getLatitude());
        spot.setLongitude(form.getLongitude());
        spot.setDescription(form.getDescription());
        spot.setOpeningHours(form.getOpeningHours());
    }

    private void showForm(Model model, SpotForm form, String action, String heading) {
        model.addAttribute("spotForm", form);
        model.addAttribute("types", TYPES);
        model.addAttribute("formAction", action);
        model.addAttribute("heading", heading);
    }
}