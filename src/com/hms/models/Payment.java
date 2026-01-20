package com.hms.models;

import java.util.Date;

public class Payment {
    private int paymentId;
    private FeeChallan challan; // Foreign key to FeeChallan table
    private Date paymentDate;
    private String paymentMethod; // "Cash", "Bank Transfer", "Online"
    private String transactionId; // Bank reference/transaction ID
    private double paidAmount;
    private User receivedBy; // Foreign key to Users table (office role)
    
    // Constructors
    public Payment() {
        this.paymentDate = new Date();
    }
    
    public Payment(FeeChallan challan, String paymentMethod, double paidAmount, User receivedBy) {
        this.challan = challan;
        this.paymentMethod = paymentMethod;
        this.paidAmount = paidAmount;
        this.receivedBy = receivedBy;
        this.paymentDate = new Date();
    }
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public FeeChallan getChallan() { return challan; }
    public void setChallan(FeeChallan challan) { this.challan = challan; }
    
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    
    public User getReceivedBy() { return receivedBy; }
    public void setReceivedBy(User receivedBy) { this.receivedBy =receivedBy;}
}