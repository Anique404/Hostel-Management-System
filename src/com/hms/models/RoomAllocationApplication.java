package com.hms.models;

import java.util.Date;

public class RoomAllocationApplication {
    private int applicationId;
    private Student student; // Foreign key to Students table
    private Hostel hostel; // Foreign key to Hostels table
    private Date applicationDate;
    private String status; // "Pending", "OfficeVerified", "AdminApproved", "Rejected"
    private User verifiedByOffice; // Foreign key to Users table (office role)
    private Date verifiedDate;
    private Room allocatedRoom; // Foreign key to Rooms table
    
    // Constructors
    public RoomAllocationApplication() {
        this.applicationDate = new Date();
        this.status = "Pending";
    }
    
    public RoomAllocationApplication(Student student, Hostel hostel) {
        this.student = student;
        this.hostel = hostel;
        this.applicationDate = new Date();
        this.status = "Pending";
    }
    
    // Getters and Setters
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }
    
    public Date getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Date applicationDate) { this.applicationDate = applicationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public User getVerifiedByOffice() { return verifiedByOffice; }
    public void setVerifiedByOffice(User verifiedByOffice) { this.verifiedByOffice = verifiedByOffice; }
    
    public Date getVerifiedDate() { return verifiedDate; }
    public void setVerifiedDate(Date verifiedDate) { this.verifiedDate = verifiedDate; }
    
    public Room getAllocatedRoom() { return allocatedRoom; }
    public void setAllocatedRoom(Room allocatedRoom) { this.allocatedRoom = allocatedRoom;}
}