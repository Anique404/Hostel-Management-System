package com.hms.models;

import java.util.Date;

public class FeeStructure {
    private int feeStructureId;
    private String feeType; // "HOSTEL_FEE" or "MESS_FEE"
    private double amount;
    private String academicYear;
    private String description;
    private boolean active;
    private Date effectiveFrom;
    private Date createdAt;
    
    // Constructors
    public FeeStructure() {
        this.active = true;
        this.effectiveFrom = new Date();
        this.createdAt = new Date();
    }
    
    public FeeStructure(String feeType, double amount, String academicYear, String description) {
        this();
        this.feeType = feeType;
        this.amount = amount;
        this.academicYear = academicYear;
        this.description = description;
    }
    
    // Getters and Setters (same as before)
    public int getFeeStructureId() {
        return feeStructureId;
    }
    
    public void setFeeStructureId(int feeStructureId) {
        this.feeStructureId = feeStructureId;
    }
    
    public String getFeeType() {
        return feeType;
    }
    
    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }
    
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "FeeStructure{" +
                "feeStructureId=" + feeStructureId +
                ", feeType='" + feeType + '\'' +
                ", amount=" + amount +
                ", academicYear='" + academicYear + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}