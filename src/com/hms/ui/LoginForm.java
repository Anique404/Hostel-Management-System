package com.hms.ui;

import com.hms.dao.UserDAO;
import com.hms.models.User;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField usernameField; // Changed from emailField
    private JPasswordField passwordField;
    private JButton loginBtn;
    private UserDAO userDAO;

    public LoginForm() {
        userDAO = new UserDAO(); // Initialize DAO
        
        setTitle("Hostel Management System - Login");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(240, 244, 248));

        // Main card
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(550, 320));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // LEFT: Image + title
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(33, 150, 243));

        // Hostel title area
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setBorder(BorderFactory.createEmptyBorder(25, 20, 15, 20));
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));

        JLabel appTitle = new JLabel("Hostel Management System");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel appSub = new JLabel("Login with your credentials");
        appSub.setForeground(new Color(220, 235, 255));
        appSub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        leftTop.add(appTitle);
        leftTop.add(Box.createVerticalStrut(5));
        leftTop.add(appSub);

        // Fake image block (simulating hostel/photo banner)
        JPanel imageBlock = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                Color c1 = new Color(25, 118, 210);
                Color c2 = new Color(144, 202, 249);
                g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(25, 20, w - 50, h - 60, 18, 18);
                g2.setColor(new Color(33, 150, 243));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                String txt = "Welcome to HMS";
                int sw = g2.getFontMetrics().stringWidth(txt);
                g2.drawString(txt, (w - sw) / 2, h / 2);
                g2.dispose();
            }
        };
        imageBlock.setOpaque(false);
        imageBlock.setPreferredSize(new Dimension(260, 220));
        imageBlock.setBorder(BorderFactory.createEmptyBorder(0, 15, 20, 15));

        leftPanel.add(leftTop, BorderLayout.NORTH);
        leftPanel.add(imageBlock, BorderLayout.CENTER);

        // RIGHT: Login form
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        rightPanel.setBackground(Color.WHITE);

        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginTitle.setForeground(new Color(55, 71, 79));

        JLabel loginSubtitle = new JLabel("Enter your username and password");
        loginSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginSubtitle.setForeground(new Color(120, 130, 135));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(loginTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(loginSubtitle);

        rightPanel.add(titlePanel, BorderLayout.NORTH);

        // Form fields - Changed labels to Username
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel usernameLabel = new JLabel("Username:"); // Changed from Email
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usernameField = new JTextField(); // Changed from emailField
        styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordField = new JPasswordField();
        styleTextField(passwordField);

        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Button + hint
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        loginBtn = new JButton("Login to Dashboard");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> performLogin());

        bottomPanel.add(loginBtn, BorderLayout.CENTER);

        JLabel hint = new JLabel("", SwingConstants.LEFT);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(new Color(130, 130, 130));
        bottomPanel.add(hint, BorderLayout.SOUTH);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Put left and right into card
        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);

        // Add card to background
        GridBagConstraints bgc = new GridBagConstraints();
        bgc.gridx = 0;
        bgc.gridy = 0;
        bgc.weightx = 1;
        bgc.weighty = 1;
        bgc.anchor = GridBagConstraints.CENTER;
        bg.add(card, bgc);

        setContentPane(bg);
        setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.BLUE); // Changed to white
        button.setBackground(new Color(33, 150, 243));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter username and password!", 
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Authenticate user from database
        User user = userDAO.validateLogin(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Login Successful!\nWelcome " + user.getUsername() + 
                "\nRole: " + user.getRole(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            openDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password!\n" +
                "Try: please provide correct username and password",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        this.dispose();
        
        switch (user.getRole()) {
            case "Admin":
                new AdminDashboard(user);
                break;
            case "Student":
                new StudentDashboard(user);
                break;
            case "Warden":
                new WardenDashboard(user);
                break;
            case "Office":
                new OfficeDashboard(user);
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Unknown user role: " + user.getRole(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(LoginForm::new);
    }
}