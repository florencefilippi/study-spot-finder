package com.studyspotfinder.studyspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller tells Spring this class handles web page requests
@Controller
public class PageController {

    // When someone visits "/", sends them to the home page "/home"
    //returned text is the file name without ".html"
    @GetMapping("/")
    public String home() {
        return "redirect:home";
    }

    // When someone visits "/map", show templates/map.html
    @GetMapping("/map")
    public String map() {
        return "map";
    }
}