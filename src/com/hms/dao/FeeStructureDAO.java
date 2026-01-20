package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.FeeStructure;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class FeeStructureDAO {
    
    // Add new fee structure WITH DEBUGGING
    public boolean addFeeStructure(FeeStructure feeStructure) {
        String sql = "INSERT INTO fee_structure (fee_type, amount, academic_year, description, is_active, effective_from) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        System.out.println("=== DEBUG: Starting addFeeStructure for Access DB ===");
        System.out.println("Fee Type: " + feeStructure.getFeeType());
        System.out.println("Amount: " + feeStructure.getAmount());
        System.out.println("Academic Year: " + feeStructure.getAcademicYear());
        System.out.println("Description: " + feeStructure.getDescription());
        System.out.println("Active: " + feeStructure.isActive());
        System.out.println("Effective From: " + feeStructure.getEffectiveFrom());
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return false;
            }
            System.out.println("DEBUG: Database connection successful");
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, feeStructure.getFeeType());
            pstmt.setDouble(2, feeStructure.getAmount());
            pstmt.setString(3, feeStructure.getAcademicYear());
            pstmt.setString(4, feeStructure.getDescription());
            pstmt.setBoolean(5, feeStructure.isActive());
            
            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(feeStructure.getEffectiveFrom().getTime());
            pstmt.setDate(6, sqlDate);
            
            System.out.println("DEBUG: SQL Query: " + pstmt.toString());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("DEBUG: Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("=== SQL ERROR in addFeeStructure ===");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            
            // Check if table doesn't exist
            if (e.getMessage().toLowerCase().contains("table") || 
                e.getMessage().toLowerCase().contains("no such table") ||
                e.getMessage().contains("fee_structure")) {
                System.out.println("DEBUG: Table might not exist. Creating it...");
                createTable();
                // Try again
                return addFeeStructure(feeStructure);
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("=== GENERAL ERROR in addFeeStructure ===");
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
            System.out.println("=== DEBUG: addFeeStructure completed ===");
        }
    }
    
    // Get all active fee structures
    public List<FeeStructure> getActiveFeeStructures() {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT * FROM fee_structure WHERE is_active = TRUE ORDER BY fee_type";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                feeStructures.add(extractFeeStructureFromResultSet(rs));
            }
            
            System.out.println("DEBUG: Found " + feeStructures.size() + " active fee structures");
        } catch (SQLException e) {
            System.err.println("Error getting active fee structures: " + e.getMessage());
            // If table doesn't exist, create it
            if (e.getMessage().toLowerCase().contains("table") || 
                e.getMessage().toLowerCase().contains("no such table")) {
                System.out.println("Table doesn't exist. Returning empty list.");
            }
            e.printStackTrace();
        }
        return feeStructures;
    }
    
    // Get all fee structures (active and inactive)
    public List<FeeStructure> getAllFeeStructures() {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT * FROM fee_structure ORDER BY fee_type, academic_year";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                feeStructures.add(extractFeeStructureFromResultSet(rs));
            }
            
            System.out.println("DEBUG: Found " + feeStructures.size() + " total fee structures");
        } catch (SQLException e) {
            System.err.println("Error getting all fee structures: " + e.getMessage());
            e.printStackTrace();
        }
        return feeStructures;
    }
    
    // Get fee structure by type
    public FeeStructure getFeeStructureByType(String feeType) {
        String sql = "SELECT * FROM fee_structure WHERE fee_type = ? AND is_active = TRUE ORDER BY effective_from DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, feeType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFeeStructureFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting fee structure by type: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Update fee structure
    public boolean updateFeeStructure(FeeStructure feeStructure) {
        String sql = "UPDATE fee_structure SET amount = ?, academic_year = ?, description = ?, " +
                    "is_active = ?, effective_from = ? WHERE fee_structure_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, feeStructure.getAmount());
            pstmt.setString(2, feeStructure.getAcademicYear());
            pstmt.setString(3, feeStructure.getDescription());
            pstmt.setBoolean(4, feeStructure.isActive());
            
            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(feeStructure.getEffectiveFrom().getTime());
            pstmt.setDate(5, sqlDate);
            
            pstmt.setInt(6, feeStructure.getFeeStructureId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fee structure: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to extract FeeStructure from ResultSet
    private FeeStructure extractFeeStructureFromResultSet(ResultSet rs) throws SQLException {
        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setFeeStructureId(rs.getInt("fee_structure_id"));
        feeStructure.setFeeType(rs.getString("fee_type"));
        feeStructure.setAmount(rs.getDouble("amount"));
        feeStructure.setAcademicYear(rs.getString("academic_year"));
        feeStructure.setDescription(rs.getString("description"));
        
        // Access uses BIT for boolean
        try {
            feeStructure.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            // Try getting as integer
            int activeInt = rs.getInt("is_active");
            feeStructure.setActive(activeInt == 1);
        }
        
        // Handle dates
        java.sql.Date sqlEffectiveFrom = rs.getDate("effective_from");
        if (sqlEffectiveFrom != null) {
            feeStructure.setEffectiveFrom(new Date(sqlEffectiveFrom.getTime()));
        }
        
        // Access doesn't have TIMESTAMP, use DATE if available
        try {
            java.sql.Timestamp sqlCreatedAt = rs.getTimestamp("created_at");
            if (sqlCreatedAt != null) {
                feeStructure.setCreatedAt(new Date(sqlCreatedAt.getTime()));
            }
        } catch (SQLException e) {
            // created_at column might not exist
        }
        
        return feeStructure;
    }
    
    // Create table if not exists (Access-specific SQL)
    private boolean createTable() {
        System.out.println("=== DEBUG: Creating fee_structure table ===");
        
        // Access SQL for creating table
        String[] createSQL = {
            "CREATE TABLE fee_structure (fee_structure_id AUTOINCREMENT PRIMARY KEY)",
            "ALTER TABLE fee_structure ADD COLUMN fee_type VARCHAR(50) NOT NULL",
            "ALTER TABLE fee_structure ADD COLUMN amount CURRENCY NOT NULL",
            "ALTER TABLE fee_structure ADD COLUMN academic_year VARCHAR(20)",
            "ALTER TABLE fee_structure ADD COLUMN description MEMO",
            "ALTER TABLE fee_structure ADD COLUMN is_active BIT DEFAULT TRUE",
            "ALTER TABLE fee_structure ADD COLUMN effective_from DATETIME",
            "ALTER TABLE fee_structure ADD COLUMN created_at DATETIME DEFAULT NOW()"
        };
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Drop table if exists (for testing)
            try {
                stmt.execute("DROP TABLE fee_structure");
                System.out.println("DEBUG: Dropped existing table");
            } catch (SQLException e) {
                // Table didn't exist, that's fine
                System.out.println("DEBUG: No table to drop");
            }
            
            // Create table with columns
            for (String sql : createSQL) {
                System.out.println("Executing: " + sql);
                stmt.execute(sql);
            }
            
            System.out.println("DEBUG: Table created successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            // Try simpler create
            return createSimpleTable();
        }
    }
    
    // Simpler table creation
    private boolean createSimpleTable() {
        String sql = "CREATE TABLE fee_structure (" +
                    "fee_structure_id AUTOINCREMENT PRIMARY KEY, " +
                    "fee_type VARCHAR(50), " +
                    "amount CURRENCY, " +
                    "academic_year VARCHAR(20), " +
                    "description MEMO, " +
                    "is_active BIT, " +
                    "effective_from DATETIME, " +
                    "created_at DATETIME DEFAULT NOW()" +
                    ")";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("DEBUG: Simple table created");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating simple table: " + e.getMessage());
            return false;
        }
    }
    
    // Check if table exists (Access-specific)
    public boolean checkTableExists() {
        // Access doesn't support SHOW TABLES. We'll try to query the table
        String sql = "SELECT COUNT(*) FROM fee_structure";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("DEBUG: Table 'fee_structure' exists");
            return true;
            
        } catch (SQLException e) {
            System.out.println("DEBUG: Table 'fee_structure' does not exist or error: " + e.getMessage());
            return false;
        }
    }
    
    // Test database connection
    public boolean testConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("DEBUG: Connection is null");
                return false;
            }
            
            boolean isConnected = !conn.isClosed();
            System.out.println("DEBUG: Database connection test: " + (isConnected ? "SUCCESS" : "FAILED"));
            
            // Test with a simple query
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    System.out.println("DEBUG: Database query test: SUCCESS");
                }
            }
            
            return isConnected;
        } catch (SQLException e) {
            System.err.println("DEBUG: Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    // Initialize - call this at startup
    public void initialize() {
        System.out.println("=== Initializing FeeStructureDAO ===");
        
        if (!checkTableExists()) {
            System.out.println("Table doesn't exist. Creating...");
            if (createTable()) {
                System.out.println("Table created successfully");
                // Insert default fee structures
                insertDefaultData();
            } else {
                System.err.println("Failed to create table");
            }
        } else {
            System.out.println("Table already exists");
        }
    }
    
    // Insert default fee structures
    private void insertDefaultData() {
        String sql = "INSERT INTO fee_structure (fee_type, amount, academic_year, description, is_active, effective_from) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Hostel fee
            pstmt.setString(1, "HOSTEL_FEE");
            pstmt.setDouble(2, 15000.00);
            pstmt.setString(3, "2024-2025");
            pstmt.setString(4, "Per Semester Hostel Fee");
            pstmt.setBoolean(5, true);
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            
            // Mess fee
            pstmt.setString(1, "MESS_FEE");
            pstmt.setDouble(2, 12000.00);
            pstmt.setString(3, "2024-2025");
            pstmt.setString(4, "Per Semester Mess Fee");
            pstmt.setBoolean(5, true);
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            
            System.out.println("DEBUG: Default fee structures inserted");
            
        } catch (SQLException e) {
            System.err.println("Error inserting default data: " + e.getMessage());
        }
    }
}