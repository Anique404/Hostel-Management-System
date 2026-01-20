package com.hms.db;

import java.io.FileInputStream;
import java.io.File;
import java.sql.*;
import java.util.Properties;

public class DBConnection {
    // Default path (fallback)
    private static String DB_PATH = "C:/Users/Anique/OneDrive/Documents/Hostel.accdb";
    private static String DB_URL;

    static {
        // Attempt to load config.properties from project root (user.dir)
        try {
            String userDir = System.getProperty("user.dir");
            File cfg = new File(userDir, "config.properties");
            if (cfg.exists()) {
                Properties p = new Properties();
                try (FileInputStream fis = new FileInputStream(cfg)) {
                    p.load(fis);
                    String path = p.getProperty("db.path");
                    if (path != null && !path.trim().isEmpty()) {
                        DB_PATH = path.trim();
                    }
                }
            }
        } catch (Exception ex) {
            // ignore and fall back to default
            System.err.println("Warning: could not read config.properties: " + ex.getMessage());
        }

        // Normalize and build URL
        DB_PATH = DB_PATH.replace('\\', '/');
        DB_URL = "jdbc:ucanaccess://" + DB_PATH;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("UCanAccess driver not found. Please add ucanaccess jar and dependencies to classpath.", e);
        }
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database at: " + DB_URL + " - " + e.getMessage(), e);
        }
    }

    // Test connection helper - returns null on success, or error message on failure
    public static String testConnection() {
        try (Connection c = getConnection()) {
            if (c != null && !c.isClosed()) {
                return null; // success
            } else {
                return "Connection returned null or closed";
            }
        } catch (SQLException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try { 
                rs.close(); 
            } catch (SQLException e) { 
                e.printStackTrace();
            }
        }
    }
}