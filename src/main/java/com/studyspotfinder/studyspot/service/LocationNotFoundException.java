package com.studyspotfinder.studyspot.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// so methods dont need throws
@ResponseStatus(HttpStatus.NOT_FOUND) // makes spring answer 404 instead of 500
public class LocationNotFoundException extends RuntimeException {
    
    public LocationNotFoundException(String id) {
        super("Spot not found: " + id);
    }
}
