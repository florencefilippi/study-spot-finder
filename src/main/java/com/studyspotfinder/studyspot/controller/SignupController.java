package com.studyspotfinder.studyspot.controller;

import com.studyspotfinder.studyspot.model.Role;
import com.studyspotfinder.studyspot.model.User;
import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.service.CustomUserDetailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService userDetailService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailService userDetailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailService = userDetailService;
    }

    // When a user signs-up, we need to manually log them in (no need to separately login after 
    // signing-up)
    private void loginAutomatically(String username, HttpServletRequest request, HttpServletResponse response){
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);
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
                         Model model,
                        HttpServletRequest request, 
                    HttpServletResponse response) {

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

        // Automatically log in the user after a successful sign-up
        loginAutomatically(username, request, response);
        return "redirect:/home";
    }

    private String error(Model model, String username, String message) {
        model.addAttribute("error", message);
        model.addAttribute("username", username);
        return "signup";
    }
}