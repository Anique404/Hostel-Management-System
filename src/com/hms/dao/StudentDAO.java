package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.Student;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    // Get student by registration number
    public Student getStudentByRegNo(String regNo) {
        String sql = "SELECT s.*, u.username, u.role FROM students s " +
                    "LEFT JOIN users u ON s.user_id = u.user_id " +
                    "WHERE s.registration_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, regNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getStudentByRegNo: " + e.getMessage());
        }
        return null;
    }
    
    // Get student by user ID
    public Student getStudentByUserId(int userId) {
        String sql = "SELECT s.*, u.username, u.role FROM students s " +
                    "LEFT JOIN users u ON s.user_id = u.user_id " +
                    "WHERE s.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getStudentByUserId: " + e.getMessage());
        }
        return null;
    }
    
    // Get student by student ID
    public Student getStudentById(int studentId) {
        String sql = "SELECT s.*, u.username, u.role FROM students s " +
                    "LEFT JOIN users u ON s.user_id = u.user_id " +
                    "WHERE s.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getStudentById: " + e.getMessage());
        }
        return null;
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, u.username, u.role FROM students s " +
                    "LEFT JOIN users u ON s.user_id = u.user_id " +
                    "ORDER BY s.student_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getAllStudents: " + e.getMessage());
        }
        return students;
    }
    
    // Add new student - Aapke Student.java ke hisaab se
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (registration_no, full_name, father_name, cnic, address, department, semester, user_id, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getRegistrationNo());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getFatherName());
            pstmt.setString(4, student.getCnic());
            pstmt.setString(5, student.getAddress());
            pstmt.setString(6, student.getDepartment());
            pstmt.setString(7, student.getSemester());
            
            // User ID agar user object se mil raha hai
            if (student.getUser() != null) {
                pstmt.setInt(8, student.getUser().getUserId());
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(9, student.getStatus() != null ? student.getStatus() : "Applied");
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in addStudent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Update student - Aapke Student.java ke hisaab se
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET registration_no = ?, full_name = ?, father_name = ?, " +
                    "cnic = ?, address = ?, department = ?, semester = ?, status = ? " +
                    "WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getRegistrationNo());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getFatherName());
            pstmt.setString(4, student.getCnic());
            pstmt.setString(5, student.getAddress());
            pstmt.setString(6, student.getDepartment());
            pstmt.setString(7, student.getSemester());
            pstmt.setString(8, student.getStatus());
            pstmt.setInt(9, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updateStudent: " + e.getMessage());
            return false;
        }
    }
    
    // Delete student
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in deleteStudent: " + e.getMessage());
            return false;
        }
    }
    
    // Helper method to extract student from ResultSet - Aapke Student.java ke hisaab se
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setFullName(rs.getString("full_name"));
        student.setFatherName(rs.getString("father_name"));
        student.setCnic(rs.getString("cnic"));
        student.setAddress(rs.getString("address"));
        student.setDepartment(rs.getString("department"));
        student.setSemester(rs.getString("semester"));
        student.setAdmissionDate(rs.getDate("admission_date"));
        student.setStatus(rs.getString("status"));
        
        // User object create karna agar user_id hai
        if (rs.getInt("user_id") > 0) {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setRole(rs.getString("role"));
            student.setUser(user);
        }
        
        return student;
    }
}