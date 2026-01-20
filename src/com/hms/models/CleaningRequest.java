package com.hms.models;

import java.util.Date;

public class CleaningRequest {
    private int requestId;
    private Student student; // Foreign key to Students table
    private String roomNumber;
    private Date preferredDate;
    private Date preferredTime;
    private Date requestDate;
    private String status; // "Pending", "Assigned", "Completed"
    private User assignedTo; // Foreign key to Users table (cleaning staff role)
    
    // Constructors
    public CleaningRequest() {
        this.requestDate = new Date();
        this.status = "Pending";
    }
    
    public CleaningRequest(Student student, String roomNumber, Date preferredDate, Date preferredTime) {
        this.student = student;
        this.roomNumber = roomNumber;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.requestDate = new Date();
        this.status = "Pending";
    }
    
    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public Date getPreferredDate() { return preferredDate; }
    public void setPreferredDate(Date preferredDate) { this.preferredDate = preferredDate; }
    
    public Date getPreferredTime() { return preferredTime; }
    public void setPreferredTime(Date preferredTime) { this.preferredTime = preferredTime; }
    
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo =assignedTo;}
}