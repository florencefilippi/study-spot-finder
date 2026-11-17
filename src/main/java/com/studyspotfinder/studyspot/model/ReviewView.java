package com.studyspotfinder.studyspot.model;

// what details page show for one review
// template never touches review entity so lazy loading cant fall in temlate
public record ReviewView(String author, String text, String postedOn) {}
