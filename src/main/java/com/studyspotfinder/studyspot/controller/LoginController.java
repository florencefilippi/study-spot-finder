package com.studyspotfinder.studyspot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {
    
    @GetMapping ("/login")
    public String login(Authentication authentication){

        if(authentication != null){
            return "redirect:/home";
        }
        return "login";
    }
}
