package com.studyspotfinder.studyspot.model;

// what details page show for one review
// mine = logged in user wrote it. for what buttons are shown on review
// template never touches review entity so lazy loading cant fall in temlate
public record ReviewView(Long id, String author, String text, String postedOn, boolean mine) {}
