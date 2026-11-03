package com.studyspotfinder.studyspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class DashboardController {
    
    @GetMapping("/admin")
    public String getAdminPage() {
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
