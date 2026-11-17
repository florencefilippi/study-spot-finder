package com.studyspotfinder.studyspot.service;

// so methods dont need throws
public class LocationNotFoundException extends RuntimeException {
    
    public  LocationNotFoundException(String id) {
        super("Spot not found: " + id);
    }
}
