package com.studyspotfinder.studyspot.controller;
import com.studyspotfinder.studyspot.model.Suggestion;
import com.studyspotfinder.studyspot.model.SuggestionStatus;
import com.studyspotfinder.studyspot.repository.SuggestionRepository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.studyspotfinder.studyspot.repository.ProfileRepository;
import com.studyspotfinder.studyspot.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.studyspotfinder.studyspot.model.Role;
import com.studyspotfinder.studyspot.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SuggestionRepository suggestionRepository;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH).withZone(ZoneId.of("America/Vancouver"));

    public AdminController(UserRepository userRepository, ProfileRepository profileRepository, SuggestionRepository suggestionRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        model.addAttribute("users", userRepository.findAll(Sort.by("username")));
        model.addAttribute("roles", Role.values());
        model.addAttribute("currentUsername", principal.getName());
        return "admin-users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role, Principal principal, RedirectAttributes redirect) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            redirect.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        User user = found.get();
        if (user.getUsername().equals(principal.getName())) {
            redirect.addFlashAttribute("error", "You can't change your own role.");
            return "redirect:/admin/users";
        }

        user.setRole(role);
        userRepository.save(user);
        redirect.addFlashAttribute("success", "Changed " + user.getUsername() + "'s role to " + role + ".");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                         Principal principal,
                         RedirectAttributes redirect) {

        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            redirect.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        User user = found.get();
        if (user.getUsername().equals(principal.getName())) {
            redirect.addFlashAttribute("error", "You can't delete your own account.");
            return "redirect:/admin/users";
        }

        profileRepository.findByUser(user).ifPresent(profileRepository::delete);
        userRepository.delete(user);
        redirect.addFlashAttribute("success", "Deleted user " + user.getUsername() + ".");
        return "redirect:/admin/users";
    }

    @GetMapping("/suggestions")
    public String listSuggestions(Model model) {
        model.addAttribute("suggestions",
                suggestionRepository.findByStatusOrderByCreatedAtAsc(SuggestionStatus.PENDING));
        model.addAttribute("dateFormat", DATE_FORMAT);
        return "admin-suggestions";
    }

    @PostMapping("/suggestions/{id}/approve")
    public String approveSuggestion(@PathVariable Long id, RedirectAttributes redirect) {
        Optional<Suggestion> found = findPending(id);
        if (found.isEmpty()) {
            redirect.addFlashAttribute("error", "That suggestion doesn't exist or was already reviewed.");
            return "redirect:/admin/suggestions";
        }

        Suggestion suggestion = found.get();
        suggestion.setStatus(SuggestionStatus.APPROVED);
        suggestionRepository.save(suggestion);
        redirect.addFlashAttribute("success", "Approved \"" + suggestion.getName() + "\".");
        return "redirect:/admin/suggestions";
    }

    @PostMapping("/suggestions/{id}/reject")
    public String rejectSuggestion(@PathVariable Long id,
                                   @RequestParam(defaultValue = "") String reason,
                                   RedirectAttributes redirect) {
        Optional<Suggestion> found = findPending(id);
        if (found.isEmpty()) {
            redirect.addFlashAttribute("error", "That suggestion doesn't exist or was already reviewed.");
            return "redirect:/admin/suggestions";
        }

        String trimmed = reason.trim();
        if (trimmed.length() > 500) {
            redirect.addFlashAttribute("error", "The reason must be 500 characters or less.");
            return "redirect:/admin/suggestions";
        }

        Suggestion suggestion = found.get();
        suggestion.setStatus(SuggestionStatus.REJECTED);
        suggestion.setRejectionReason(trimmed.isEmpty() ? null : trimmed);
        suggestionRepository.save(suggestion);
        redirect.addFlashAttribute("success", "Rejected \"" + suggestion.getName() + "\".");
        return "redirect:/admin/suggestions";
    }

    // only PENDING suggestions can be approved or rejected
    private Optional<Suggestion> findPending(Long id) {
        return suggestionRepository.findById(id)
                .filter(s -> s.getStatus() == SuggestionStatus.PENDING);
    }
}