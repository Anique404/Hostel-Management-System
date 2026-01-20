package com.hms.models;

import java.util.Date;

public class FeeChallan {
    private int challanId;
    private int studentId;
    private Student student;
    private String feeType; // "HOSTEL_FEE" or "MESS_FEE"
    private double amount;
    private Date issueDate;
    private Date dueDate;
    private String status; // "PENDING", "PAID", "VERIFIED"
    private Date paymentDate;
    private String transactionId;
    private String academicYear;
    private Date createdAt;
    
    // Constructors
    public FeeChallan() {}
    
    public FeeChallan(int studentId, String feeType, double amount, Date issueDate, Date dueDate, String academicYear) {
        this.studentId = studentId;
        this.feeType = feeType;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = "PENDING";
        this.academicYear = academicYear;
        this.createdAt = new Date();
    }
    
    // Getters and Setters
    public int getChallanId() {
        return challanId;
    }
    
    public void setChallanId(int challanId) {
        this.challanId = challanId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
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
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    // Additional method for month_year (legacy support)
    public String getMonthYear() {
        if (issueDate != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM-yyyy");
            return sdf.format(issueDate);
        }
        return "";
    }
    
    public void setMonthYear(String monthYear) {
        // For backward compatibility
    }
}