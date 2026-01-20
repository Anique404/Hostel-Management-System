package com.hms.models;

import java.util.Date;

public class Complaint {
    private int complaintId;
    private Student student; // Foreign key to Students table
    private String complaintType; // "Electricity", "Plumbing", "Cleaning", "Furniture", "Other"
    private String description;
    private Date complaintDate;
    private String status; // "Pending", "InProgress", "Resolved", "Rejected"
    private User resolvedBy; // Foreign key to Users table (warden role)
    private Date resolvedDate;
    private String location; // New field: Where the complaint occurred
    private String priority; // New field: "Low", "Medium", "High", "Urgent"
    private String resolutionNotes; // New field: Notes when complaint is resolved
    
    // Constructors
    public Complaint() {
        this.complaintDate = new Date();
        this.status = "Pending";
        this.priority = "Medium";
    }
    
    public Complaint(Student student, String complaintType, String description) {
        this.student = student;
        this.complaintType = complaintType;
        this.description = description;
        this.complaintDate = new Date();
        this.status = "Pending";
        this.priority = "Medium";
    }
    
    public Complaint(Student student, String complaintType, String description, String location, String priority) {
        this.student = student;
        this.complaintType = complaintType;
        this.description = description;
        this.location = location;
        this.priority = priority;
        this.complaintDate = new Date();
        this.status = "Pending";
    }
    
    // Getters and Setters
    public int getComplaintId() { 
        return complaintId; 
    }
    
    public void setComplaintId(int complaintId) { 
        this.complaintId = complaintId; 
    }
    
    public Student getStudent() { 
        return student; 
    }
    
    public void setStudent(Student student) { 
        this.student = student; 
    }
    
    public String getComplaintType() { 
        return complaintType; 
    }
    
    public void setComplaintType(String complaintType) { 
        this.complaintType = complaintType; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public Date getComplaintDate() { 
        return complaintDate; 
    }
    
    public void setComplaintDate(Date complaintDate) { 
        this.complaintDate = complaintDate; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public User getResolvedBy() { 
        return resolvedBy; 
    }
    
    public void setResolvedBy(User resolvedBy) { 
        this.resolvedBy = resolvedBy; 
    }
    
    public Date getResolvedDate() { 
        return resolvedDate; 
    }
    
    public void setResolvedDate(Date resolvedDate) { 
        this.resolvedDate = resolvedDate;
    }
    
    // New fields getters and setters
    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location; 
    }
    
    public String getPriority() { 
        return priority; 
    }
    
    public void setPriority(String priority) { 
        this.priority = priority; 
    }
    
    public String getResolutionNotes() { 
        return resolutionNotes; 
    }
    
    public void setResolutionNotes(String resolutionNotes) { 
        this.resolutionNotes = resolutionNotes; 
    }
    
    // Helper methods
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }
    
    public boolean isResolved() {
        return "Resolved".equalsIgnoreCase(status);
    }
    
    public boolean isRejected() {
        return "Rejected".equalsIgnoreCase(status);
    }
    
    public boolean isInProgress() {
        return "InProgress".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "Complaint [ID=" + complaintId + 
               ", Type=" + complaintType + 
               ", Status=" + status + 
               ", Student=" + (student != null ? student.getFullName() : "N/A") + 
               ", Date=" + complaintDate + "]";
    }
}