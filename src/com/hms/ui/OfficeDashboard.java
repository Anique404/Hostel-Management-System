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

public class OfficeDashboard extends JFrame {
    
    private User currentUser;
    private StudentDAO studentDAO;
    
    public OfficeDashboard(User user) {
        this.currentUser = user;
        this.studentDAO = new StudentDAO();
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Office Dashboard - Hostel Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        
     // =============== OFFICE DASHBOARD SIDEBAR ===============
     // Sidebar
     JPanel sidebar = new JPanel();
     sidebar.setLayout(new GridLayout(4, 1, 2, 2));
     sidebar.setBackground(new Color(240, 240, 240));  // Light grey sidebar background
     sidebar.setPreferredSize(new Dimension(220, 600));
     sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

     // ONLY 4 menu items
     String[] menuItems = {" Dashboard", " Student Records", " Settings", " Logout"};

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
     // =============== END SIDEBAR ===============
        
        // Create ONLY 3 content panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createStudentRecordsPanel(), "Student Records");
        mainPanel.add(createSettingsPanel(), "Settings");
        
        // Main layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1100, 70));
        
        JLabel titleLabel = new JLabel("OFFICE DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // User info in header
        JLabel userInfo = new JLabel("User: " + currentUser.getUsername(), SwingConstants.RIGHT);
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
        
        JLabel title = new JLabel("Office Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Only 2 simple cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        cardsPanel.setBackground(new Color(240, 240, 240));
        
        try {
            // Get data from database
            List<Student> allStudents = studentDAO.getAllStudents();
            int totalStudents = allStudents != null ? allStudents.size() : 0;
            
            cardsPanel.add(createSimpleCard("Total Students", String.valueOf(totalStudents), 
                new Color(52, 152, 219)));
            
            // Today's date card
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            cardsPanel.add(createSimpleCard("Today's Date", sdf.format(new Date()), 
                new Color(39, 174, 96)));
                
        } catch (Exception e) {
            e.printStackTrace();
            cardsPanel.add(createSimpleCard("Error", "DB Error", Color.RED));
            cardsPanel.add(createSimpleCard("Check", "Connection", Color.RED));
        }
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        welcomePanel.setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("<html><center><h2>Welcome, " + currentUser.getUsername() + "!</h2>" +
            "<p>Office Management System - Hostel Management</p>" +
            "<p>Use the sidebar to navigate through options</p></center></html>", 
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
    
    // Student Records Panel - SIMPLE version
    private JPanel createStudentRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Student Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Simple search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(25);
        searchPanel.add(searchField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(39, 174, 96));
        searchBtn.setForeground(new Color(60, 60, 60));
        searchPanel.add(searchBtn);
        
        JButton resetBtn = new JButton("Show All");
        searchPanel.add(resetBtn);
        
        // Student Table - SIMPLE columns
        String[] columns = {"Student ID", "Name", "Registration No", "Department", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studentTable = new JTable(model);
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(52, 152, 219));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        
        // Load student data from database
        loadStudentData(model);
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Simple action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton viewBtn = new JButton("View Details");
        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(new Color(60, 60, 60));
        
        JButton refreshBtn = new JButton("Refresh");
        
        // Search functionality
        searchBtn.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                try {
                    List<Student> allStudents = studentDAO.getAllStudents();
                    model.setRowCount(0);
                    
                    for (Student student : allStudents) {
                        if (student.getFullName().toLowerCase().contains(searchTerm) ||
                            student.getRegistrationNo().toLowerCase().contains(searchTerm) ||
                            student.getDepartment().toLowerCase().contains(searchTerm)) {
                            
                            String status = student.getStatus();
                            if (status == null || status.isEmpty()) {
                                status = "Active";
                            }
                            
                            model.addRow(new Object[]{
                                student.getStudentId(),
                                student.getFullName(),
                                student.getRegistrationNo(),
                                student.getDepartment(),
                                status
                            });
                        }
                    }
                    
                    if (model.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "No students found!");
                        loadStudentData(model);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error searching students");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter search term!");
            }
        });
        
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudentData(model);
        });
        
        viewBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int studentId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    Student student = studentDAO.getStudentById(studentId);
                    
                    if (student != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        String status = student.getStatus();
                        if (status == null || status.isEmpty()) {
                            status = "Active";
                        }
                        
                        String details = "Student Details:\n\n" +
                                       "ID: " + student.getStudentId() + "\n" +
                                       "Name: " + student.getFullName() + "\n" +
                                       "Reg No: " + student.getRegistrationNo() + "\n" +
                                       "Department: " + student.getDepartment() + "\n" +
                                       "Semester: " + student.getSemester() + "\n" +
                                       "Admission Date: " + (student.getAdmissionDate() != null ? 
                                         sdf.format(student.getAdmissionDate()) : "N/A") + "\n" +
                                       "Status: " + status + "\n" +
                                       "Address: " + (student.getAddress() != null ? 
                                         student.getAddress() : "N/A");
                        
                        JOptionPane.showMessageDialog(this, details,
                            "Student Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error loading student details");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student!");
            }
        });
        
        refreshBtn.addActionListener(e -> loadStudentData(model));
        
        actionPanel.add(viewBtn);
        actionPanel.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadStudentData(DefaultTableModel model) {
        try {
            List<Student> students = studentDAO.getAllStudents();
            model.setRowCount(0);
            
            for (Student student : students) {
                String status = student.getStatus();
                if (status == null || status.isEmpty()) {
                    status = "Active";
                }
                
                model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getRegistrationNo(),
                    student.getDepartment(),
                    status
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students from database");
        }
    }
    
    // Settings Panel - SIMPLE version
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Simple settings form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        
        // Current user info
        JPanel userPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        userPanel.setBorder(BorderFactory.createTitledBorder("Your Information"));
        
        userPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        userPanel.add(usernameField);
        
        userPanel.add(new JLabel("Role:"));
        JTextField roleField = new JTextField(currentUser.getRole());
        roleField.setEditable(false);
        userPanel.add(roleField);
        
        formPanel.add(userPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password change
        JPanel passwordPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        
        passwordPanel.add(new JLabel("Current Password:"));
        JPasswordField currentPass = new JPasswordField();
        passwordPanel.add(currentPass);
        
        passwordPanel.add(new JLabel("New Password:"));
        JPasswordField newPass = new JPasswordField();
        passwordPanel.add(newPass);
        
        passwordPanel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPass = new JPasswordField();
        passwordPanel.add(confirmPass);
        
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Simple buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton saveBtn = new JButton("Change Password");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(new Color(60, 60, 60));
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        
        saveBtn.addActionListener(e -> {
            String current = new String(currentPass.getPassword());
            String newP = new String(newPass.getPassword());
            String confirm = new String(confirmPass.getPassword());
            
            if (current.isEmpty() || newP.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all password fields!");
                return;
            }
            
            if (!newP.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "New passwords don't match!");
                return;
            }
            
            // In real app, you would update password in database here
            JOptionPane.showMessageDialog(this, 
                "Password change request sent!\n" +
                "(In real app, this would update database)",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            currentPass.setText("");
            newPass.setText("");
            confirmPass.setText("");
        });
        
        buttonPanel.add(saveBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User officeUser = new User();
            officeUser.setUsername("office1");
            officeUser.setRole("Office");
            new OfficeDashboard(officeUser);
        });
    }
}