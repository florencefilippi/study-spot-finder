package com.studyspotfinder.studyspot.config;

import com.studyspotfinder.studyspot.repository.UserRepository;
import com.studyspotfinder.studyspot.repository.SavedSpotRepository;
import com.studyspotfinder.studyspot.model.User;
import com.studyspotfinder.studyspot.model.Role;
import com.studyspotfinder.studyspot.model.SavedSpot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration 
public class UserSeeder {
    private final PasswordEncoder passwordEncoder;

    UserSeeder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner seedUsers(UserRepository users, SavedSpotRepository savedSpots) {
        return args -> {
            if(users.findByUsername("testadmin").isEmpty()){
                users.save(new User("testadmin", passwordEncoder.encode("testadminpassword"), Role.ADMIN));
            }
            if(users.findByUsername("testcontributor").isEmpty()){
                users.save(new User("testcontributor", passwordEncoder.encode("testcontributorpassword"), Role.CONTRIBUTOR));
            }
            if(users.findByUsername("testuser").isEmpty()){
                users.save(new User("testuser", passwordEncoder.encode("testuserpassword"), Role.USER));
            }

            User testuser = users.findByUsername("testuser").orElseThrow();

            if(!savedSpots.existsByUserAndSpotId(testuser, "sfu/mackenzie-cafe")){
                savedSpots.save(new SavedSpot(testuser, "SFU Mackenzie Cafe", "sfu/mackenzie-cafe"));
            }
        };
    }
}
