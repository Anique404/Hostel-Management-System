package com.hms.dao;

import com.hms.db.DBConnection;
import com.hms.models.Payment;
import com.hms.models.FeeChallan;
import com.hms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    
    // Record new payment
    public boolean recordPayment(Payment payment) {
        String sql = "INSERT INTO Payments (challan_id, payment_date, payment_method, " +
                    "transaction_id, paid_amount, received_by) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, payment.getChallan().getChallanId());
            pstmt.setTimestamp(2, new Timestamp(payment.getPaymentDate().getTime()));
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getTransactionId());
            pstmt.setDouble(5, payment.getPaidAmount());
            pstmt.setInt(6, payment.getReceivedBy().getUserId());
            
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
    
    // Get payments by student
    public List<Payment> getPaymentsByStudent(int studentId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, fc.challan_id as fc_id, fc.month_year, fc.amount as challan_amount, " +
                    "u.user_id as receiver_id, u.username as receiver_name " +
                    "FROM Payments p " +
                    "JOIN Fee_Challans fc ON p.challan_id = fc.challan_id " +
                    "JOIN Users u ON p.received_by = u.user_id " +
                    "JOIN Students s ON fc.student_id = s.student_id " +
                    "WHERE s.student_id = ? ORDER BY p.payment_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return payments;
    }
    
    // Get payment by challan ID
    public Payment getPaymentByChallanId(int challanId) {
        String sql = "SELECT p.*, fc.challan_id as fc_id, fc.month_year, fc.amount as challan_amount, " +
                    "u.user_id as receiver_id, u.username as receiver_name " +
                    "FROM Payments p " +
                    "JOIN Fee_Challans fc ON p.challan_id = fc.challan_id " +
                    "JOIN Users u ON p.received_by = u.user_id " +
                    "WHERE fc.challan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, challanId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
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
    
    // Helper method to extract Payment from ResultSet
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaidAmount(rs.getDouble("paid_amount"));
        
        // Create and set FeeChallan object
        FeeChallan challan = new FeeChallan();
        challan.setChallanId(rs.getInt("fc_id"));
        challan.setMonthYear(rs.getString("month_year"));
        challan.setAmount(rs.getDouble("challan_amount"));
        payment.setChallan(challan);
        
        // Create and set User object (receiver)
        User receiver = new User();
        receiver.setUserId(rs.getInt("receiver_id"));
        receiver.setUsername(rs.getString("receiver_name"));
        payment.setReceivedBy(receiver);
        
        return payment;}
}