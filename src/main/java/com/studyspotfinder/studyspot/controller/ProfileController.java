package com.studyspotfinder.studyspot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.studyspotfinder.studyspot.repository.ProfileRepository;
import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.model.Profile;
import com.studyspotfinder.studyspot.model.User;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller 
public class ProfileController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // Constructor that assigns the UserRepository and ProfileRepository together
    public ProfileController(UserRepository userRepository, ProfileRepository profileRepository){
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // Helper function that returns which users are logged-in
    private User currentUser(Authentication authentication){
        String username = authentication.getName();

        return userRepository.findByUsername(username).orElseThrow();
    }
    
    @GetMapping("/profile")
    public String getProfile(Authentication authentication, Model model) {
        // Get the logged-in user
        User loggedinUser = currentUser(authentication);

        Optional<Profile> profileOptional = profileRepository.findByUser(loggedinUser);
        boolean firstTime = !profileOptional.isPresent();
        Profile profile = profileOptional.orElseGet(() -> new Profile(loggedinUser));

        model.addAttribute("profile", profile);
        model.addAttribute("firstTime", firstTime);

        return "profile";
    }

       @PostMapping("/profile")
    public String postProfile(Authentication authentication, Model model,
                              @RequestParam String name,
                              @RequestParam(required = false) String school,
                              @RequestParam(required = false) String major,
                              @RequestParam(required = false) String company,
                              @RequestParam(required = false) String locationName) {
        User loggedinUser = currentUser(authentication);

        Optional<Profile> existing = profileRepository.findByUser(loggedinUser);

        boolean firstTime = !existing.isPresent();

        Profile profile = existing.orElseGet(() -> new Profile(loggedinUser));

        profile.setName(name);
        if (school != null) profile.setSchool(school);
        if (major != null) profile.setMajor(major);
        if (company != null) profile.setCompany(company);
        if (locationName != null) profile.setLocationName(locationName);
        profileRepository.save(profile);

        if(firstTime){
            return "redirect:/home";
        } else {
            return "redirect:/profile?saved";
        }
    }
}
