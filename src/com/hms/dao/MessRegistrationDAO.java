package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.MessRegistration;
import com.hms.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessRegistrationDAO {
    private String lastErrorMessage = null;

    public String getLastErrorMessage() { return lastErrorMessage; }

    // Register student for mess
    public boolean registerForMess(MessRegistration registration) {
        String sql = "INSERT INTO MessRegistrations (student_id, meal_type, month_count, " +
                    "start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, registration.getStudent().getStudentId());
            pstmt.setString(2, registration.getMealType());
            pstmt.setInt(3, registration.getMonthCount());
            pstmt.setDate(4, new java.sql.Date(registration.getStartDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(registration.getEndDate().getTime()));
            pstmt.setString(6, registration.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    public List<MessRegistration> getAllMessRegistrations() {
        List<MessRegistration> registrations = new ArrayList<>();
        String query = "SELECT mr.*, s.* FROM MessRegistrations mr " +
                      "JOIN Students s ON mr.student_id = s.student_id " +
                      "ORDER BY mr.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                MessRegistration registration = new MessRegistration();
                registration.setRegistrationId(rs.getInt("registration_id"));
                registration.setMealType(rs.getString("meal_type"));
                registration.setMonthCount(rs.getInt("month_count"));
                registration.setStartDate(rs.getDate("start_date"));
                registration.setEndDate(rs.getDate("end_date"));
                registration.setStatus(rs.getString("status"));
                
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setFullName(rs.getString("full_name"));
                student.setRegistrationNo(rs.getString("registration_no"));
                
                registration.setStudent(student);
                
                registrations.add(registration);
            }
            
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error getting all mess registrations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return registrations;
    }
    
    // Get active mess registrations for student
    public List<MessRegistration> getActiveRegistrationsByStudent(int studentId) {
        List<MessRegistration> registrations = new ArrayList<>();
        String sql = "SELECT mr.*, s.student_id as s_id, s.registration_no, s.full_name " +
                    "FROM MessRegistrations mr " +
                    "JOIN Students s ON mr.student_id = s.student_id " +
                    "WHERE mr.student_id = ? AND mr.status = 'Active'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                registrations.add(extractMessRegistrationFromResultSet(rs));
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return registrations;
    }
    
    // End mess registration
    public boolean endMessRegistration(int registrationId) {
        String sql = "UPDATE MessRegistrations SET status = 'Ended' WHERE registration_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, registrationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Check if student has active mess registration
    public boolean hasActiveMessRegistration(int studentId) {
        String sql = "SELECT COUNT(*) FROM MessRegistrations WHERE student_id = ? AND status = 'Active'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return false;
    }
    
    // Helper method to extract MessRegistration from ResultSet
    private MessRegistration extractMessRegistrationFromResultSet(ResultSet rs) throws SQLException {
        MessRegistration registration = new MessRegistration();
        registration.setRegistrationId(rs.getInt("registration_id"));
        registration.setMealType(rs.getString("meal_type"));
        registration.setMonthCount(rs.getInt("month_count"));
        registration.setStartDate(rs.getDate("start_date"));
        registration.setEndDate(rs.getDate("end_date"));
        registration.setStatus(rs.getString("status"));
        
        // Create and set Student object
        Student student = new Student();
        student.setStudentId(rs.getInt("s_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setFullName(rs.getString("full_name"));
        registration.setStudent(student);
        
        return registration;
    }
}