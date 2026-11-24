package com.studyspotfinder.studyspot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity 
@Table(name = "saved_spots")
public class SavedSpot {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected SavedSpot() {};

    @Column(nullable = false)
    private String spotName;

    @Column(nullable = false)
    private String spotId;

    public SavedSpot(User user, String spotName, String spotId){
        this.user = user;
        this.spotName = spotName;
        this.spotId = spotId;
    }

    // Getters (We don't need to ability to set these variables)
    public User getUser(){
        return user;
    }
    public String getSpotName(){
        return spotName;
    }
    public String getSpotId(){
        return spotId;
    }
    public Long getId(){
        return id;
    }
}
