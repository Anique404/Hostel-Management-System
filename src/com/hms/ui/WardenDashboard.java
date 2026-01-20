package com.hms.ui;

import com.hms.models.*;
import com.hms.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WardenDashboard extends JFrame {
    
    private User currentUser;
    private ComplaintDAO complaintDAO;
    
    public WardenDashboard(User user) {
        this.currentUser = user;
        this.complaintDAO = new ComplaintDAO();
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Warden Dashboard - Hostel Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        
     // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(3, 1, 2, 2));
        sidebar.setBackground(new Color(240, 240, 240));  // Light grey sidebar background
        sidebar.setPreferredSize(new Dimension(200, 550));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ONLY 3 menu items
        String[] menuItems = {" Dashboard", " View Complaints", " Logout"};

        // Colors define karein
        Color buttonBgColor = new Color(255, 255, 255);      // White background for buttons
        Color buttonTextColor = new Color(60, 60, 60);       // Dark grey text
        Color buttonHoverBgColor = new Color(41, 128, 185);  // Blue background on hover
        Color buttonHoverTextColor = new Color(60, 60, 60);            // White text on hover
        Color buttonBorderColor = new Color(220, 220, 220);  // Light grey border

        for (int i = 0; i < menuItems.length; i++) {
            final String menuText = menuItems[i];
            final String cleanItem = menuText.replaceAll("[^a-zA-Z\\s]", "").trim();
            
            JButton btn = new JButton(menuText);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Button styling
            btn.setBackground(buttonBgColor);           // White background
            btn.setForeground(buttonTextColor);         // Dark grey text
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonBorderColor, 1),  // Border
                BorderFactory.createEmptyBorder(12, 20, 12, 20)         // Padding
            ));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Hover effect - Mouse enter
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(buttonHoverBgColor);     // Blue on hover
                    btn.setForeground(buttonHoverTextColor);   // White text on hover
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 100, 160), 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(buttonBgColor);          // Back to white
                    btn.setForeground(buttonTextColor);        // Back to dark grey
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(buttonBorderColor, 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    btn.setBackground(new Color(30, 100, 160));  // Darker blue when clicked
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    btn.setBackground(buttonHoverBgColor);      // Back to hover color
                }
            });
            
            btn.addActionListener(e -> {
                if (cleanItem.equals("Logout")) {
                    logout();
                } else {
                    cardLayout.show(mainPanel, cleanItem);
                }
            });
            sidebar.add(btn);
        }
        
        // Create ONLY 2 content panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createViewComplaintsPanel(), "View Complaints");
        
        // Main layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));  // Direct Blue color

        headerPanel.setPreferredSize(new Dimension(1000, 70));
        
        JLabel titleLabel = new JLabel("WARDEN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Warden info in header
        JLabel userInfo = new JLabel("Warden: " + currentUser.getUsername(), SwingConstants.RIGHT);
        userInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        userInfo.setForeground(Color.WHITE);
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        headerPanel.add(userInfo, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm();
        }
    }
    
    // SIMPLE Dashboard Panel
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(240, 240, 240));
        
        JLabel title = new JLabel("Warden Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Simple cards - Get data from database
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        cardsPanel.setBackground(new Color(240, 240, 240));
        
        try {
            // Get data from database
            List<Complaint> pendingComplaints = complaintDAO.getPendingComplaints();
            List<Complaint> resolvedComplaints = complaintDAO.getResolvedComplaints();
            
            int pendingCount = pendingComplaints != null ? pendingComplaints.size() : 0;
            int resolvedCount = resolvedComplaints != null ? resolvedComplaints.size() : 0;
            
            cardsPanel.add(createSimpleCard("Pending Complaints", String.valueOf(pendingCount), 
                new Color(231, 76, 60)));
            
            cardsPanel.add(createSimpleCard("Resolved Complaints", String.valueOf(resolvedCount), 
                new Color(46, 204, 113)));
                
        } catch (Exception e) {
            e.printStackTrace();
            cardsPanel.add(createSimpleCard("Error", "DB Error", Color.RED));
            cardsPanel.add(createSimpleCard("Check", "Connection", Color.RED));
        }
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        welcomePanel.setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("<html><center><h2>Welcome, Warden " + currentUser.getUsername() + "!</h2>" +
            "<p>Hostel Management System - Warden Panel</p>" +
            "<p>Manage student complaints and resolve issues</p></center></html>", 
            SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(welcomePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSimpleCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // View Complaints Panel - With Database
    private JPanel createViewComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Manage Complaints", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Complaints Table
        String[] columns = {"Complaint ID", "Student", "Room", "Type", "Description", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable complaintsTable = new JTable(model);
        complaintsTable.setRowHeight(25);
        complaintsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        complaintsTable.getTableHeader().setBackground(new Color(52, 152, 219));
        complaintsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Load complaints from database
        loadComplaintsData(model);
        
        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        
        // Action Buttons - Resolve and Reject
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton resolveBtn = new JButton("Resolve Complaint");
        resolveBtn.setBackground(new Color(46, 204, 113));
        resolveBtn.setForeground(new Color(60, 60, 60));
        resolveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton rejectBtn = new JButton("Reject Complaint");
        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(new Color(60, 60, 60));
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton viewBtn = new JButton("View Details");
        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(new Color(60, 60, 60));
        
        JButton refreshBtn = new JButton("Refresh");
        
        // Button actions
        resolveBtn.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int complaintId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 1).toString();
                String complaintType = model.getValueAt(selectedRow, 3).toString();
                
                String resolution = JOptionPane.showInputDialog(this,
                    "Resolve complaint for " + studentName + "\n" +
                    "Type: " + complaintType + "\n\n" +
                    "Enter resolution details:",
                    "Resolve Complaint", JOptionPane.QUESTION_MESSAGE);
                
                if (resolution != null && !resolution.trim().isEmpty()) {
                    try {
                        boolean success = complaintDAO.updateComplaintStatus(
                            complaintId, 
                            "Resolved", 
                            currentUser.getUserId(),
                            resolution
                        );
                        
                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                "✅ Complaint resolved!\n" +
                                "Student: " + studentName + "\n" +
                                "Resolution: " + resolution,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadComplaintsData(model); // Refresh table
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "❌ Error resolving complaint: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a complaint!");
            }
        });
        
        rejectBtn.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int complaintId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 1).toString();
                
                String reason = JOptionPane.showInputDialog(this,
                    "Reject complaint for " + studentName + "\n\n" +
                    "Enter rejection reason:",
                    "Reject Complaint", JOptionPane.QUESTION_MESSAGE);
                
                if (reason != null && !reason.trim().isEmpty()) {
                    try {
                        boolean success = complaintDAO.updateComplaintStatus(
                            complaintId, 
                            "Rejected", 
                            currentUser.getUserId(),
                            "Rejected: " + reason
                        );
                        
                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                "❌ Complaint rejected!\n" +
                                "Student: " + studentName + "\n" +
                                "Reason: " + reason,
                                "Rejected", JOptionPane.INFORMATION_MESSAGE);
                            loadComplaintsData(model);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a complaint!");
            }
        });
        
        viewBtn.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int complaintId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    Complaint complaint = complaintDAO.getComplaintById(complaintId);
                    
                    if (complaint != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                        String details = "📢 Complaint Details:\n\n" +
                                       "Complaint ID: " + complaint.getComplaintId() + "\n" +
                                       "Student: " + complaint.getStudent().getFullName() + "\n" +
                                       "Type: " + complaint.getComplaintType() + "\n" +
                                       "Date: " + sdf.format(complaint.getComplaintDate()) + "\n" +
                                       "Status: " + complaint.getStatus() + "\n" +
                                       "Description:\n" + complaint.getDescription();
                        
                        JTextArea textArea = new JTextArea(details);
                        textArea.setEditable(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        
                        JScrollPane detailScroll = new JScrollPane(textArea);
                        detailScroll.setPreferredSize(new Dimension(400, 300));
                        
                        JOptionPane.showMessageDialog(this, detailScroll,
                            "Complaint Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading complaint details");
                }
            }
        });
        
        refreshBtn.addActionListener(e -> loadComplaintsData(model));
        
        actionPanel.add(resolveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(viewBtn);
        actionPanel.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadComplaintsData(DefaultTableModel model) {
        try {
            List<Complaint> complaints = complaintDAO.getPendingComplaints();
            model.setRowCount(0); // Clear existing data
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
            
            for (Complaint complaint : complaints) {
                String description = complaint.getDescription();
                if (description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                
                model.addRow(new Object[]{
                    complaint.getComplaintId(),
                    complaint.getStudent().getFullName(),
                    complaint.getLocation() != null ? complaint.getLocation() : "N/A",
                    complaint.getComplaintType(),
                    description,
                    sdf.format(complaint.getComplaintDate()),
                    complaint.getStatus()
                });
            }
            
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"", "No pending complaints", "", "", "", "", ""});
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addRow(new Object[]{"", "Error loading complaints", "", "", "", "", ""});
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User wardenUser = new User();
            wardenUser.setUsername("warden1");
            wardenUser.setRole("Warden");
            new WardenDashboard(wardenUser);
        });
    }
}