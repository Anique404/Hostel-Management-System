package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.Room;
import com.hms.models.Hostel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    
    // Add new room
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Rooms (hostel_id, room_number, capacity, current_occupancy, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, room.getHostel().getHostelId());
            pstmt.setString(2, room.getRoomNumber());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setInt(4, room.getCurrentOccupancy());
            pstmt.setString(5, room.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Get available rooms by hostel
    public List<Room> getAvailableRoomsByHostel(int hostelId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, h.hostel_id, h.hostel_name, h.total_rooms " +
                    "FROM Rooms r JOIN Hostels h ON r.hostel_id = h.hostel_id " +
                    "WHERE r.hostel_id = ? AND r.status = 'Available' AND r.current_occupancy < r.capacity";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hostelId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return rooms;
    }
    
    // Update room occupancy
    public boolean updateRoomOccupancy(int roomId, int newOccupancy) {
        String sql = "UPDATE Rooms SET current_occupancy = ?, status = ? WHERE room_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newOccupancy);
            
            // Determine status based on occupancy
            String status = (newOccupancy > 0) ? "Full" : "Available";
            pstmt.setString(2, status);
            pstmt.setInt(3, roomId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Helper method to extract Room from ResultSet
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setCapacity(rs.getInt("capacity"));
        room.setCurrentOccupancy(rs.getInt("current_occupancy"));
        room.setStatus(rs.getString("status"));
        
        // Create and set Hostel object
        Hostel hostel = new Hostel();
        hostel.setHostelId(rs.getInt("hostel_id"));
        hostel.setHostelName(rs.getString("hostel_name"));
        hostel.setTotalRooms(rs.getInt("total_rooms"));
        room.setHostel(hostel);
        
        return room;}
    }