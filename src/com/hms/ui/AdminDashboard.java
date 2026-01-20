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
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;

public class AdminDashboard extends JFrame {
    
    private User currentUser;
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private ComplaintDAO complaintDAO;
    private RoomAllocationApplicationDAO roomAppDAO;
    private MessRegistrationDAO messRegDAO;
    private FeeChallanDAO feeChallanDAO;
    private CleaningRequestDAO cleaningRequestDAO;
    private FeeStructureDAO feeStructureDAO;
    
    public AdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.complaintDAO = new ComplaintDAO();
        this.roomAppDAO = new RoomAllocationApplicationDAO();
        this.messRegDAO = new MessRegistrationDAO();
        this.feeChallanDAO = new FeeChallanDAO();
        this.cleaningRequestDAO = new CleaningRequestDAO();
        this.feeStructureDAO = new FeeStructureDAO();
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Admin Dashboard - Hostel Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        
        JLabel titleLabel = new JLabel("ADMINISTRATOR PANEL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Admin info in header
        JLabel adminInfo = new JLabel("Admin: " + currentUser.getUsername(), SwingConstants.RIGHT);
        adminInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminInfo.setForeground(Color.WHITE);
        adminInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        headerPanel.add(adminInfo, BorderLayout.EAST);
        
        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(9, 1, 2, 2));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(250, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] menuItems = {
            " Dashboard", 
            " Add User", 
            " Remove User", 
            " Hostel Applications",
            " Mess Registration", 
            " Fee Structure",
            " View Complaints",
            " Logout"
        };
        
        Color buttonBgColor = new Color(255, 255, 255);
        Color buttonTextColor = new Color(60, 60, 60);
        Color buttonHoverBgColor = new Color(41, 128, 185);
        Color buttonHoverTextColor = new Color(60, 60, 60);
        Color buttonBorderColor = new Color(220, 220, 220);
        
