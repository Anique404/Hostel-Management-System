
    package com.hms.models;

import java.util.Date;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role; // "Admin", "Student", "Warden", "Office"
    private Date createdDate;
    
    // Constructors
    public User() {
        this.createdDate = new Date();
    }
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdDate = new Date();
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}