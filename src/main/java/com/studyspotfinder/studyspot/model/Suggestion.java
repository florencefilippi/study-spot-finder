package com.studyspotfinder.studyspot.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// A study spot a user suggested. Once an admin approves it, it shows on the map.
@Entity
@Table(name = "suggestions")
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    // from the pin on the form's map
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    // one of the same types contributors pick from (Cafe, Library, ...)
    @Column(nullable = false)
    private String type;

    private String openingHours;

    @Column(length = 1000)
    private String description;

    // the suggester's first review of the place
    @Column(length = 1000)
    private String reviewText;

    // username of whoever suggested it
    @Column(nullable = false)
    private String suggestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionStatus status = SuggestionStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // filled in when an admin rejects it
    @Column(length = 500)
    private String rejectionReason;

    // Empty constructor for JPA
    protected Suggestion() {}

    public Suggestion(String suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getSuggestedBy() { return suggestedBy; }

    public SuggestionStatus getStatus() { return status; }
    public void setStatus(SuggestionStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}