        for (int i = 0; i < menuItems.length; i++) {
            final String menuText = menuItems[i];
            final String cleanItem = menuText.replaceAll("[^a-zA-Z\\s]", "").trim();
            
            JButton btn = new JButton(menuText);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Button styling
            btn.setBackground(buttonBgColor);
            btn.setForeground(buttonTextColor);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonBorderColor, 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
            ));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Hover effect
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(buttonHoverBgColor);
                    btn.setForeground(buttonHoverTextColor);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 100, 160), 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(buttonBgColor);
                    btn.setForeground(buttonTextColor);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(buttonBorderColor, 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
            });
            
            btn.addActionListener(e -> menuItemClicked(cleanItem, cardLayout, mainPanel));
            sidebar.add(btn);
        }
        
        // Create content panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createAddUserPanel(), "Add User");
        mainPanel.add(createRemoveUserPanel(), "Remove User");
        mainPanel.add(createHostelApplicationsPanel(), "Hostel Applications");
        mainPanel.add(createMessRegistrationPanel(), "Mess Registration");
        mainPanel.add(createFeeStructurePanel(), "Fee Structure");
        mainPanel.add(createViewComplaintPanel(), "View Complaints");
        
        // Main layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void menuItemClicked(String item, CardLayout cardLayout, JPanel mainPanel) {
        if (item.equals("Logout")) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm();
            }
        } else {
            cardLayout.show(mainPanel, item);
        }
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Dashboard Overview", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        cardsPanel.setBackground(new Color(245, 247, 250));
        
        try {
            // Get real data from database
            List<User> allUsers = userDAO.getAllUsers();
            List<Student> allStudents = studentDAO.getAllStudents();
            List<Complaint> pendingComplaints = complaintDAO.getPendingComplaints();
            List<RoomAllocationApplication> pendingApps = roomAppDAO.getApplicationsByStatus("Pending");
            
            int totalUsers = allUsers != null ? allUsers.size() : 0;
            int totalStudents = allStudents != null ? allStudents.size() : 0;
            int pendingComplaintCount = pendingComplaints != null ? pendingComplaints.size() : 0;
            int pendingAppCount = pendingApps != null ? pendingApps.size() : 0;
            
            cardsPanel.add(createDashboardCard("👥 Total Users", String.valueOf(totalUsers), 
                new Color(41, 128, 185), "Registered Users"));
            
            cardsPanel.add(createDashboardCard("🎓 Total Students", String.valueOf(totalStudents), 
                new Color(46, 204, 113), "Active Students"));
            
            cardsPanel.add(createDashboardCard("📋 Pending Tasks", 
                String.valueOf(pendingComplaintCount + pendingAppCount), 
                new Color(241, 196, 15), "Applications & Complaints"));
                
        } catch (Exception e) {
            e.printStackTrace();
            cardsPanel.add(createErrorCard("Database", "Error"));
            cardsPanel.add(createErrorCard("Please", "Check"));
            cardsPanel.add(createErrorCard("Connection", "Failed"));
        }
        
        // Recent Activities
        JPanel activitiesPanel = new JPanel(new BorderLayout());
        activitiesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recent Activities"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        activitiesPanel.setBackground(Color.WHITE);
        
        DefaultListModel<String> activitiesModel = new DefaultListModel<>();
        JList<String> activitiesList = new JList<>(activitiesModel);
        activitiesList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activitiesList.setBackground(Color.WHITE);
        JScrollPane activitiesScroll = new JScrollPane(activitiesList);
        
        // Load recent activities
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            
            // Get recent complaints
            List<Complaint> complaints = complaintDAO.getPendingComplaints();
            if (complaints != null) {
                for (int i = 0; i < Math.min(complaints.size(), 5); i++) {
                    Complaint c = complaints.get(i);
                    if (c != null) {
                        activitiesModel.addElement("📢 Complaint: " + c.getComplaintType() + 
                            " (" + sdf.format(c.getComplaintDate()) + ")");
                    }
                }
            }
            
            // Get pending applications
            List<RoomAllocationApplication> apps = roomAppDAO.getApplicationsByStatus("Pending");
            if (apps != null) {
                for (int i = 0; i < Math.min(apps.size(), 3); i++) {
                    RoomAllocationApplication app = apps.get(i);
                    if (app != null) {
                        activitiesModel.addElement("📋 Hostel App: " + 
                            (app.getStudent() != null && app.getStudent().getFullName() != null ? 
                             app.getStudent().getFullName() : "Student") + 
                            " (" + sdf.format(app.getApplicationDate()) + ")");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            activitiesModel.addElement("⚠️ Error loading activities");
        }
        
        if (activitiesModel.isEmpty()) {
            activitiesModel.addElement("📭 No recent activities");
        }
        
        activitiesPanel.add(activitiesScroll, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(activitiesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDashboardCard(String title, String value, Color color, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(150, 150, 150));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createErrorCard(String title, String value) {
        return createDashboardCard(title, value, new Color(231, 76, 60), "Error loading data");
    }
    
    private JPanel createAddUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Add New User", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("User Information"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username*:"), gbc);
        
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password*:"), gbc);
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password*:"), gbc);
        
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(confirmPasswordField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Role*:"), gbc);
        
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Admin", "Office", "Warden"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(roleCombo, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(emailField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addBtn = new JButton("Add User");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(new Color(60, 60, 60));
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        addBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String email = emailField.getText().trim();
            
            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Check if username exists
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setRole(role);
                
                
                boolean success = userDAO.addUser(newUser);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ User added successfully!\nUsername: " + username + "\nRole: " + role,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear form
                    usernameField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    emailField.setText("");
                    roleCombo.setSelectedIndex(0);
                    
                    // If student role, also create student record
                    if ("Student".equals(role)) {
                        Student student = new Student();
                        student.setRegistrationNo(username);
                        student.setFullName(username);
                        student.setStatus("Active");
                        student.setAdmissionDate(new Date());
                        studentDAO.addStudent(student);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            emailField.setText("");
            roleCombo.setSelectedIndex(0);
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRemoveUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Remove User", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Select User to Remove"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"User ID", "Username", "Role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable userTable = new JTable(model);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Load users
        loadUsersTable(model);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        removeBtn.setBackground(new Color(231, 76, 60));
        removeBtn.setForeground(new Color(60, 60, 60));
        removeBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        removeBtn.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a user to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int userId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            String username = model.getValueAt(selectedRow, 1).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove user: " + username + "?\nThis action cannot be undone!",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean success = userDAO.deleteUser(userId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "✅ User removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsersTable(model);
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Failed to remove user!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsersTable(model));
        
        buttonPanel.add(removeBtn);
        buttonPanel.add(refreshBtn);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadUsersTable(DefaultTableModel model) {
        try {
            List<User> users = userDAO.getAllUsers();
            model.setRowCount(0);
            
            for (User user : users) {
                model.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createHostelApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Hostel Applications", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 247, 250));
        
        // Applications Table
        String[] columns = {"App ID", "Student Reg No", "Student Name", "Hostel", "Applied Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable appsTable = new JTable(model);
        appsTable.setRowHeight(25);
        appsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        appsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        appsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(appsTable);
        
        // Load applications
        loadHostelApplications(model);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(245, 247, 250));
        
        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        approveBtn.setBackground(new Color(46, 204, 113));
        approveBtn.setForeground(new Color(60, 60, 60));
        approveBtn.setFocusPainted(false);
        
        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(new Color(60, 60, 60));
        rejectBtn.setFocusPainted(false);
        
        JButton viewBtn = new JButton("View Details");
        viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Button actions
        approveBtn.addActionListener(e -> {
            int selectedRow = appsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 2).toString();
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Approve hostel application for " + studentName + "?",
                    "Confirm Approval", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = roomAppDAO.updateApplicationStatus(appId, "AdminApproved", currentUser.getUserId());
                        
                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                "✅ Application approved for " + studentName,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadHostelApplications(model);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "❌ Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select an application!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        rejectBtn.addActionListener(e -> {
            int selectedRow = appsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 2).toString();
                
                String reason = JOptionPane.showInputDialog(this,
                    "Enter rejection reason for " + studentName + ":",
                    "Rejection Reason", JOptionPane.QUESTION_MESSAGE);
                
                if (reason != null && !reason.trim().isEmpty()) {
                    try {
                        boolean success = roomAppDAO.updateApplicationStatus(appId, "Rejected", currentUser.getUserId());
                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                "❌ Application rejected for " + studentName + "\nReason: " + reason,
                                "Application Rejected", JOptionPane.INFORMATION_MESSAGE);
                            loadHostelApplications(model);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select an application!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        viewBtn.addActionListener(e -> {
            int selectedRow = appsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String details = "📋 Application Details:\n\n" +
                               "App ID: " + model.getValueAt(selectedRow, 0) + "\n" +
                               "Student: " + model.getValueAt(selectedRow, 2) + "\n" +
                               "Student Reg No: " + model.getValueAt(selectedRow, 1) + "\n" +
                               "Hostel: " + model.getValueAt(selectedRow, 3) + "\n" +
                               "Applied Date: " + model.getValueAt(selectedRow, 4) + "\n" +
                               "Status: " + model.getValueAt(selectedRow, 5);
                
                JOptionPane.showMessageDialog(this, details,
                    "Application Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        refreshBtn.addActionListener(e -> {
            loadHostelApplications(model);
            JOptionPane.showMessageDialog(this, "Table refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        
        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(viewBtn);
        actionPanel.add(refreshBtn);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadHostelApplications(DefaultTableModel model) {
        try {
            List<RoomAllocationApplication> apps = roomAppDAO.getApplicationsByStatus("Pending");
            model.setRowCount(0);
            
            if (apps == null || apps.isEmpty()) {
                model.addRow(new Object[]{"No pending applications", "N/A", "N/A", "N/A", "N/A", "N/A"});
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            
            for (RoomAllocationApplication app : apps) {
                String regNo = "N/A";
                String studentName = "N/A";
                String hostelName = "N/A";
                
                if (app.getStudent() != null && app.getStudent().getStudentId() > 0) {
                    try {
                        StudentDAO studentDAO = new StudentDAO();
                        Student student = studentDAO.getStudentById(app.getStudent().getStudentId());
                        
                        if (student != null) {
                            regNo = student.getRegistrationNo() != null ? student.getRegistrationNo() : "N/A";
                            studentName = student.getFullName() != null ? student.getFullName() : "N/A";
                        }
                    } catch (Exception e) {
                        regNo = "ID: " + app.getStudent().getStudentId();
                    }
                }
                
                if (app.getHostel() != null && app.getHostel().getHostelId() > 0) {
                    try {
                        HostelDAO hostelDAO = new HostelDAO();
                        Hostel hostel = hostelDAO.getHostelById(app.getHostel().getHostelId());
                        
                        if (hostel != null) {
                            hostelName = hostel.getHostelName() != null ? hostel.getHostelName() : "N/A";
                        }
                    } catch (Exception e) {
                        hostelName = "ID: " + app.getHostel().getHostelId();
                    }
                }
                
                model.addRow(new Object[]{
                    app.getApplicationId(),
                    regNo,
                    studentName,
                    hostelName,
                    sdf.format(app.getApplicationDate()),
                    app.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("ERROR in loadHostelApplications: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "❌ Error loading applications: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createMessRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Mess Registration Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Active Mess Registrations"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"Reg ID", "Student", "Reg No", "Meal Type", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable messTable = new JTable(model);
        messTable.setRowHeight(30);
        messTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        messTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(messTable);
        
        // Load mess registrations
        loadMessRegistrations(model);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton endBtn = new JButton("End Registration");
        endBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        endBtn.setBackground(new Color(231, 76, 60));
        endBtn.setForeground(new Color(60, 60, 60));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        endBtn.addActionListener(e -> {
            int selectedRow = messTable.getSelectedRow();
            if (selectedRow >= 0) {
                int regId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 1).toString();
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "End mess registration for " + studentName + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = messRegDAO.endMessRegistration(regId);
                        if (success) {
                            JOptionPane.showMessageDialog(this, 
                                "✅ Registration ended successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadMessRegistrations(model);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        refreshBtn.addActionListener(e -> loadMessRegistrations(model));
        
        actionPanel.add(endBtn);
        actionPanel.add(refreshBtn);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadMessRegistrations(DefaultTableModel model) {
        try {
            List<MessRegistration> registrations = messRegDAO.getAllMessRegistrations();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            
            model.setRowCount(0);
            for (MessRegistration reg : registrations) {
                model.addRow(new Object[]{
                    reg.getRegistrationId(),
                    reg.getStudent().getFullName(),
                    reg.getStudent().getRegistrationNo(),
                    reg.getMealType(),
                    sdf.format(reg.getStartDate()),
                    sdf.format(reg.getEndDate()),
                    reg.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
    
   private JPanel createFeeStructurePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(new Color(245, 247, 250));
    
    JLabel title = new JLabel("Fee Structure Management", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(new Color(52, 73, 94));
    title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
    // Initialize fee structure DAO
    feeStructureDAO.initialize();
    
    // Main content with two panels
    JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
    mainContent.setBackground(new Color(245, 247, 250));
    
    // Left Panel - Current Fee Structure
    JPanel currentPanel = new JPanel(new BorderLayout());
    currentPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Current Fee Structure"),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    currentPanel.setBackground(Color.WHITE);
    
    String[] columns = {"ID", "Fee Type", "Amount", "Academic Year", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    JTable feeTable = new JTable(model);
    feeTable.setRowHeight(30);
    feeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    feeTable.getTableHeader().setBackground(new Color(240, 240, 240));
    feeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    
    JScrollPane tableScroll = new JScrollPane(feeTable);
    
    // Load current fee structure
    loadFeeStructureData(model);
    
    // Refresh button for table
    JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton refreshTableBtn = new JButton("Refresh");
    refreshTableBtn.addActionListener(e -> loadFeeStructureData(model));
    tableButtonPanel.add(refreshTableBtn);
    
    currentPanel.add(tableScroll, BorderLayout.CENTER);
    currentPanel.add(tableButtonPanel, BorderLayout.SOUTH);
    
    // Right Panel - Add/Edit Fee Structure
    JPanel editPanel = new JPanel(new GridBagLayout());
    editPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Add New Fee Structure"),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    editPanel.setBackground(Color.WHITE);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // Fee Type
    gbc.gridx = 0; gbc.gridy = 0;
    editPanel.add(new JLabel("Fee Type*:"), gbc);
    
    JComboBox<String> feeTypeCombo = new JComboBox<>(new String[]{"HOSTEL_FEE", "MESS_FEE"});
    feeTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    gbc.gridx = 1; gbc.gridy = 0;
    editPanel.add(feeTypeCombo, gbc);
    
    // Amount
    gbc.gridx = 0; gbc.gridy = 1;
    editPanel.add(new JLabel("Amount* (Rs.):"), gbc);
    
    JTextField amountField = new JTextField();
    amountField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    gbc.gridx = 1; gbc.gridy = 1;
    editPanel.add(amountField, gbc);
    
    // Academic Year
    gbc.gridx = 0; gbc.gridy = 2;
    editPanel.add(new JLabel("Academic Year*:"), gbc);
    
    JTextField yearField = new JTextField("2024-2025");
    yearField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    gbc.gridx = 1; gbc.gridy = 2;
    editPanel.add(yearField, gbc);
    
    // Description
    gbc.gridx = 0; gbc.gridy = 3;
    editPanel.add(new JLabel("Description:"), gbc);
    
    JTextArea descArea = new JTextArea(3, 20);
    descArea.setLineWrap(true);
    descArea.setWrapStyleWord(true);
    descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    JScrollPane descScroll = new JScrollPane(descArea);
    gbc.gridx = 1; gbc.gridy = 3;
    gbc.fill = GridBagConstraints.BOTH;
    editPanel.add(descScroll, gbc);
    
    // Active Checkbox
    JCheckBox activeCheckbox = new JCheckBox("Active (Available for use)", true);
    activeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    gbc.gridx = 0; gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    editPanel.add(activeCheckbox, gbc);
    
    // Add Button
    JButton saveBtn = new JButton("Save Fee Structure");
    saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    saveBtn.setBackground(new Color(41, 128, 185));
    saveBtn.setForeground(new Color(60, 60, 60));
    saveBtn.setFocusPainted(false);
    saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    
    saveBtn.addActionListener(e -> {
        try {
            // Validate
            if (amountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(panel, "Amount must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (yearField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter academic year!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("=== DEBUG: Creating FeeStructure object ===");
            
            FeeStructure feeStructure = new FeeStructure();
            feeStructure.setFeeType((String) feeTypeCombo.getSelectedItem());
            feeStructure.setAmount(amount);
            feeStructure.setAcademicYear(yearField.getText());
            feeStructure.setDescription(descArea.getText());
            feeStructure.setActive(activeCheckbox.isSelected());
            feeStructure.setEffectiveFrom(new Date());
            
            System.out.println("=== DEBUG: Calling addFeeStructure() ===");
            boolean success = feeStructureDAO.addFeeStructure(feeStructure);
            
            if (success) {
                JOptionPane.showMessageDialog(panel, 
                    "✅ Fee structure saved successfully!\n\n" +
                    "Fee Type: " + feeStructure.getFeeType() + "\n" +
                    "Amount: Rs. " + String.format("%.2f", feeStructure.getAmount()) + "\n" +
                    "Academic Year: " + feeStructure.getAcademicYear(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form
                amountField.setText("");
                descArea.setText("");
                
                // Refresh table
                loadFeeStructureData(model);
            } else {
                JOptionPane.showMessageDialog(panel, 
                    "❌ Failed to save fee structure!\n" +
                    "Please check console for error details.",
                    "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, 
                "Please enter valid amount!\nExample: 15000.00", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, 
                "❌ Unexpected error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    gbc.gridx = 0; gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    editPanel.add(saveBtn, gbc);
    
    mainContent.add(currentPanel);
    mainContent.add(editPanel);
    
    panel.add(title, BorderLayout.NORTH);
    panel.add(mainContent, BorderLayout.CENTER);
    
    return panel;
}

private void loadFeeStructureData(DefaultTableModel model) {
    try {
        List<FeeStructure> feeStructures = feeStructureDAO.getAllFeeStructures();
        model.setRowCount(0);
        
        if (feeStructures != null && !feeStructures.isEmpty()) {
            for (FeeStructure fs : feeStructures) {
                model.addRow(new Object[]{
                    fs.getFeeStructureId(),
                    fs.getFeeType(),
                    "Rs. " + String.format("%.2f", fs.getAmount()),
                    fs.getAcademicYear(),
                    fs.isActive() ? "✅ Active" : "❌ Inactive"
                });
            }
        } else {
            model.addRow(new Object[]{"No fee structures found", "", "", "", ""});
        }
    } catch (Exception e) {
        System.err.println("Error loading fee structure data: " + e.getMessage());
        model.addRow(new Object[]{"Error loading data", "", "", "", ""});
    }
}
    
    private JPanel createViewComplaintPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("View Complaints", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Complaints Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Complaints"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"Complaint ID", "Student", "Type", "Description", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable complaintsTable = new JTable(model);
        complaintsTable.setRowHeight(25);
        complaintsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        complaintsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        complaintsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        
        // Load complaints
        loadComplaintsData(model);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(245, 247, 250));
        
        JButton resolveBtn = new JButton("Mark as Resolved");
        resolveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resolveBtn.setBackground(new Color(46, 204, 113));
        resolveBtn.setForeground(new Color(60, 60, 60));
        resolveBtn.setFocusPainted(false);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Button actions
        resolveBtn.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int complaintId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String studentName = model.getValueAt(selectedRow, 1).toString();
                
                try {
                    boolean success = complaintDAO.updateComplaintStatus(complaintId, "Resolved");
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "✅ Complaint resolved!\n" +
                            "Student: " + studentName,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadComplaintsData(model);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "❌ Error resolving complaint: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a complaint!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshBtn.addActionListener(e -> loadComplaintsData(model));
        
        actionPanel.add(resolveBtn);
        actionPanel.add(refreshBtn);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadComplaintsData(DefaultTableModel model) {
        try {
            List<Complaint> complaints = complaintDAO.getPendingComplaints();
            model.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            
            for (Complaint complaint : complaints) {
                String description = complaint.getDescription();
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                
                model.addRow(new Object[]{
                    complaint.getComplaintId(),
                    complaint.getStudent().getFullName(),
                    complaint.getComplaintType(),
                    description != null ? description : "No description",
                    sdf.format(complaint.getComplaintDate()),
                    complaint.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "❌ Error loading complaints: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}