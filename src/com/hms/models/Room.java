package com.hms.models;

public class Room {
    private int roomId;
    private Hostel hostel; // Foreign key to Hostels table
    private String roomNumber;
    private int capacity;
    private int currentOccupancy;
    private String status; // "Available", "Full", "Maintenance"
    
    // Constructors
    public Room() {
        this.currentOccupancy = 0;
        this.status = "Available";
    }
    
    public Room(Hostel hostel, String roomNumber, int capacity) {
        this.hostel = hostel;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.currentOccupancy = 0;
        this.status = "Available";
    }
    
    // Business logic method
    public boolean hasAvailableSpace() {
        return currentOccupancy < capacity && status.equals("Available");
    }
    
    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) {this.status=status;}
}