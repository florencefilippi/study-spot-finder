package com.studyspotfinder.studyspot.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity 
@Table(name = "reviews",
    // only one review per user per location
    uniqueConstraints = @UniqueConstraint(
        name = "uk_reviews_author_location",
        columnNames = {"author_id", "location_id"}),

    // lookup all reviews for spot
    indexes = @Index(name = "idx_reviews_location", columnList = "location_id"))
public class Review {
    
    public static final int MAX_TEXT_LENGTH = 1000;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // id from LocationLookup
    @Column(name = "location_id", nullable = false, length = 100)
    private String locationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "review_text", nullable = false, length = MAX_TEXT_LENGTH)
    private String text;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Empty constructor for JPA
    protected Review() {}

    public Review(String locationId, User author, String text, Instant createdAt) {
        this.locationId = locationId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    // getters
    public Long getId() { return id; }
    public String getLocationId() { return locationId; }
    public User getAuthor() { return author; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}
