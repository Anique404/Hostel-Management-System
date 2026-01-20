package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.Complaint;
import com.hms.models.Student;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {
    private String lastErrorMessage = null;

    public String getLastErrorMessage() { return lastErrorMessage; }

    // Submit new complaint
    public boolean submitComplaint(Complaint complaint) {
        String sql = "INSERT INTO Complaints (student_id, complaint_type, description, " +
                    "complaint_date, status, location, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, complaint.getStudent().getStudentId());
            pstmt.setString(2, complaint.getComplaintType());
            pstmt.setString(3, complaint.getDescription());
            pstmt.setTimestamp(4, new Timestamp(complaint.getComplaintDate().getTime()));
            pstmt.setString(5, complaint.getStatus());
            pstmt.setString(6, complaint.getLocation());
            pstmt.setString(7, complaint.getPriority());
            
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
    
    // Get complaints by student
    public List<Complaint> getComplaintsByStudent(int studentId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "WHERE c.student_id = ? ORDER BY c.complaint_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return complaints;
    }
    
    // Get pending complaints (for warden/admin)
    public List<Complaint> getPendingComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "WHERE c.status = 'Pending' ORDER BY c.complaint_date DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        return complaints;
    }
    
    // Get resolved complaints
    public List<Complaint> getResolvedComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "WHERE c.status = 'Resolved' ORDER BY c.resolved_date DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        return complaints;
    }
    
    // Get complaints by status (for admin panel)
    public List<Complaint> getComplaintsByStatus(String status) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "WHERE c.status = ? ORDER BY c.complaint_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return complaints;
    }
    
    // Get recent complaints (for dashboard)
    public List<Complaint> getRecentComplaints(int limit) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "ORDER BY c.complaint_date DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return complaints;
    }
    
    // Get complaint by ID
    public Complaint getComplaintById(int complaintId) {
        Complaint complaint = null;
        String sql = "SELECT c.*, s.student_id as s_id, s.registration_no, s.full_name, " +
                    "u.user_id as resolver_id, u.username as resolver_name " +
                    "FROM Complaints c " +
                    "JOIN Students s ON c.student_id = s.student_id " +
                    "LEFT JOIN Users u ON c.resolved_by = u.user_id " +
                    "WHERE c.complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, complaintId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                complaint = extractComplaintFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return complaint;
    }
    
    // Resolve complaint (for warden/admin)
    public boolean resolveComplaint(int complaintId, int wardenId) {
        String sql = "UPDATE Complaints SET status = 'Resolved', " +
                    "resolved_by = ?, resolved_date = ? WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, wardenId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(3, complaintId);
            
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
    
    // Update complaint status with resolution notes (for warden)
    public boolean updateComplaintStatus(int complaintId, String status, int resolvedByUserId, String resolutionNotes) {
        String sql = "";
        
        if (status.equals("Resolved")) {
            sql = "UPDATE Complaints SET status = ?, resolved_by = ?, " +
                  "resolved_date = ?, resolution_notes = ? WHERE complaint_id = ?";
        } else {
            sql = "UPDATE Complaints SET status = ?, resolved_by = ?, " +
                  "resolution_notes = ? WHERE complaint_id = ?";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            if (status.equals("Resolved")) {
                pstmt.setString(1, status);
                pstmt.setInt(2, resolvedByUserId);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(4, resolutionNotes);
                pstmt.setInt(5, complaintId);
            } else {
                pstmt.setString(1, status);
                pstmt.setInt(2, resolvedByUserId);
                pstmt.setString(3, resolutionNotes);
                pstmt.setInt(4, complaintId);
            }
            
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
    
    // Simple update complaint status
    public boolean updateComplaintStatus(int complaintId, String status) {
        String sql = "UPDATE Complaints SET status = ? WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, complaintId);
            
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
    
    // Helper method to extract Complaint from ResultSet
    private Complaint extractComplaintFromResultSet(ResultSet rs) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(rs.getInt("complaint_id"));
        complaint.setComplaintType(rs.getString("complaint_type"));
        complaint.setDescription(rs.getString("description"));
        complaint.setComplaintDate(rs.getTimestamp("complaint_date"));
        complaint.setStatus(rs.getString("status"));
        complaint.setResolvedDate(rs.getTimestamp("resolved_date"));
        complaint.setLocation(rs.getString("location"));
        complaint.setPriority(rs.getString("priority"));
        
        // Create and set Student object
        Student student = new Student();
        student.setStudentId(rs.getInt("s_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setFullName(rs.getString("full_name"));
        complaint.setStudent(student);
        
        // Set resolver if exists
        if (rs.getInt("resolver_id") > 0) {
            User resolver = new User();
            resolver.setUserId(rs.getInt("resolver_id"));
            resolver.setUsername(rs.getString("resolver_name"));
            complaint.setResolvedBy(resolver);
        }
        
        return complaint;
    }
}