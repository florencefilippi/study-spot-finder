package com.studyspotfinder.studyspot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class LoginController {
    
    @GetMapping ("/login")
    public String login(Authentication authentication, Model model){

        if(authentication != null){
            model.addAttribute("message", "Welcome back, " + authentication);
            return "redirect:/home";
        }
        else{
            model.addAttribute("message", "Welcome! Please log in.");
        }
        return "login";
    }
}
