package com.studyspotfinder.studyspot.controller;

import com.studyspotfinder.studyspot.repository.SavedSpotRepository;
import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.model.User;
import com.studyspotfinder.studyspot.model.SavedSpot;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller 
public class SavedSpotController {
    
    private final SavedSpotRepository savedSpotRepository;
    private final UserRepository userRepository;

    // Constructor that assigns the SavedSpotRepository and UserRepository together
    public SavedSpotController(SavedSpotRepository savedSpotRepository, UserRepository userRepository){
        this.savedSpotRepository = savedSpotRepository;
        this.userRepository = userRepository;
    }

    // Helper function that returns which users are logged-in
    private User currentUser(Authentication authentication){
        String username = authentication.getName();

        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping("/saved")
    public String getSavedSpots(Authentication authentication, Model model) {
        User loggedinUser = currentUser(authentication);

        List<SavedSpot> savedSpots = savedSpotRepository.findByUser(loggedinUser);

        model.addAttribute("savedSpots", savedSpots);

        return "saved";
    }
    
    // Get Mapping: used to get a list of the locations saved by the user
    @GetMapping("/api/saved")
    @ResponseBody
    public List<String> getSavedSpotsId(Authentication authentication) {
        User loggedinUser = currentUser(authentication);

        List<SavedSpot> savedSpots = savedSpotRepository.findByUser(loggedinUser);

        List<String> ids = new ArrayList<>();
        for(SavedSpot s : savedSpots){
            ids.add(s.getSpotId());
        }

        return ids;
    }
    

    @PostMapping("/saved")
    @ResponseBody
    public String postSavedSpots(Authentication authentication, @RequestParam String spotId, @RequestParam String spotName) {
        
        User loggedinUser = currentUser(authentication);

        if(!savedSpotRepository.existsByUserAndSpotId(loggedinUser, spotId)){
            savedSpotRepository.save(new SavedSpot(loggedinUser, spotName, spotId));
            return "saved";
        }
        return "already saved";
    }
    
    
}