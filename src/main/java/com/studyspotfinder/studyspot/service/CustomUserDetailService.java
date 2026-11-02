package com.studyspotfinder.studyspot.service;

import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.model.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service 
public class CustomUserDetailService implements UserDetailsService {
    
    private final UserRepository users;

    public CustomUserDetailService(UserRepository users){
        this.users = users;
    }

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = users.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("No username: " + username));

        return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
     }
}
