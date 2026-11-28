package com.studyspotfinder.studyspot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// What the user typed into the Suggest a Spot form
public class SuggestionForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be 100 characters or less")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must be 200 characters or less")
    private String address;

    @NotBlank(message = "Pick a type")
    private String type;

    // filled in by the pin on the map (hidden inputs)
    @NotNull(message = "Place the pin on the map")
    private Double latitude;

    @NotNull(message = "Place the pin on the map")
    private Double longitude;

    @Size(max = 100, message = "Hours must be 100 characters or less")
    private String openingHours;

    @Size(max = 1000, message = "Description must be 1000 characters or less")
    private String description;

    @NotBlank(message = "Please write a short review of the spot")
    @Size(max = 1000, message = "Review must be 1000 characters or less")
    private String reviewText;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}