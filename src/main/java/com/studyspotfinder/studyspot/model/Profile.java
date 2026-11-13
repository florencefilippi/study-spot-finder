package com.studyspotfinder.studyspot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity 
@Table(name = "profile")
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String name;
    private String school;
    private String major;

    // Empty constructor for Jpa
    protected Profile(){}

    public Profile(User user){
        this.user = user;
    }

    // Setters + Getters
    public String getName(){
        return name;
    }
    public String getSchool(){
        return school;
    }
    public String getMajor(){
        return major;
    }
    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
    
    public void setName(String name){
        this.name = name;
    }
    public void setSchool(String school){
        this.school = school;
    }
    public void setMajor(String major){
        this.major = major;
    }
}
