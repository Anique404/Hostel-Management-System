package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.CleaningRequest;
import com.hms.models.Student;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CleaningRequestDAO {
    private String lastErrorMessage = null;

    public String getLastErrorMessage() { return lastErrorMessage; }

    // Submit cleaning request
    public boolean submitCleaningRequest(CleaningRequest request) {
        String sql = "INSERT INTO Cleaning_Requests (student_id, room_number, preferred_date, " +
                    "preferred_time, request_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, request.getStudent().getStudentId());
            pstmt.setString(2, request.getRoomNumber());
            pstmt.setDate(3, new java.sql.Date(request.getPreferredDate().getTime()));
            pstmt.setTime(4, new Time(request.getPreferredTime().getTime()));
            pstmt.setTimestamp(5, new Timestamp(request.getRequestDate().getTime()));
            pstmt.setString(6, request.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Get cleaning requests by student
    public List<CleaningRequest> getRequestsByStudent(int studentId) {
        List<CleaningRequest> requests = new ArrayList<>();
        String sql = "SELECT cr.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as assigned_user_id, u.username as assigned_user_name " +
                    "FROM Cleaning_Requests cr " +
                    "JOIN Students s ON cr.student_id = s.student_id " +
                    "LEFT JOIN Users u ON cr.assigned_to = u.user_id " +
                    "WHERE cr.student_id = ? ORDER BY cr.preferred_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(extractCleaningRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return requests;
    }
    
    // Get pending cleaning requests
    public List<CleaningRequest> getPendingCleaningRequests() {
        List<CleaningRequest> requests = new ArrayList<>();
        String sql = "SELECT cr.*, s.student_id as s_id, s.registration_no, s.full_name " +
                    "FROM Cleaning_Requests cr " +
                    "JOIN Students s ON cr.student_id = s.student_id " +
                    "WHERE cr.status = 'Pending' ORDER BY cr.preferred_date";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                requests.add(extractCleaningRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        return requests;
    }
    
    // Assign cleaning request to staff
    public boolean assignCleaningRequest(int requestId, int staffId) {
        String sql = "UPDATE Cleaning_Requests SET status = 'Assigned', assigned_to = ? " +
                    "WHERE request_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, staffId);
            pstmt.setInt(2, requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Mark cleaning request as completed
    public boolean markRequestAsCompleted(int requestId) {
        String sql = "UPDATE Cleaning_Requests SET status = 'Completed' WHERE request_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Helper method to extract CleaningRequest from ResultSet
    private CleaningRequest extractCleaningRequestFromResultSet(ResultSet rs) throws SQLException {
        CleaningRequest request = new CleaningRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setRoomNumber(rs.getString("room_number"));
        request.setPreferredDate(rs.getDate("preferred_date"));
        request.setPreferredTime(rs.getTime("preferred_time"));
        request.setRequestDate(rs.getTimestamp("request_date"));
        request.setStatus(rs.getString("status"));
        
        // Create and set Student object
        Student student = new Student();
        student.setStudentId(rs.getInt("s_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setFullName(rs.getString("full_name"));
        request.setStudent(student);
        
        // Set assigned staff if exists
        if (rs.getInt("assigned_user_id") > 0) {
            User assignedStaff = new User();
            assignedStaff.setUserId(rs.getInt("assigned_user_id"));
            assignedStaff.setUsername(rs.getString("assigned_user_name"));
            request.setAssignedTo(assignedStaff);
        }
        
        return request;
    }
}