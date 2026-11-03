package com.studyspotfinder.studyspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller tells Spring this class handles web page requests
@Controller
public class PageController {

    // When someone visits "/", show templates/home.html
    //returned text is the file name without ".html"
    @GetMapping("/")
    public String home() {
        return "landing";
    }

    // When someone visits "/map", show templates/map.html
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    // When someone visits "/profile", show templates/profile.html
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}