package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.FeeChallan;
import com.hms.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeChallanDAO {
    
    // Generate fee challan
    public boolean generateChallan(FeeChallan challan) {
        String sql = "INSERT INTO fee_challan (student_id, fee_type, amount, issue_date, due_date, status, academic_year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("=== DEBUG: Generating fee challan ===");
        System.out.println("Student ID: " + challan.getStudentId());
        System.out.println("Fee Type: " + challan.getFeeType());
        System.out.println("Amount: " + challan.getAmount());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, challan.getStudentId());
            pstmt.setString(2, challan.getFeeType());
            pstmt.setDouble(3, challan.getAmount());
            pstmt.setDate(4, new java.sql.Date(challan.getIssueDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(challan.getDueDate().getTime()));
            pstmt.setString(6, "PENDING");
            pstmt.setString(7, challan.getAcademicYear());
            
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Challan generated, rows affected: " + rows);
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR generating challan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get challans by student ID
    public List<FeeChallan> getChallansByStudent(int studentId) {
        List<FeeChallan> challans = new ArrayList<>();
        String sql = "SELECT * FROM fee_challan WHERE student_id = ? ORDER BY issue_date DESC";
        
        System.out.println("=== DEBUG: Getting challans for student ID: " + studentId + " ===");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                challans.add(extractChallanFromResultSet(rs));
                count++;
            }
            
            System.out.println("DEBUG: Found " + count + " challans for student");
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("ERROR getting challans by student: " + e.getMessage());
            e.printStackTrace();
        }
        return challans;
    }
    
    // Get all challans (for admin)
    public List<FeeChallan> getAllChallans() {
        List<FeeChallan> challans = new ArrayList<>();
        String sql = "SELECT fc.*, s.full_name, s.registration_no FROM fee_challan fc " +
                    "LEFT JOIN student s ON fc.student_id = s.student_id " +
                    "ORDER BY fc.issue_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                challans.add(extractChallanFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR getting all challans: " + e.getMessage());
            e.printStackTrace();
        }
        return challans;
    }
    
    // Get pending challans for admin verification (status = 'PAID')
    public List<FeeChallan> getPendingChallans() {
        List<FeeChallan> challans = new ArrayList<>();
        String sql = "SELECT fc.*, s.full_name, s.registration_no FROM fee_challan fc " +
                    "LEFT JOIN student s ON fc.student_id = s.student_id " +
                    "WHERE fc.status = 'PAID' " +
                    "ORDER BY fc.issue_date DESC";
        
        System.out.println("=== DEBUG: Getting pending challans (PAID status) ===");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                challans.add(extractChallanFromResultSet(rs));
                count++;
            }
            
            System.out.println("DEBUG: Found " + count + " pending challans");
            
        } catch (SQLException e) {
            System.err.println("ERROR getting pending challans: " + e.getMessage());
            e.printStackTrace();
        }
        return challans;
    }
    
    // Mark challan as paid
    public boolean markChallanAsPaid(int challanId, String transactionId) {
        String sql = "UPDATE fee_challan SET status = 'PAID', payment_date = ?, transaction_id = ? WHERE challan_id = ?";
        
        System.out.println("=== DEBUG: Marking challan as paid ===");
        System.out.println("Challan ID: " + challanId);
        System.out.println("Transaction ID: " + transactionId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(2, transactionId);
            pstmt.setInt(3, challanId);
            
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Marked as paid, rows affected: " + rows);
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR marking challan as paid: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Verify payment (admin)
    public boolean verifyPayment(int challanId, boolean isVerified) {
        String newStatus = isVerified ? "VERIFIED" : "PENDING";
        String sql = "UPDATE fee_challan SET status = ? WHERE challan_id = ?";
        
        System.out.println("=== DEBUG: Verifying payment ===");
        System.out.println("Challan ID: " + challanId);
        System.out.println("New Status: " + newStatus);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, challanId);
            
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Payment verified, rows affected: " + rows);
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR verifying payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get challan by ID
    public FeeChallan getChallanById(int challanId) {
        String sql = "SELECT fc.*, s.full_name, s.registration_no FROM fee_challan fc " +
                    "LEFT JOIN student s ON fc.student_id = s.student_id " +
                    "WHERE fc.challan_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, challanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractChallanFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR getting challan by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Extract challan from ResultSet
    private FeeChallan extractChallanFromResultSet(ResultSet rs) throws SQLException {
        FeeChallan challan = new FeeChallan();
        
        try {
            challan.setChallanId(rs.getInt("challan_id"));
            challan.setStudentId(rs.getInt("student_id"));
            challan.setFeeType(rs.getString("fee_type"));
            challan.setAmount(rs.getDouble("amount"));
            challan.setIssueDate(rs.getDate("issue_date"));
            challan.setDueDate(rs.getDate("due_date"));
            challan.setStatus(rs.getString("status"));
            challan.setPaymentDate(rs.getDate("payment_date"));
            challan.setTransactionId(rs.getString("transaction_id"));
            challan.setAcademicYear(rs.getString("academic_year"));
            
            // Try to get student details
            try {
                Student student = new Student();
                student.setFullName(rs.getString("full_name"));
                student.setRegistrationNo(rs.getString("registration_no"));
                challan.setStudent(student);
            } catch (SQLException e) {
                // Student columns might not be in result set
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR extracting challan from ResultSet: " + e.getMessage());
            throw e;
        }
        
        return challan;
    }
    
    // Check if table exists
    public boolean checkTableExists() {
        String sql = "SELECT COUNT(*) FROM fee_challan";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("DEBUG: fee_challan table exists");
            return true;
            
        } catch (SQLException e) {
            System.err.println("DEBUG: fee_challan table does not exist: " + e.getMessage());
            return false;
        }
    }
    
    // Create table if not exists
    public boolean createTable() {
        System.out.println("=== DEBUG: Creating fee_challan table ===");
        
        String sql = "CREATE TABLE fee_challan (" +
                    "challan_id AUTOINCREMENT PRIMARY KEY, " +
                    "student_id INTEGER NOT NULL, " +
                    "fee_type VARCHAR(50) NOT NULL, " +
                    "amount CURRENCY NOT NULL, " +
                    "issue_date DATETIME NOT NULL, " +
                    "due_date DATETIME NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "payment_date DATETIME, " +
                    "transaction_id VARCHAR(100), " +
                    "academic_year VARCHAR(20), " +
                    "created_at DATETIME DEFAULT NOW()" +
                    ")";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("DEBUG: fee_challan table created successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("ERROR creating table: " + e.getMessage());
            return false;
        }
    }
    
    // Initialize table
    public void initialize() {
        System.out.println("=== Initializing FeeChallanDAO ===");
        
        if (!checkTableExists()) {
            System.out.println("Table doesn't exist. Creating...");
            if (createTable()) {
                System.out.println("Table created successfully");
            } else {
                System.err.println("Failed to create table");
            }
        } else {
            System.out.println("Table already exists");
        }
    }
}