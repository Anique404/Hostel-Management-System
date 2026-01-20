package com.hms.models;

import java.util.Date;
import java.util.Calendar;

public class MessRegistration {
    private int registrationId;
    private Student student; // Foreign key to Students table
    private String mealType; // "Breakfast", "Lunch", "Dinner", "FullDay"
    private int monthCount;
    private Date startDate;
    private Date endDate;
    private String status; // "Active", "Ended"
    
    // Constructors
    public MessRegistration() {
        this.status = "Active";
    }
    
    public MessRegistration(Student student, String mealType, int monthCount, Date startDate) {
        this.student = student;
        this.mealType = mealType;
        this.monthCount = monthCount;
        this.startDate = startDate;
        this.status = "Active";
        
        // Calculate end date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, monthCount);
        this.endDate = calendar.getTime();
    }
    
    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    
    public int getMonthCount() { return monthCount; }
    public void setMonthCount(int monthCount) { this.monthCount = monthCount; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) {this.status=status;}
}