package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.Hostel;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HostelDAO {
    
    // Add new hostel
    public boolean addHostel(Hostel hostel) {
        String sql = "INSERT INTO Hostels (hostel_name, total_rooms, warden_id) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hostel.getHostelName());
            pstmt.setInt(2, hostel.getTotalRooms());
            pstmt.setInt(3, hostel.getWarden().getUserId());
            
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
    
    // Get all hostels
    public List<Hostel> getAllHostels() {
        List<Hostel> hostels = new ArrayList<>();
        String sql = "SELECT h.*, u.user_id, u.username, u.role FROM Hostels h " +
                    "JOIN Users u ON h.warden_id = u.user_id";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                hostels.add(extractHostelFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        return hostels;
    }
    
    // Get hostel by ID
    public Hostel getHostelById(int hostelId) {
        String sql = "SELECT h.*, u.user_id, u.username, u.role FROM Hostels h " +
                    "JOIN Users u ON h.warden_id = u.user_id WHERE h.hostel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hostelId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractHostelFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return null;
    }
    
    // Helper method to extract Hostel from ResultSet
    private Hostel extractHostelFromResultSet(ResultSet rs) throws SQLException {
        Hostel hostel = new Hostel();
        hostel.setHostelId(rs.getInt("hostel_id"));
        hostel.setHostelName(rs.getString("hostel_name"));
        hostel.setTotalRooms(rs.getInt("total_rooms"));
        
        // Create and set Warden User object
        User warden = new User();
        warden.setUserId(rs.getInt("user_id"));
        warden.setUsername(rs.getString("username"));
        warden.setRole(rs.getString("role"));
        hostel.setWarden(warden);
        
        return hostel;}
    }

