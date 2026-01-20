package com.hms.models;

public class Hostel {
    private int hostelId;
    private String hostelName;
    private int totalRooms;
    private User warden; // Foreign key to Users table (warden role)
    
    // Constructors
    public Hostel() {}
    
    public Hostel(String hostelName, int totalRooms, User warden) {
        this.hostelName = hostelName;
        this.totalRooms = totalRooms;
        this.warden = warden;
    }
    
    // Getters and Setters
    public int getHostelId() { return hostelId; }
    public void setHostelId(int hostelId) { this.hostelId = hostelId; }
    
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
    
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    
    public User getWarden() { return warden; }
    public void setWarden(User warden) {this.warden=warden;}
}