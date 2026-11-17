package com.studyspotfinder.studyspot.service;

// when review breaks a rule
public class ReviewValidationException extends RuntimeException {
    
    public ReviewValidationException(String message) {
        super(message);
    }
}
