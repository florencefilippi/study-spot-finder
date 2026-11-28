package com.studyspotfinder.studyspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyspotfinder.studyspot.model.SuggestionStatus;
import com.studyspotfinder.studyspot.repository.SuggestionRepository;

@Controller 
public class DashboardController {

    private final SuggestionRepository suggestionRepository;

    public DashboardController(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }
    
    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        model.addAttribute("pendingSuggestions", suggestionRepository.countByStatus(SuggestionStatus.PENDING));
        return "admin";
    }
    
    @GetMapping("/contributor")
    public String getContributorPage() {
        return "contributor";
    }
    
    @GetMapping("/home")
    public String getHomePage() {
        return "home";
    }  
}