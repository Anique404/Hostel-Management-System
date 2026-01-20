package com.hms.models;

import java.util.Date;

public class Student {
    private int studentId;
    private User user; // Foreign key to Users table
    private String registrationNo;
    private String fullName;
    private String fatherName;
    private String cnic;
    private String address;
    private String department;
    private String semester;
    private Date admissionDate;
    private String status; // "Applied", "Approved", "Active", "Left"
    
    // Constructors
    public Student() {}
    
    public Student(User user, String registrationNo, String fullName, String fatherName, 
                   String cnic, String address, String department, String semester) {
        this.user = user;
        this.registrationNo = registrationNo;
        this.fullName = fullName;
        this.fatherName = fatherName;
        this.cnic = cnic;
        this.address = address;
        this.department = department;
        this.semester = semester;
        this.admissionDate = new Date();
        this.status = "Applied";
    }
    
    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    
    public String getCnic() { return cnic; }
    public void setCnic(String cnic) { this.cnic = cnic; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public Date getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(Date admissionDate) { this.admissionDate = admissionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status)  {this.status=status;}
}