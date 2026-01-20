package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.RoomAllocationApplication;
import com.hms.models.Student;
import com.hms.models.Hostel;
import com.hms.models.Room;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationApplicationDAO {
    private String lastErrorMessage = null;

    public String getLastErrorMessage() { return lastErrorMessage; }

    // Create new application
    public boolean createApplication(RoomAllocationApplication application) {
        String sql = "INSERT INTO RoomAllocationApplication " +
                    "(student_id, hostel_id, application_date, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, application.getStudent().getStudentId());
            pstmt.setInt(2, application.getHostel() != null ? application.getHostel().getHostelId() : 0);
            pstmt.setTimestamp(3, new Timestamp(application.getApplicationDate().getTime()));
            pstmt.setString(4, application.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            // Get the generated application ID
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    application.setApplicationId(generatedKeys.getInt(1));
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error creating application: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Get all applications
    public List<RoomAllocationApplication> getAllApplications() {
        List<RoomAllocationApplication> applications = new ArrayList<>();
        
        System.out.println("=== DEBUG: getAllApplications ===");
        
        String sql = "SELECT application_id, student_id, hostel_id, application_date, status " +
                     "FROM RoomAllocationApplication ORDER BY application_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            System.out.println("DEBUG: Connection established");
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("DEBUG: Row " + count + 
                                 " - App ID: " + rs.getInt("application_id") + 
                                 ", Student ID: " + rs.getInt("student_id"));
                
                RoomAllocationApplication app = new RoomAllocationApplication();
                app.setApplicationId(rs.getInt("application_id"));
                app.setApplicationDate(rs.getTimestamp("application_date"));
                app.setStatus(rs.getString("status"));
                
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                app.setStudent(student);
                
                Hostel hostel = new Hostel();
                hostel.setHostelId(rs.getInt("hostel_id"));
                app.setHostel(hostel);
                
                applications.add(app);
            }
            
            System.out.println("DEBUG: Total records: " + count);
            
        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        
        return applications;
    }
    
 // Add this method to test direct database access
    public void directDatabaseTest() {
        System.out.println("=== DIRECT DATABASE TEST ===");
        
        String[] testQueries = {
            "SELECT COUNT(*) FROM RoomAllocationApplication",
            "SELECT COUNT(*) FROM roomallocationapplication",
            "SELECT COUNT(*) FROM ROOMALLOCATIONAPPLICATION",
            "SELECT * FROM RoomAllocationApplication"
        };
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            for (String query : testQueries) {
                System.out.println("\nTrying query: " + query);
                
                try {
                    pstmt = conn.prepareStatement(query);
                    rs = pstmt.executeQuery();
                    
                    if (query.contains("COUNT")) {
                        if (rs.next()) {
                            System.out.println("Count: " + rs.getInt(1));
                        }
                    } else {
                        int rowCount = 0;
                        while (rs.next()) {
                            rowCount++;
                            System.out.println("Row " + rowCount + ":");
                            System.out.println("  App ID: " + rs.getInt("application_id"));
                            System.out.println("  Student ID: " + rs.getInt("student_id"));
                            System.out.println("  Status: " + rs.getString("status"));
                        }
                        System.out.println("Total rows: " + rowCount);
                    }
                    
                } catch (SQLException e) {
                    System.err.println("Query failed: " + e.getMessage());
                } finally {
                    DBConnection.closeResultSet(rs);
                    DBConnection.closeStatement(pstmt);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    // Get applications by student ID
    public List<RoomAllocationApplication> getApplicationsByStudentId(int studentId) {
        List<RoomAllocationApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM RoomAllocationApplication WHERE student_id = ? ORDER BY application_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                applications.add(extractApplicationFromResultSet(rs));
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error getting applications by student ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return applications;
    }
    
    // Get application by ID
    public RoomAllocationApplication getApplicationById(int applicationId) {
        String sql = "SELECT * FROM RoomAllocationApplication WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, applicationId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractApplicationFromResultSet(rs);
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error getting application by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return null;
    }
    
    // Get applications by status - CORRECTED VERSION
    public List<RoomAllocationApplication> getApplicationsByStatus(String status) {
        List<RoomAllocationApplication> applications = new ArrayList<>();
        
        System.out.println("DEBUG: getApplicationsByStatus called with: " + status);
        
        // SIMPLE QUERY WITHOUT JOIN - Don't try to load student/hostel here
        String sql = "SELECT application_id, student_id, hostel_id, application_date, status " +
                     "FROM RoomAllocationApplication WHERE status = ? ORDER BY application_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            System.out.println("DEBUG: Got connection");
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            
            rs = pstmt.executeQuery();
            System.out.println("DEBUG: Query executed");
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("DEBUG: Found app #" + count + 
                                 " - ID: " + rs.getInt("application_id") + 
                                 ", Student ID: " + rs.getInt("student_id"));
                
                // Create simple application WITHOUT loading student/hostel
                RoomAllocationApplication app = new RoomAllocationApplication();
                app.setApplicationId(rs.getInt("application_id"));
                app.setApplicationDate(rs.getTimestamp("application_date"));
                app.setStatus(rs.getString("status"));
                
                // Just set IDs, don't load full objects
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                app.setStudent(student);
                
                Hostel hostel = new Hostel();
                hostel.setHostelId(rs.getInt("hostel_id"));
                app.setHostel(hostel);
                
                applications.add(app);
            }
            
            System.out.println("DEBUG: Total apps found: " + count);
            
        } catch (SQLException e) {
            System.err.println("ERROR in getApplicationsByStatus: " + e.getMessage());
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            // CLOSE RESOURCES PROPERLY
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        
        return applications;
    }
    
    // Get applications by student ID
    public List<RoomAllocationApplication> getApplicationsByStudent(int studentId) {
        List<RoomAllocationApplication> applications = new ArrayList<>();
        
        // SIMPLE VERSION without complex JOINs
        String sql = "SELECT * FROM RoomAllocationApplication WHERE student_id = ? ORDER BY application_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RoomAllocationApplication app = extractApplicationFromResultSet(rs);
                
                // Load student details
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.getStudentById(studentId);
                if (student != null) {
                    app.setStudent(student);
                }
                
                // Load hostel details
                if (app.getHostel() != null && app.getHostel().getHostelId() > 0) {
                    HostelDAO hostelDAO = new HostelDAO();
                    Hostel hostel = hostelDAO.getHostelById(app.getHostel().getHostelId());
                    if (hostel != null) {
                        app.setHostel(hostel);
                    }
                }
                
                applications.add(app);
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error in getApplicationsByStudent: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return applications;
    }
    
    // Update application status (Office verification)
    public boolean verifyByOffice(int applicationId, int officeUserId) {
        String sql = "UPDATE RoomAllocationApplication SET status = 'OfficeVerified', " +
                    "verified_by_office = ?, verified_date = ? WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officeUserId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(3, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error in verifyByOffice: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Update application (generic update)
    public boolean updateApplication(RoomAllocationApplication application) {
        String sql = "UPDATE RoomAllocationApplication SET " +
                    "student_id = ?, hostel_id = ?, application_date = ?, status = ?, " +
                    "allocated_room_id = ?, verified_by_office = ?, verified_date = ? " +
                    "WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, application.getStudent().getStudentId());
            pstmt.setInt(2, application.getHostel() != null ? application.getHostel().getHostelId() : 0);
            pstmt.setTimestamp(3, new Timestamp(application.getApplicationDate().getTime()));
            pstmt.setString(4, application.getStatus());
            
            // Set allocated room if exists
            if (application.getAllocatedRoom() != null) {
                pstmt.setInt(5, application.getAllocatedRoom().getRoomId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            // Set verified by office if exists
            if (application.getVerifiedByOffice() != null) {
                pstmt.setInt(6, application.getVerifiedByOffice().getUserId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            // Set verified date if exists
            if (application.getVerifiedDate() != null) {
                pstmt.setTimestamp(7, new Timestamp(application.getVerifiedDate().getTime()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP);
            }
            
            pstmt.setInt(8, application.getApplicationId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error updating application: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Update application status - CORRECTED VERSION
    public boolean updateApplicationStatus(int applicationId, String status, int adminId) {
        String query = "UPDATE RoomAllocationApplication SET status = ? WHERE application_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("DEBUG: Updated application " + applicationId + " to status: " + status);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error updating application status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Approve application and allocate room (Admin approval)
    public boolean approveAndAllocateRoom(int applicationId, int roomId) {
        String sql = "UPDATE RoomAllocationApplication SET status = 'AdminApproved', " +
                    "allocated_room_id = ? WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error in approveAndAllocateRoom: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Reject application
    public boolean rejectApplication(int applicationId) {
        String sql = "UPDATE RoomAllocationApplication SET status = 'Rejected' WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error in rejectApplication: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Delete application
    public boolean deleteApplication(int applicationId) {
        String sql = "DELETE FROM RoomAllocationApplication WHERE application_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error deleting application: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
    
    // Count applications by status
    public int countApplicationsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM RoomAllocationApplication WHERE status = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error counting applications by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return 0;
    }
    
    // Check if student has pending application
    public boolean hasPendingApplication(int studentId) {
        String sql = "SELECT COUNT(*) FROM RoomAllocationApplication WHERE student_id = ? AND status = 'Pending'";
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
            System.err.println("Error in hasPendingApplication: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return false;
    }
    
    // Check if student has any application (any status)
    public boolean hasAnyApplication(int studentId) {
        String sql = "SELECT COUNT(*) FROM RoomAllocationApplication WHERE student_id = ?";
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
            System.err.println("Error checking if student has any application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return false;
    }
    
    // Get applications by hostel
    public List<RoomAllocationApplication> getApplicationsByHostel(int hostelId) {
        List<RoomAllocationApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM RoomAllocationApplication WHERE hostel_id = ? ORDER BY application_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hostelId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                applications.add(extractApplicationFromResultSet(rs));
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.err.println("Error getting applications by hostel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return applications;
    }
    
    // Helper method to extract Application from ResultSet
    private RoomAllocationApplication extractApplicationFromResultSet(ResultSet rs) throws SQLException {
        RoomAllocationApplication application = new RoomAllocationApplication();
        application.setApplicationId(rs.getInt("application_id"));
        application.setApplicationDate(rs.getTimestamp("application_date"));
        application.setStatus(rs.getString("status"));
        
        if (rs.getTimestamp("verified_date") != null) {
            application.setVerifiedDate(rs.getTimestamp("verified_date"));
        }
        
        // Create and set Student object
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        application.setStudent(student);
        
        // Create and set Hostel object if available
        int hostelId = rs.getInt("hostel_id");
        if (hostelId > 0) {
            Hostel hostel = new Hostel();
            hostel.setHostelId(hostelId);
            application.setHostel(hostel);
        }
        
        // Set allocated room if exists
        int roomId = rs.getInt("allocated_room_id");
        if (roomId > 0) {
            Room room = new Room();
            room.setRoomId(roomId);
            application.setAllocatedRoom(room);
        }
        
        // Set office user if verified
        int userId = rs.getInt("verified_by_office");
        if (userId > 0) {
            User officeUser = new User();
            officeUser.setUserId(userId);
            application.setVerifiedByOffice(officeUser);
        }
        
        return application;
    }
    
    // Simple extraction for basic queries
    private RoomAllocationApplication extractSimpleApplication(ResultSet rs) throws SQLException {
        RoomAllocationApplication application = new RoomAllocationApplication();
        application.setApplicationId(rs.getInt("application_id"));
        
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        application.setStudent(student);
        
        int hostelId = rs.getInt("hostel_id");
        if (hostelId > 0) {
            Hostel hostel = new Hostel();
            hostel.setHostelId(hostelId);
            application.setHostel(hostel);
        }
        
        application.setApplicationDate(rs.getTimestamp("application_date"));
        application.setStatus(rs.getString("status"));
        
        return application;
    }
    
    // NEW METHOD: Test database connection and table
    public boolean testConnection() {
        String sql = "SELECT COUNT(*) FROM RoomAllocationApplication";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("DEBUG: Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("ERROR: Database connection failed: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
}