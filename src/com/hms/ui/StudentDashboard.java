package com.hms.ui;


import com.hms.models.*;
import com.hms.dao.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Date;
import java.util.List;

public class StudentDashboard extends JFrame {
    
    private User currentUser;
    private StudentDAO studentDAO;
    private ComplaintDAO complaintDAO;
    private CleaningRequestDAO cleaningRequestDAO;
    private MessRegistrationDAO messRegistrationDAO;
    private RoomAllocationApplicationDAO roomAllocDAO;
    private HostelDAO hostelDAO;
    private FeeChallanDAO feeChallanDAO;
    private Student student;
    
    public StudentDashboard(User user) {
        this.currentUser = user;
        this.studentDAO = new StudentDAO();
        this.complaintDAO = new ComplaintDAO();
        this.cleaningRequestDAO = new CleaningRequestDAO();
        this.messRegistrationDAO = new MessRegistrationDAO();
        this.roomAllocDAO = new RoomAllocationApplicationDAO();
        this.hostelDAO = new HostelDAO();
        this.feeChallanDAO = new FeeChallanDAO();
        
        // Get student data from database
        this.student = loadStudentData();
        
        initComponents();
    }
    
    private Student loadStudentData() {
        try {
            // Try to get student by registration number (username)
            Student loadedStudent = studentDAO.getStudentByRegNo(currentUser.getUsername());
            if (loadedStudent != null) return loadedStudent;
            
            // Fallback: maybe Users.username is not the same as registration_no; try linked user_id
            if (currentUser != null && currentUser.getUserId() > 0) {
                Student byUser = studentDAO.getStudentByUserId(currentUser.getUserId());
                if (byUser != null) return byUser;
            }
            
            // Not found in either place
            JOptionPane.showMessageDialog(this, 
                "Student profile not found in database.\n" +
                "Please ensure a Students record exists for your account (registration_no or linked user_id).\n" +
                "If you're an admin, create the student profile via Admin -> Add User (choose Student role) or add a Students record.",
                "Profile Not Found", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (Exception e) {
            System.err.println("Error loading student data: " + e.getMessage());
            return null;
        }
    }
    
    private void initComponents() {
        setTitle("Student Dashboard - Hostel Management System");
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
        
        JLabel titleLabel = new JLabel("STUDENT DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Student info in header
        String studentName = (student != null && student.getFullName() != null) ? 
                           student.getFullName() : currentUser.getUsername();
        String regNo = (student != null && student.getRegistrationNo() != null) ?
                      student.getRegistrationNo() : currentUser.getUsername();
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(41, 128, 185));
        
        JLabel userInfo = new JLabel("<html><b>" + studentName + "</b><br>" + regNo + "</html>", SwingConstants.RIGHT);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(Color.WHITE);
        userInfo.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));
        infoPanel.add(userInfo, BorderLayout.EAST);
        
        JLabel dateLabel = new JLabel(new SimpleDateFormat("dd MMM yyyy").format(new Date()), SwingConstants.LEFT);
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateLabel.setForeground(new Color(200, 220, 255));
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0));
        infoPanel.add(dateLabel, BorderLayout.WEST);
        
        headerPanel.add(infoPanel, BorderLayout.SOUTH);
        
     // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 2, 2));
        sidebar.setBackground(new Color(240, 240, 240));  // Light grey sidebar background
        sidebar.setPreferredSize(new Dimension(250, 650));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] menuItems = {
            " Dashboard", 
            " My Profile", 
            " Hostel Application", 
            " Mess Registration",
            " Submit Complaint",
            " My Fees",
            " Logout"
        };

        // Colors define karein
        Color buttonBgColor = new Color(255, 255, 255);      // White background for buttons
        Color buttonTextColor = new Color(60, 60, 60);       // Dark grey text
        Color buttonHoverBgColor = new Color(41, 128, 185);  // Blue background on hover
        Color buttonHoverTextColor = new Color(60, 60, 60);            // White text on hover
        Color buttonBorderColor = new Color(220, 220, 220);  // Light grey border

        for (int i = 0; i < menuItems.length; i++) {
            final String menuText = menuItems[i];
            final String cleanItem;
            
            if (menuText.contains("Hostel Application")) {
                cleanItem = "Apply for Hostel";
            } else {
                cleanItem = menuText.replaceAll("[^a-zA-Z\\s]", "").trim();
            }
            
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
            
            btn.addActionListener(e -> menuItemClicked(cleanItem, cardLayout, mainPanel));
            sidebar.add(btn);
        }
        
        // Create content panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createProfilePanel(), "My Profile");
        mainPanel.add(createHostelApplicationPanel(), "Apply for Hostel");
        mainPanel.add(createMessRegistrationPanel(), "Mess Registration");
        mainPanel.add(createComplaintPanel(), "Submit Complaint");
        mainPanel.add(createFeeChallanPanel(), "My Fees");
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Dashboard Overview", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
        contentPanel.setBackground(new Color(245, 247, 250));
        
        // Stats Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        cardsPanel.setBackground(new Color(245, 247, 250));
        
        if (student != null) {
            try {
                // Get room allocation status
                String roomInfo = "Not Applied";
                List<RoomAllocationApplication> apps = roomAllocDAO.getAllApplications();
                for (RoomAllocationApplication app : apps) {
                    if (app.getStudent().getStudentId() == student.getStudentId()) {
                        if ("AdminApproved".equals(app.getStatus())) {
                            roomInfo = app.getAllocatedRoom() != null ? 
                                      app.getAllocatedRoom().getRoomNumber() : "Approved";
                            break;
                        } else if ("Pending".equals(app.getStatus())) {
                            roomInfo = "Pending Approval";
                            break;
                        }
                    }
                }
                
                // Check mess registration
                String messStatus = "Not Registered";
                if (messRegistrationDAO.hasActiveMessRegistration(student.getStudentId())) {
                    List<MessRegistration> registrations = messRegistrationDAO.getActiveRegistrationsByStudent(student.getStudentId());
                    if (!registrations.isEmpty()) {
                        messStatus = registrations.get(0).getMealType();
                    }
                }
                
                // Get pending complaints count
                int pendingComplaints = 0;
                List<Complaint> complaints = complaintDAO.getComplaintsByStudent(student.getStudentId());
                if (complaints != null) {
                    pendingComplaints = (int) complaints.stream()
                        .filter(c -> c != null && "Pending".equals(c.getStatus()))
                        .count();
                }
                
                // Get fee status
                String feeStatus = "No Dues";
                List<FeeChallan> feeChallans = feeChallanDAO.getChallansByStudent(student.getStudentId());
                if (feeChallans != null) {
                    long pendingFees = feeChallans.stream()
                        .filter(fc -> fc != null && "Pending".equals(fc.getStatus()))
                        .count();
                    if (pendingFees > 0) {
                        feeStatus = pendingFees + " Pending";
                    }
                }
                
                cardsPanel.add(createDashboardCard(" Room Status", roomInfo, 
                    new Color(52, 152, 219), "Hostel Room"));
                
                cardsPanel.add(createDashboardCard(" Mess Status", messStatus, 
                    new Color(46, 204, 113), "Meal Plan"));
                
                cardsPanel.add(createDashboardCard(" Complaints", pendingComplaints + " Pending", 
                    new Color(241, 196, 15), "Pending Issues"));
                
                cardsPanel.add(createDashboardCard(" Fee Dues", feeStatus, 
                    new Color(155, 89, 182), "Payment Status"));
                    
            } catch (Exception e) {
                System.err.println("Error loading dashboard data: " + e.getMessage());
                cardsPanel.add(createErrorCard("Database Error", "Connection Issue"));
                cardsPanel.add(createErrorCard("Please", "Try Again"));
                cardsPanel.add(createErrorCard("Contact", "Administrator"));
                cardsPanel.add(createErrorCard("System", "Offline"));
            }
        } else {
            // Student not found
            cardsPanel.add(createDashboardCard("⚠️ Profile", "Not Found", 
                new Color(231, 76, 60), "Complete Registration"));
            
            cardsPanel.add(createDashboardCard("📋 Action", "Required", 
                new Color(231, 76, 60), "Contact Office"));
            
            cardsPanel.add(createDashboardCard("🎓 Status", "Inactive", 
                new Color(231, 76, 60), "Not Registered"));
            
            cardsPanel.add(createDashboardCard("📞 Help", "Needed", 
                new Color(231, 76, 60), "Administration"));
        }
        
        // Recent Activities Panel
        JPanel activitiesPanel = new JPanel(new BorderLayout());
        activitiesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recent Activities"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        activitiesPanel.setBackground(Color.WHITE);
        
        DefaultListModel<String> activitiesModel = new DefaultListModel<>();
        
        if (student != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
                
                // Add recent complaints
                List<Complaint> recentComplaints = complaintDAO.getComplaintsByStudent(student.getStudentId());
                if (recentComplaints != null) {
                    for (int i = 0; i < Math.min(recentComplaints.size(), 3); i++) {
                        Complaint c = recentComplaints.get(i);
                        if (c != null) {
                            String statusIcon = "Pending".equals(c.getStatus()) ? "⏳" : 
                                               "Resolved".equals(c.getStatus()) ? "✅" : "📝";
                            activitiesModel.addElement(statusIcon + " Complaint: " + 
                                (c.getComplaintType() != null ? c.getComplaintType() : "General") + 
                                " (" + sdf.format(c.getComplaintDate()) + ")");
                        }
                    }
                }
                
                // Add hostel application status
                List<RoomAllocationApplication> hostelApps = roomAllocDAO.getAllApplications();
                if (hostelApps != null) {
                    for (RoomAllocationApplication app : hostelApps) {
                        if (app.getStudent().getStudentId() == student.getStudentId()) {
                            if ("Pending".equals(app.getStatus())) {
                                activitiesModel.addElement("📋 Hostel Application: Pending Review");
                                break;
                            } else if ("AdminApproved".equals(app.getStatus())) {
                                activitiesModel.addElement("✅ Hostel Application: Approved");
                                break;
                            }
                        }
                    }
                }
                
                // Add fee challans
                List<FeeChallan> feeChallans = feeChallanDAO.getChallansByStudent(student.getStudentId());
                if (feeChallans != null && !feeChallans.isEmpty()) {
                    FeeChallan recentChallan = feeChallans.get(0);
                    if (recentChallan != null) {
                        String statusIcon = "Pending".equals(recentChallan.getStatus()) ? "💰" : 
                                           "Paid".equals(recentChallan.getStatus()) ? "✅" : "📄";
                        activitiesModel.addElement(statusIcon + " Fee Challan: " + 
                            recentChallan.getFeeType() + " - Rs." + recentChallan.getAmount() + 
                            " (" + sdf.format(recentChallan.getDueDate()) + ")");
                    }
                }
                
            } catch (Exception e) {
                activitiesModel.addElement("⚠️ Error loading activities");
            }
        }
        
        if (activitiesModel.isEmpty()) {
            activitiesModel.addElement("📭 No recent activities");
            activitiesModel.addElement("Submit your first complaint");
            activitiesModel.addElement("Apply for hostel accommodation");
            activitiesModel.addElement("Check your fee challans");
        }
        
        JList<String> activitiesList = new JList<>(activitiesModel);
        activitiesList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activitiesList.setBackground(Color.WHITE);
        activitiesList.setSelectionBackground(new Color(240, 240, 240));
        JScrollPane activitiesScroll = new JScrollPane(activitiesList);
        activitiesScroll.setBorder(null);
        
        activitiesPanel.add(activitiesScroll, BorderLayout.CENTER);
        
        contentPanel.add(cardsPanel, BorderLayout.NORTH);
        contentPanel.add(activitiesPanel, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDashboardCard(String title, String value, Color color, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        
        // Description
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
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("My Profile", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        if (student == null) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setBackground(new Color(245, 247, 250));
            
            JLabel errorLabel = new JLabel("<html><center><h3>⚠️ Profile Not Found</h3>" +
                "<p>Your student profile is not registered in the system.</p>" +
                "<p>Please contact the administration office to complete your registration.</p>" +
                "<p><b>Your Username:</b> " + currentUser.getUsername() + "</p>" +
                "<p><b>Role:</b> " + currentUser.getRole() + "</p></center></html>", 
                SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            
            panel.add(title, BorderLayout.NORTH);
            panel.add(errorPanel, BorderLayout.CENTER);
            return panel;
        }
        
        // Main profile content
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(new Color(245, 247, 250));
        
        // Left panel - Basic Info
        JPanel basicInfoPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        basicInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Basic Information"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        basicInfoPanel.setBackground(Color.WHITE);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        
        String[][] basicInfo = {
            {"Student ID:", student.getRegistrationNo() != null ? student.getRegistrationNo() : "N/A"},
            {"Full Name:", student.getFullName() != null ? student.getFullName() : "N/A"},
            {"Father's Name:", student.getFatherName() != null ? student.getFatherName() : "N/A"},
            {"CNIC:", student.getCnic() != null ? student.getCnic() : "N/A"},
            {"Department:", student.getDepartment() != null ? student.getDepartment() : "N/A"},
            {"Semester:", student.getSemester() != null ? student.getSemester() : "N/A"},
            {"Admission Date:", student.getAdmissionDate() != null ? dateFormat.format(student.getAdmissionDate()) : "N/A"},
            {"Status:", student.getStatus() != null ? student.getStatus() : "N/A"}
        };
        
        for (String[] data : basicInfo) {
            JLabel label = new JLabel(data[0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(new Color(80, 80, 80));
            basicInfoPanel.add(label);
            
            JLabel value = new JLabel(data[1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            basicInfoPanel.add(value);
        }
        
        // Right panel - Address and Additional Info
        JPanel addressPanel = new JPanel(new BorderLayout());
        addressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Contact Information"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        addressPanel.setBackground(Color.WHITE);
        
        JTextArea addressArea = new JTextArea(student.getAddress() != null ? student.getAddress() : "Address not provided");
        addressArea.setEditable(false);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressArea.setBackground(Color.WHITE);
        addressArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(null);
        
        addressPanel.add(addressScroll, BorderLayout.CENTER);
        
        mainContent.add(basicInfoPanel, BorderLayout.WEST);
        mainContent.add(addressPanel, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHostelApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Hostel Room Application", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        if (student == null) {
            JLabel errorLabel = new JLabel("<html><center><h3>Profile Required</h3>" +
                "<p>You must complete your student registration first.</p>" +
                "<p>Please contact the administration office.</p></center></html>", 
                SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(errorLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Check existing applications
        try {
            List<RoomAllocationApplication> allApps = roomAllocDAO.getAllApplications();
            
            boolean hasPending = false;
            boolean hasApproved = false;
            RoomAllocationApplication approvedApp = null;
            
            for (RoomAllocationApplication app : allApps) {
                if (app.getStudent().getStudentId() == student.getStudentId()) {
                    if ("Pending".equals(app.getStatus())) {
                        hasPending = true;
                        break;
                    } else if ("AdminApproved".equals(app.getStatus())) {
                        hasApproved = true;
                        approvedApp = app;
                        break;
                    }
                }
            }
            
            if (hasPending) {
                // Show pending status
                JPanel statusPanel = new JPanel(new BorderLayout());
                statusPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
                statusPanel.setBackground(Color.WHITE);
                
                JLabel statusLabel = new JLabel("<html><center><h2>⏳ Application Pending</h2>" +
                    "<p>Your hostel application is under review.</p>" +
                    "<p>Please wait for office verification and admin approval.</p>" +
                    "<p>You will be notified once a decision is made.</p></center></html>", 
                    SwingConstants.CENTER);
                statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                statusPanel.add(statusLabel, BorderLayout.CENTER);
                panel.add(statusPanel, BorderLayout.CENTER);
                return panel;
            }
            
            if (hasApproved && approvedApp != null) {
                // Show approved status
                JPanel statusPanel = new JPanel(new BorderLayout());
                statusPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
                statusPanel.setBackground(Color.WHITE);
                
                String roomInfo = approvedApp.getAllocatedRoom() != null ? 
                                 "Room: " + approvedApp.getAllocatedRoom().getRoomNumber() : 
                                 "Room will be assigned soon";
                
                JLabel statusLabel = new JLabel("<html><center><h2>✅ Application Approved</h2>" +
                    "<p>Your hostel application has been approved!</p>" +
                    "<p><b>" + roomInfo + "</b></p>" +
                    "<p>Please visit the hostel office for room key and further instructions.</p></center></html>", 
                    SwingConstants.CENTER);
                statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                statusPanel.add(statusLabel, BorderLayout.CENTER);
                panel.add(statusPanel, BorderLayout.CENTER);
                return panel;
            }
            
        } catch (Exception e) {
            System.err.println("Error checking applications: " + e.getMessage());
        }
        
        // Application Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("New Application Form"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
     // Get available hostels - FINAL FIX
     // Hostel selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Hostel:"), gbc);

        JComboBox<String> hostelCombo = new JComboBox<>();
        hostelCombo.addItem("-- Select Hostel --");

        // DIRECTLY LOAD HOSTELS WITHOUT FINAL VARIABLE
        try {
            List<Hostel> tempHostels = hostelDAO.getAllHostels();
            if (tempHostels != null && !tempHostels.isEmpty()) {
                for (Hostel hostel : tempHostels) {
                    if (hostel != null && hostel.getHostelName() != null) {
                        hostelCombo.addItem(hostel.getHostelName());
                    }
                }
            } else {
                // Add default options if no hostels in database
                hostelCombo.addItem("Boys Hostel A");
                hostelCombo.addItem("Boys Hostel B");
                hostelCombo.addItem("Girls Hostel A");
            }
        } catch (Exception ex) {
            System.err.println("Error loading hostels: " + ex.getMessage());
            // Add default options on error
            hostelCombo.addItem("Boys Hostel A");
            hostelCombo.addItem("Boys Hostel B");
            hostelCombo.addItem("Girls Hostel A");
        }

        hostelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(hostelCombo, gbc);

        // Hostel selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Hostel:"), gbc);

        
        hostelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(hostelCombo, gbc);
        
        // Room Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Room Type:"), gbc);
        
        JComboBox<String> roomCombo = new JComboBox<>(new String[]{"Single", "Double", "Triple"});
        roomCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(roomCombo, gbc);
        
        // Academic Year
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Academic Year:"), gbc);
        
        JTextField yearField = new JTextField("2024-2025");
        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(yearField, gbc);
        
        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Emergency Contact:"), gbc);
        
        JTextField contactField = new JTextField();
        contactField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(contactField, gbc);
        
        // Special Requirements
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Special Requirements:"), gbc);
        
        JTextArea requirementsArea = new JTextArea(4, 30);
        requirementsArea.setLineWrap(true);
        requirementsArea.setWrapStyleWord(true);
        requirementsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane requirementsScroll = new JScrollPane(requirementsArea);
        requirementsScroll.setPreferredSize(new Dimension(300, 80));
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(requirementsScroll, gbc);
        
        // Submit Button
        JButton submitBtn = new JButton("Submit Application");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBackground(new Color(41, 128, 185));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        submitBtn.addActionListener(e -> {
            if (!assertStudentExists()) return;
            
            // Validate fields
            if (hostelCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a hostel!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (contactField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please provide emergency contact number!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Create application object
                RoomAllocationApplication application = new RoomAllocationApplication();
                application.setStudent(student);
                
                // Hostel load karo lambda ke andar (NO OUTSIDE VARIABLE NEEDED)
                List<Hostel> hostelsList = hostelDAO.getAllHostels();
                
                // Find selected hostel
                Hostel selectedHostel = null;
                String selectedHostelName = (String) hostelCombo.getSelectedItem();
                
                if (hostelsList != null) {
                    for (Hostel hostel : hostelsList) {
                        if (hostel.getHostelName().equals(selectedHostelName)) {
                            selectedHostel = hostel;
                            break;
                        }
                    }
                }
                
                if (selectedHostel == null) {
                    selectedHostel = new Hostel();
                    selectedHostel.setHostelName(selectedHostelName);
                }
                
                application.setHostel(selectedHostel);
                application.setApplicationDate(new Date());
                application.setStatus("Pending");
                
                // Save to database
                boolean success = roomAllocDAO.createApplication(application);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        " Application submitted successfully!\n\n" +
                        "Application will be processed by:\n" +
                        "1. Office Verification\n" +
                        "2. Admin Approval\n" +
                        "3. Room Allocation\n\n" +
                        "You will be notified of the status.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear form
                    hostelCombo.setSelectedIndex(0);
                    contactField.setText("");
                    requirementsArea.setText("");
                    
                    // Refresh panel to show pending status
                    panel.removeAll();
                    panel.add(createHostelApplicationPanel());
                    panel.revalidate();
                    panel.repaint();
                } else {
                    String daoErr = roomAllocDAO.getLastErrorMessage();
                    String msg = daoErr != null ? daoErr : "Database save failed";
                    JOptionPane.showMessageDialog(this, 
                        "❌ Failed to submit application!\n" + msg,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Failed to submit application!\n" +
                    "Error: " + ex.getMessage() + "\n" +
                    "Please try again or contact support.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(submitBtn, gbc);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMessRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Mess Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        if (student == null) {
            JLabel errorLabel = new JLabel("Student profile required. Please complete registration first.", SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            return panel;
        }
        
        try {
            // Check existing registration
            List<MessRegistration> existingRegs = messRegistrationDAO.getActiveRegistrationsByStudent(student.getStudentId());
            
            if (!existingRegs.isEmpty() && !existingRegs.get(0).getStatus().equals("Ended")) {
                // Show current registration
                MessRegistration currentReg = existingRegs.get(0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                
                JPanel currentPanel = new JPanel(new BorderLayout());
                currentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Current Registration"),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
                currentPanel.setBackground(Color.WHITE);
                
                String infoText = "<html><center>" +
                    "<h2> Currently Registered</h2>" +
                    "<table align='center' cellpadding='10'>" +
                    "<tr><td><b>Meal Type:</b></td><td>" + currentReg.getMealType() + "</td></tr>" +
                    "<tr><td><b>Start Date:</b></td><td>" + dateFormat.format(currentReg.getStartDate()) + "</td></tr>" +
                    "<tr><td><b>End Date:</b></td><td>" + dateFormat.format(currentReg.getEndDate()) + "</td></tr>" +
                    "<tr><td><b>Status:</b></td><td>" + currentReg.getStatus() + "</td></tr>" +
                    "</table></center></html>";
                
                JLabel infoLabel = new JLabel(infoText, SwingConstants.CENTER);
                infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                JButton endBtn = new JButton("End Registration");
                endBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                endBtn.setBackground(new Color(231, 76, 60));
                endBtn.setForeground(new Color(60, 60, 60));
                endBtn.setFocusPainted(false);
                endBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                
                endBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to end your mess registration?\n" +
                        "This action cannot be undone.",
                        "Confirm End Registration", JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = messRegistrationDAO.endMessRegistration(currentReg.getRegistrationId());
                        if (success) {
                            JOptionPane.showMessageDialog(this, 
                                "✅ Mess registration ended successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Refresh panel
                            panel.removeAll();
                            panel.add(createMessRegistrationPanel());
                            panel.revalidate();
                            panel.repaint();
                        }
                    }
                });
                
                currentPanel.add(infoLabel, BorderLayout.CENTER);
                currentPanel.add(endBtn, BorderLayout.SOUTH);
                
                panel.add(title, BorderLayout.NORTH);
                panel.add(currentPanel, BorderLayout.CENTER);
                
                return panel;
            }
        } catch (Exception e) {
            System.err.println("Error checking mess registration: " + e.getMessage());
        }
        
        // Registration Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("New Registration"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Meal Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Meal Plan:"), gbc);
        
        JComboBox<String> mealCombo = new JComboBox<>(new String[]{
            "Vegetarian", "Non-Vegetarian", "Special Diet"
        });
        mealCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(mealCombo, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Duration:"), gbc);
        
        JComboBox<String> durationCombo = new JComboBox<>(new String[]{"1 Month", "3 Months", "6 Months", "Full Semester"});
        durationCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(durationCombo, gbc);
        
        // Start Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Start Date:"), gbc);
        
        JTextField startField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        startField.setEditable(false);
        startField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(startField, gbc);
        
        // Special Requirements
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Dietary Needs:"), gbc);
        
        JTextArea dietArea = new JTextArea(3, 30);
        dietArea.setLineWrap(true);
        dietArea.setWrapStyleWord(true);
        dietArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane dietScroll = new JScrollPane(dietArea);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(dietScroll, gbc);
        
        // Register Button
        JButton registerBtn = new JButton("Register for Mess");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setBackground(new Color(41, 128, 185));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        registerBtn.addActionListener(e -> {
            if (!assertStudentExists()) return;
             try {
                // Calculate month count
                String duration = (String) durationCombo.getSelectedItem();
                int monthCount = 1;
                if (duration.contains("3")) monthCount = 3;
                else if (duration.contains("6")) monthCount = 6;
                else if (duration.contains("Full")) monthCount = 4; // Assuming semester = 4 months
                
                // Create registration
                MessRegistration registration = new MessRegistration();
                registration.setStudent(student);
                registration.setMealType((String) mealCombo.getSelectedItem());
                registration.setMonthCount(monthCount);
                registration.setStartDate(new Date());
                
                // Calculate end date
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(java.util.Calendar.MONTH, monthCount);
                registration.setEndDate(cal.getTime());
                
                registration.setStatus("Active");
                
                // Save to database
                boolean success = messRegistrationDAO.registerForMess(registration);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Mess registration successful!\n\n" +
                        "Meal Plan: " + registration.getMealType() + "\n" +
                        "Duration: " + monthCount + " months\n" +
                        "Start Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(registration.getStartDate()) + "\n" +
                        "End Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(registration.getEndDate()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh panel
                    panel.removeAll();
                    panel.add(createMessRegistrationPanel());
                    panel.revalidate();
                    panel.repaint();
                } else {
                    String daoErr = messRegistrationDAO.getLastErrorMessage();
                    String msg = daoErr != null ? daoErr : "Database save failed";
                    JOptionPane.showMessageDialog(this, 
                        "❌ Registration failed!\n" + msg,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Registration failed!\nError: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(registerBtn, gbc);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createComplaintPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("Submit Complaint", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 60, 60));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        if (student == null) {
            JLabel errorLabel = new JLabel("Student profile required.", SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Main content panel
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(new Color(245, 247, 250));
        
        // Left panel - Complaint Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("New Complaint"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Complaint Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Electrical", "Plumbing", "Internet", "Cleaning", 
            "Furniture", "Noise", "Security", "Other"
        });
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(typeCombo, gbc);
        
        // Location
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Location:"), gbc);
        
        JTextField locationField = new JTextField();
        locationField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(locationField, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Priority:"), gbc);
        
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High", "Urgent"});
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(priorityCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        
        JTextArea descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(descArea);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(descScroll, gbc);
        
        // Submit Button
        JButton submitBtn = new JButton("Submit Complaint");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        submitBtn.setBackground(new Color(41, 128, 185));
        submitBtn.setForeground(new Color(60, 60, 60));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        submitBtn.addActionListener(e -> {
            if (!assertStudentExists()) return;
             if (locationField.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Please specify location!", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             
             if (descArea.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Please enter description!", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             
             try {
                 Complaint complaint = new Complaint();
                 complaint.setStudent(student);
                 complaint.setComplaintType((String) typeCombo.getSelectedItem());
                 complaint.setDescription("Location: " + locationField.getText() + 
                                       "\nPriority: " + priorityCombo.getSelectedItem() +
                                       "\n\nDescription:\n" + descArea.getText());
                 complaint.setComplaintDate(new Date());
                 complaint.setStatus("Pending");
                 
                 boolean success = complaintDAO.submitComplaint(complaint);
                 
                 if (success) {
                     JOptionPane.showMessageDialog(this, 
                         "✅ Complaint submitted!\nComplaint ID: CMP-" + System.currentTimeMillis(),
                         "Success", JOptionPane.INFORMATION_MESSAGE);
                     
                     // Clear form
                     locationField.setText("");
                     descArea.setText("");
                     typeCombo.setSelectedIndex(0);
                     priorityCombo.setSelectedIndex(0);
                     
                     // Refresh complaints table
                     refreshComplaintsTable(panel);
                 } else {
                    String daoErr = complaintDAO.getLastErrorMessage();
                    String msg = daoErr != null ? daoErr : "Failed to save complaint to database.";
                    JOptionPane.showMessageDialog(this,
                        "❌ Failed to submit complaint!\n" + msg,
                        "Error", JOptionPane.ERROR_MESSAGE);
                 }
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             }
        });
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(submitBtn, gbc);
        
        // Right panel - Recent Complaints
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recent Complaints"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        recentPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Type", "Date", "Status"};
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
        
        JScrollPane tableScroll = new JScrollPane(complaintsTable);
        tableScroll.setBorder(null);
        
        recentPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Load complaints
        loadComplaintsData(model);
        
        mainContent.add(formPanel);
        mainContent.add(recentPanel);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadComplaintsData(DefaultTableModel model) {
        if (student == null) return;
        
        try {
            List<Complaint> complaints = complaintDAO.getComplaintsByStudent(student.getStudentId());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
            
            model.setRowCount(0); // Clear existing data
            
            if (complaints != null) {
                for (Complaint c : complaints) {
                    if (c != null) {
                        model.addRow(new Object[]{
                            c.getComplaintType(),
                            sdf.format(c.getComplaintDate()),
                            c.getStatus()
                        });
                    }
                }
            }
            
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No complaints", "N/A", "N/A"});
            }
        } catch (Exception e) {
            System.err.println("Error loading complaints: " + e.getMessage());
            model.addRow(new Object[]{"Error loading", "N/A", "N/A"});
        }
    }
    
    private void refreshComplaintsTable(JPanel parentPanel) {
        // Find the table in the panel and refresh it
        Component[] components = parentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JScrollPane) {
                        JViewport viewport = ((JScrollPane) subComp).getViewport();
                        if (viewport.getView() instanceof JTable) {
                            JTable table = (JTable) viewport.getView();
                            if (table.getModel() instanceof DefaultTableModel) {
                                loadComplaintsData((DefaultTableModel) table.getModel());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
 // Add this method in StudentDashboard class (anywhere in the class)
    private JPanel createSummaryCard(String title, String value, Color color, String description) {
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
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
    
    private JPanel createFeeChallanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel title = new JLabel("My Fees", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        if (student == null) {
            JLabel errorLabel = new JLabel("Student profile required.", SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            return panel;
        }
        
        System.out.println("DEBUG: Current Student ID: " + student.getStudentId());
        
        // Initialize fee challan table
        try {
            feeChallanDAO.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing fee challan: " + e.getMessage());
        }
        
        // Check mess registration
        boolean hasMessRegistration = checkMessRegistration();
        System.out.println("DEBUG: Has mess registration: " + hasMessRegistration);
        
        // Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Always show Hostel Fee tab
        JPanel hostelPanel = createFeeDetailPanel("HOSTEL_FEE", hasMessRegistration);
        tabbedPane.addTab("🏠 Hostel Fee", hostelPanel);
        
        // Show Mess Fee tab based on registration
        if (hasMessRegistration) {
            JPanel messPanel = createFeeDetailPanel("MESS_FEE", hasMessRegistration);
            tabbedPane.addTab("🍽️ Mess Fee", messPanel);
        } else {
            tabbedPane.addTab("🍽️ Mess Fee", createNotRegisteredPanel());
        }
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
 // Add these methods in StudentDashboard class (anywhere in the class)

    private boolean checkMessRegistration() {
        try {
            if (student == null) return false;
            List<MessRegistration> registrations = messRegistrationDAO.getActiveRegistrationsByStudent(student.getStudentId());
            return registrations != null && !registrations.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking mess registration: " + e.getMessage());
            return false;
        }
    }

    private JPanel createNotRegisteredPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setBackground(Color.WHITE);
        
        JLabel message = new JLabel("<html><center><h2>🍽️ Mess Registration Required</h2>" +
            "<p>You are not currently registered for mess services.</p>" +
            "<p>Please register for mess to view and pay mess fees.</p>" +
            "<p>Go to <b>Mess Registration</b> section to register.</p></center></html>", 
            SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(message, BorderLayout.CENTER);
        return panel;
    }

    private double getFeeAmount(String feeType) {
        try {
            FeeStructureDAO feeStructureDAO = new FeeStructureDAO();
            FeeStructure feeStructure = feeStructureDAO.getFeeStructureByType(feeType);
            if (feeStructure != null) {
                return feeStructure.getAmount();
            }
        } catch (Exception e) {
            System.err.println("Error getting fee amount: " + e.getMessage());
        }
        
        // Default amounts if not found in database
        if ("HOSTEL_FEE".equals(feeType)) {
            return 15000.00;
        } else if ("MESS_FEE".equals(feeType)) {
            return 12000.00;
        }
        return 0.0;
    }

    private boolean generateFeeChallan(String feeType, double amount) {
        try {
            FeeChallan challan = new FeeChallan();
            challan.setStudentId(student.getStudentId());
            challan.setStudent(student);
            challan.setFeeType(feeType);
            challan.setAmount(amount);
            challan.setIssueDate(new Date());
            
            // Set due date (30 days from now)
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
            challan.setDueDate(cal.getTime());
            
            challan.setStatus("PENDING");
            challan.setAcademicYear("2024-2025");
            
            System.out.println("DEBUG: Generating challan for student ID: " + student.getStudentId());
            System.out.println("Fee Type: " + feeType);
            System.out.println("Amount: " + amount);
            
            return feeChallanDAO.generateChallan(challan);
            
        } catch (Exception e) {
            System.err.println("Error generating fee challan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkIfFeeGenerated(String feeType) {
        try {
            if (student == null) return false;
            List<FeeChallan> challans = feeChallanDAO.getChallansByStudent(student.getStudentId());
            if (challans != null) {
                for (FeeChallan challan : challans) {
                    if (challan != null && challan.getFeeType() != null && 
                        challan.getFeeType().equals(feeType)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking fee generation: " + e.getMessage());
        }
        return false;
    }

    private void loadChallansForFeeType(DefaultTableModel model, String feeType) {
        try {
            if (student == null) {
                model.addRow(new Object[]{"Student profile not found", "", "", "", "", "", ""});
                return;
            }
            
            List<FeeChallan> challans = feeChallanDAO.getChallansByStudent(student.getStudentId());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            
            model.setRowCount(0);
            
            int count = 0;
            if (challans != null && !challans.isEmpty()) {
                for (FeeChallan challan : challans) {
                    if (challan != null && challan.getFeeType() != null && 
                        challan.getFeeType().equals(feeType)) {
                        model.addRow(new Object[]{
                            challan.getChallanId(),
                            "Rs. " + String.format("%.2f", challan.getAmount()),
                            sdf.format(challan.getIssueDate()),
                            sdf.format(challan.getDueDate()),
                            challan.getStatus() != null ? challan.getStatus() : "PENDING",
                            challan.getTransactionId() != null ? challan.getTransactionId() : "N/A",
                            "Mark as Paid"
                        });
                        count++;
                    }
                }
            }
            
            System.out.println("DEBUG: Loaded " + count + " challans for " + feeType);
            
            if (count == 0) {
                model.addRow(new Object[]{"No challans generated", "", "", "", "", "", ""});
            }
            
        } catch (Exception e) {
            System.err.println("Error loading challans: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", "", "", "", "", "", ""});
        }
    }

    private void markChallanAsPaid(int challanId, DefaultTableModel model, String feeType) {
        String transactionId = JOptionPane.showInputDialog(this,
            "Enter Transaction/Reference ID:",
            "Mark as Paid",
            JOptionPane.QUESTION_MESSAGE);
        
        if (transactionId != null && !transactionId.trim().isEmpty()) {
            try {
                boolean success = feeChallanDAO.markChallanAsPaid(challanId, transactionId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Fee marked as paid!\n" +
                        "Transaction ID: " + transactionId + "\n" +
                        "Status: Waiting for Admin Verification\n\n" +
                        "Admin will verify your payment shortly.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadChallansForFeeType(model, feeType);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Failed to mark as paid!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFeeDetailPanel(String feeType, boolean hasMessRegistration) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Get fee amount
        double feeAmount = getFeeAmount(feeType);
        
        // Top panel - Summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        
        summaryPanel.add(createSummaryCard("Fee Type", feeType, 
            new Color(52, 152, 219), "Type of Fee"));
        
        summaryPanel.add(createSummaryCard("Amount Due", 
            "Rs. " + String.format("%.2f", feeAmount), 
            new Color(231, 76, 60), "To be paid"));
        
        // Check if fee already generated
        boolean feeGenerated = checkIfFeeGenerated(feeType);
        String statusText = feeGenerated ? "Generated" : "Not Generated";
        Color statusColor = feeGenerated ? new Color(46, 204, 113) : new Color(241, 196, 15);
        
        summaryPanel.add(createSummaryCard("Status", statusText, 
            statusColor, "Fee Challan Status"));
        
        // Center panel - Generated Challans Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Generated Challans"));
        centerPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Challan ID", "Amount", "Issue Date", "Due Date", "Status", "Transaction ID", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable challanTable = new JTable(model);
        challanTable.setRowHeight(30);
        challanTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        challanTable.getTableHeader().setBackground(new Color(240, 240, 240));
        challanTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Load data
        loadChallansForFeeType(model, feeType);
        
        JScrollPane scrollPane = new JScrollPane(challanTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton generateBtn = new JButton("📄 Generate New Challan");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generateBtn.setBackground(new Color(41, 128, 185));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        
        JButton markPaidBtn = new JButton("💰 Mark as Paid");
        markPaidBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        markPaidBtn.setBackground(new Color(46, 204, 113));
        markPaidBtn.setForeground(Color.WHITE);
        markPaidBtn.setFocusPainted(false);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Button actions
        generateBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Generate new fee challan for " + feeType + "?\n" +
                "Amount: Rs. " + String.format("%.2f", feeAmount),
                "Confirm", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = generateFeeChallan(feeType, feeAmount);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Fee challan generated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadChallansForFeeType(model, feeType);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Failed to generate challan!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        markPaidBtn.addActionListener(e -> {
            int selectedRow = challanTable.getSelectedRow();
            if (selectedRow >= 0) {
                Object challanIdObj = model.getValueAt(selectedRow, 0);
                if (challanIdObj instanceof Integer) {
                    int challanId = (Integer) challanIdObj;
                    markChallanAsPaid(challanId, model, feeType);
                } else if (challanIdObj instanceof String) {
                    String str = (String) challanIdObj;
                    if (!str.contains("No challans") && !str.contains("Error")) {
                        try {
                            int challanId = Integer.parseInt(str);
                            markChallanAsPaid(challanId, model, feeType);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this,
                                "Please select a valid challan!",
                                "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a challan to mark as paid!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshBtn.addActionListener(e -> {
            loadChallansForFeeType(model, feeType);
            JOptionPane.showMessageDialog(this,
                "Challans list refreshed!",
                "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(generateBtn);
        buttonPanel.add(markPaidBtn);
        buttonPanel.add(refreshBtn);
        
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper to ensure student profile is loaded
    private boolean assertStudentExists() {
        if (student == null) {
            JOptionPane.showMessageDialog(this,
                "Student profile required to perform this action.\nPlease contact administration to create your student profile.",
                "Profile Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}