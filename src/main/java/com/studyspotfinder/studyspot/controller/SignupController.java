package com.studyspotfinder.studyspot.controller;

import com.studyspotfinder.studyspot.model.Role;
import com.studyspotfinder.studyspot.model.User;
import com.studyspotfinder.studyspot.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam String role,
                         Model model) {

        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return error(model, username, "Please fill in all fields.");
        }
        if (password.length() < 8) {
            return error(model, username, "Password must be at least 8 characters.");
        }
        if (!password.equals(confirmPassword)) {
            return error(model, username, "Passwords do not match.");
        }
        if (userRepository.existsByUsername(username)) {
            return error(model, username, "Username already exists.");
        }
        // Validates the role selection
        if(!role.equals("USER") && !role.equals("CONTRIBUTOR")){
            return error(model, username, "Invalid Role");
        }

        Role roleType = Role.valueOf(role);
        

        userRepository.save(new User(username, passwordEncoder.encode(password), roleType));

        return "redirect:/login?registered";
    }

    private String error(Model model, String username, String message) {
        model.addAttribute("error", message);
        model.addAttribute("username", username);
        return "signup";
    }
}