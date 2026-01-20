package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private String lastErrorMessage = null;

    public String getLastErrorMessage() { return lastErrorMessage; }


    // Validate user login
    public User validateLogin(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                // Normalize role string to avoid whitespace/case issues
                String role = rs.getString("role");
                if (role != null) role = role.trim();
                user.setRole(role);
                user.setCreatedDate(rs.getTimestamp("created_date"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return null;
    }


    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                String role = rs.getString("role");
                if (role != null) role = role.trim();
                user.setRole(role);
                user.setCreatedDate(rs.getTimestamp("created_date"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        return users;
    }

    // Delete user by ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

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

    // Get user by username
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                String role = rs.getString("role");
                if (role != null) role = role.trim();
                user.setRole(role);
                user.setCreatedDate(rs.getTimestamp("created_date"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return null;
    }
    // Add new user
        public boolean addUser(User user) {
        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            // Use Statement.RETURN_GENERATED_KEYS to get the auto-generated ID when supported
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Try to get the auto-generated user_id; but some JDBC drivers (Access) may not return it
                try {
                    rs = pstmt.getGeneratedKeys();
                    if (rs != null && rs.next()) {
                        int userId = rs.getInt(1);
                        user.setUserId(userId); // set back into object for callers
                        System.out.println("User added with ID: " + userId);
                    } else {
                        // Fallback: query the users table by username to get the id (works when username is unique)
                        DBConnection.closeResultSet(rs);
                        rs = null;
                        try (PreparedStatement lookup = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?")) {
                            lookup.setString(1, user.getUsername());
                            ResultSet rs2 = lookup.executeQuery();
                            if (rs2 != null && rs2.next()) {
                                int userId = rs2.getInt("user_id");
                                user.setUserId(userId);
                                System.out.println("User added (fallback) with ID: " + userId);
                            }
                            if (rs2 != null) try { rs2.close(); } catch (SQLException ignore) {}
                        } catch (SQLException lookupEx) {
                            // ignore lookup failure but log for debugging
                            System.err.println("Warning: could not lookup generated user_id: " + lookupEx.getMessage());
                        }
                    }
                } catch (SQLException ignore) {
                    // Some drivers don't support getGeneratedKeys; try fallback lookup
                    System.err.println("Warning: could not retrieve generated keys: " + ignore.getMessage());
                    try {
                        DBConnection.closeResultSet(rs);
                        rs = null;
                        try (PreparedStatement lookup = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?")) {
                            lookup.setString(1, user.getUsername());
                            ResultSet rs2 = lookup.executeQuery();
                            if (rs2 != null && rs2.next()) {
                                int userId = rs2.getInt("user_id");
                                user.setUserId(userId);
                                System.out.println("User added (fallback) with ID: " + userId);
                            }
                            if (rs2 != null) try { rs2.close(); } catch (SQLException ignore2) {}
                        }
                    } catch (SQLException lookupEx) {
                        System.err.println("Warning: fallback lookup failed: " + lookupEx.getMessage());
                    }
                } finally {
                    if (rs != null) try { rs.close(); } catch (SQLException e) { }
                }
                return true;  // success because rows were affected
            }
            return false;  // Indicate failure if no rows affected
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { }
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }

    // Get all users by role
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                String dbRole = rs.getString("role");
                if (dbRole != null) dbRole = dbRole.trim();
                user.setRole(dbRole);
                user.setCreatedDate(rs.getTimestamp("created_date"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return users;
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    // Update user
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getUserId());

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

    // Get user by ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                String role = rs.getString("role");
                if (role != null) role = role.trim();
                user.setRole(role);
                user.setCreatedDate(rs.getTimestamp("created_date"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    // Remove user by username (helper used by AdminDashboard)
    public boolean removeUser(String username) {
        String sqlFind = "SELECT user_id FROM Users WHERE username = ?";
        String sqlDelete = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlFind);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                DBConnection.closeResultSet(rs);
                DBConnection.closeStatement(pstmt);
                pstmt = conn.prepareStatement(sqlDelete);
                pstmt.setInt(1, userId);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            } else {
                return false; // not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }
}