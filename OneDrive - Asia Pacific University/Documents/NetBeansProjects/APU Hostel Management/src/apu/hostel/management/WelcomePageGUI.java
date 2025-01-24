package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WelcomePageGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private APUHostelManagement management;
    private static String currentResidentID; // Add this line

    public static String getCurrentResidentID() { // Add this method
        return currentResidentID;
    }

    public WelcomePageGUI() {
        management = new APUHostelManagement();
    
        setTitle("APU Hostel Management System");
        setSize(1024, 768); // Adjusted size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
    
        JPanel roleSelectionPanel = createRoleSelectionPanel();
        JPanel staffAuthCodePanel = createAuthCodePanel("Staff Authorization Code", "StaffLogin");
        JPanel managerAuthCodePanel = createAuthCodePanel("Manager Authorization Code", "ManagerLogin");
        JPanel residentLoginPanel = createLoginPanel("Resident Login Page", "ResidentMenu");
        JPanel staffLoginPanel = createLoginPanel("Staff Login Page", "StaffMenu");
        JPanel residentRegistrationPanel = createRegistrationPanel("Resident Registration", "registerResident");
        JPanel staffRegistrationPanel = createRegistrationPanel("Staff Registration", "registerStaff");
        JPanel managerLoginPanel = createLoginPanel("Manager Login Page", "ManagerMenu");
        JPanel managerRegistrationPanel = createRegistrationPanel("Manager Registration", "registerManager");

    
        mainPanel.add(roleSelectionPanel, "RoleSelection");
        mainPanel.add(staffAuthCodePanel, "StaffAuthCode");
        mainPanel.add(managerAuthCodePanel, "ManagerAuthCode");
        mainPanel.add(residentLoginPanel, "ResidentLogin");
        mainPanel.add(staffLoginPanel, "StaffLogin");
        mainPanel.add(residentRegistrationPanel, "ResidentRegistration");
        mainPanel.add(staffRegistrationPanel, "StaffRegistration");
        mainPanel.add(managerLoginPanel, "ManagerLogin");
        mainPanel.add(managerRegistrationPanel, "ManagerRegistration"); // Ensure this line is correct

    
        add(mainPanel);
        cardLayout.show(mainPanel, "RoleSelection");
    }

    private JPanel createRoleSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to APU Hostel Management Fees Payment System (AHMFPS)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Adjusted font size
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(48, 0, 0, 0)); // Adjusted space above the welcome message
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 19, 19)); // Adjusted to 5 rows
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38)); // Adjusted border

        JLabel label = new JLabel("Select Your Role:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        JButton managerButton = new JButton("Manager");
        JButton staffButton = new JButton("Staff");
        JButton residentButton = new JButton("Resident");

        managerButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        staffButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        residentButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size

        managerButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size
        staffButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size
        residentButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size

        managerButton.addActionListener(e -> cardLayout.show(mainPanel, "ManagerAuthCode"));
        staffButton.addActionListener(e -> cardLayout.show(mainPanel, "StaffAuthCode"));
        residentButton.addActionListener(e -> cardLayout.show(mainPanel, "ResidentLogin"));

        centerPanel.add(label);
        centerPanel.add(managerButton);
        centerPanel.add(staffButton);
        centerPanel.add(residentButton);
        centerPanel.add(Box.createVerticalStrut(38)); // Adjusted space below the buttons

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAuthCodePanel(String title, String nextCard) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "RoleSelection"));
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 19, 19)); // Adjusted grid layout
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38)); // Adjusted border

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Adjusted font size

        JTextField authCodeField = new JTextField();
        authCodeField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        authCodeField.setForeground(Color.GRAY);
        authCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (authCodeField.getText().equals(title)) {
                    authCodeField.setText("");
                    authCodeField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (authCodeField.getText().isEmpty()) {
                    authCodeField.setForeground(Color.GRAY);
                    authCodeField.setText(title);
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        submitButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size
        submitButton.addActionListener(e -> {
            String authCode = authCodeField.getText();
            String role = title.contains("Staff") ? "staff" : "manager";
            if (APUHostelManagement.isValidAuthCode(authCode, role)) {
                cardLayout.show(mainPanel, nextCard);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid authorization code. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        centerPanel.add(titleLabel);
        centerPanel.add(authCodeField);
        centerPanel.add(submitButton);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Add a component listener to reset the auth code field when the panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                authCodeField.setText(title);
                authCodeField.setForeground(Color.GRAY);
            }
        });
        
        return panel;
        }

    private JPanel createLoginPanel(String title, String menuCard) {
        JPanel panel = new JPanel(new BorderLayout());
    
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "RoleSelection"));
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);
    
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 19, 19)); // Adjusted grid layout
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38)); // Adjusted border
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
    
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Adjusted font size
        centerPanel.add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
    
        JTextField usernameField = new JTextField("Username");
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        usernameField.setForeground(Color.GRAY);
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().equals("Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setForeground(Color.GRAY);
                    usernameField.setText("Username");
                }
            }
        });
        centerPanel.add(usernameField, gbc);
    
        gbc.gridy++;
        gbc.gridwidth = 1;
        JPasswordField passwordField = new JPasswordField("Password");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("Password");
                    passwordField.setEchoChar((char) 0);
                }
            }
        });

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setPreferredSize(new Dimension(300, 40)); // Adjust size as needed
        passwordPanel.add(passwordField, BorderLayout.CENTER);
    
        JLabel showHideIcon = new JLabel(new ImageIcon(new ImageIcon("images/show_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Use appropriate path to the icon
        showHideIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideIcon.setPreferredSize(new Dimension(40, 40)); // Set the size of the icon to match the height of the password field
        showHideIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (passwordField.getEchoChar() == '*') {
                    passwordField.setEchoChar((char) 0);
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/hide_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Use appropriate path to the hide icon
                } else {
                    passwordField.setEchoChar('*');
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Use appropriate path to the show icon
                }
            }
        });
        passwordPanel.add(showHideIcon, BorderLayout.EAST);
        centerPanel.add(passwordPanel, gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;  
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        loginButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean loginSuccess = false;
            try {
                if (title.equals("Resident Login Page")) {
                    APUHostelManagement.User user = APUHostelManagement.User.findUser(username, password, "approved_residents.txt");
                    if (user != null && user.getRole().equals("resident")) {
                        loginSuccess = true;
                        APUHostelManagement.Resident resident = (APUHostelManagement.Resident) user; // Cast to Resident
                        new ResidentMainPageGUI(resident); // Launch ResidentMainPageGUI with resident info
                        dispose(); // Close current window
                    }
                } else if (title.equals("Staff Login Page")) {
                    APUHostelManagement.User user = APUHostelManagement.User.findUser(username, password, "approved_staffs.txt");
                    if (user != null && user.getRole().equals("staff")) {
                        loginSuccess = true;
                        APUHostelManagement.Staff staff = (APUHostelManagement.Staff) user; // Cast to Staff
                        new StaffMainPageGUI(staff); // Launch StaffMainPageGUI with staff info
                        dispose(); // Close current window
                    }
                } else if (title.equals("Manager Login Page")) {
                    APUHostelManagement.User user = APUHostelManagement.User.findUser(username, password, "approved_managers.txt");
                    if (user != null && user.getRole().equals("manager")) {
                        loginSuccess = true;
                        APUHostelManagement.Manager manager = (APUHostelManagement.Manager) user; // Cast to Manager
                        new ManagerMainPageGUI(manager); // Launch ManagerMainPageGUI with manager info
                        dispose(); // Close current window
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (!loginSuccess) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        centerPanel.add(loginButton, gbc);
    
        gbc.gridy++;
        JLabel registerLabel = new JLabel("<html>Don't have an account? <a href=''>Register here</a></html>", SwingConstants.CENTER);
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (title.equals("Resident Login Page")) {
                    cardLayout.show(mainPanel, "ResidentRegistration");
                } else if (title.equals("Staff Login Page")) {
                    cardLayout.show(mainPanel, "StaffRegistration");
                } else if (title.equals("Manager Login Page")) {
                    cardLayout.show(mainPanel, "ManagerRegistration");
                }
            }
        });
    
        centerPanel.add(titleLabel);
        centerPanel.add(usernameField);
        centerPanel.add(passwordField);
        centerPanel.add(loginButton);
        centerPanel.add(registerLabel, gbc);
    
        panel.add(centerPanel, BorderLayout.CENTER);
    
        // Add a component listener to reset the fields when the panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                usernameField.setText("Username");
                usernameField.setForeground(Color.GRAY);
                passwordField.setText("Password");
                passwordField.setForeground(Color.GRAY);
                passwordField.setEchoChar((char) 0);
                showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Reset to show icon
            }
        });
    
        return panel;
    }

    private JPanel createRegistrationPanel(String title, String registerMethod) {
        JPanel panel = new JPanel(new BorderLayout());
    
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(e -> {
            if (registerMethod.equals("registerManager")) {
                cardLayout.show(mainPanel, "ManagerLogin");
            } else if (registerMethod.equals("registerResident")) {
                cardLayout.show(mainPanel, "ResidentLogin");
            } else if (registerMethod.equals("registerStaff")) {
                cardLayout.show(mainPanel, "StaffLogin");
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);
    
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 19, 19)); // Adjusted grid layout
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38)); // Adjusted border
    
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Adjusted font size
    
        JTextField icPassportField = new JTextField("IC/Passport Number");
        icPassportField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        icPassportField.setForeground(Color.GRAY);
        icPassportField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (icPassportField.getText().equals("IC/Passport Number")) {
                    icPassportField.setText("");
                    icPassportField.setForeground(Color.BLACK);
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (icPassportField.getText().isEmpty()) {
                    icPassportField.setForeground(Color.GRAY);
                    icPassportField.setText("IC/Passport Number");
                }
            }
        });
    
        JTextField usernameField = new JTextField("Create Username");
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        usernameField.setForeground(Color.GRAY);
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().equals("Create Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setForeground(Color.GRAY);
                    usernameField.setText("Create Username");
                }
            }
        });
    
        JPasswordField passwordField = new JPasswordField("Create Password");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals("Create Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("Create Password");
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    
        JTextField contactNumberField = new JTextField("Contact Number");
        contactNumberField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        contactNumberField.setForeground(Color.GRAY);
        contactNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (contactNumberField.getText().equals("Contact Number")) {
                    contactNumberField.setText("");
                    contactNumberField.setForeground(Color.BLACK);
                }
            }
    
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (contactNumberField.getText().isEmpty()) {
                    contactNumberField.setForeground(Color.GRAY);
                    contactNumberField.setText("Contact Number");
                }
            }
        });
    
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        registerButton.setPreferredSize(new Dimension(171, 57)); // Adjusted button size
        registerButton.addActionListener(e -> {
            String icPassportNumber = icPassportField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String contactNumber = contactNumberField.getText();

            // Scenario 1: Check if all fields are filled
            if (icPassportNumber.isEmpty() || username.isEmpty() || password.isEmpty() || contactNumber.isEmpty()) {
                showErrorDialog("Please fill in all fields.");
                return;
            }

            // Scenario 2: Check for existing data
            try {
                if (!APUHostelManagement.isUnique(icPassportNumber, "", "")) {
                    showErrorDialog(icPassportNumber.length() == 14 ? "IC number already exists. Please use a different IC number." : "Passport number already exists. Please use a different Passport number.");
                    return;
                }
                if (!APUHostelManagement.isUnique("", username, "")) {
                    showErrorDialog("Username already exists. Please use a different Username.");
                    return;
                }
                if (!APUHostelManagement.isUnique("", "", contactNumber)) {
                    showErrorDialog("Contact Number already exists. Please use a different Contact Number.");
                    return;
                }
            } catch (IOException ex) {
                showErrorDialog("An error occurred while checking the existing data. Please try again.");
                return;
            }

            // Scenario 3: Validate format of each field
            if (!APUHostelManagement.isValidICPassport(icPassportNumber)) {
                showErrorDialog("Invalid IC/Passport Number. IC format: xxxxxx-xx-xxxx, Passport format: one alphabet followed by 8 numbers.");
                return;
            }
            if (!APUHostelManagement.isValidUsername(username)) {
                showErrorDialog("Invalid Username. Username must be between 3 and 12 characters long, contain only letters, numbers, and underscores, and must contain at least one letter.");
                return;
            }
            if (!APUHostelManagement.isValidPassword(password, username)) {
                showErrorDialog("Invalid Password. Password must be between 8 and 12 characters long, contain at least one number, one special character (!@#$%^&*()), and one uppercase letter, and cannot be similar to the username.");
                return;
            }
            if (!APUHostelManagement.isValidContactNumber(contactNumber)) {
                showErrorDialog("Invalid Contact Number. The correct format is 01X-XXX-XXXX.");
                return;
            }

            boolean success = false;
            if (registerMethod.equals("registerManager")) {
                success = APUHostelManagement.registerManager(icPassportNumber, username, password, contactNumber);
            } else if (registerMethod.equals("registerResident")) {
                success = APUHostelManagement.registerResident(icPassportNumber, username, password, contactNumber);
            } else if (registerMethod.equals("registerStaff")) {
                success = APUHostelManagement.registerStaff(icPassportNumber, username, password, contactNumber);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful.");
                cardLayout.show(mainPanel, "RoleSelection");
            } else {
                showErrorDialog("Registration failed. Please try again.");
            }
        });
    
        centerPanel.add(titleLabel);
        centerPanel.add(icPassportField);
        centerPanel.add(usernameField);
        centerPanel.add(passwordField);
        centerPanel.add(contactNumberField);
        centerPanel.add(registerButton);
    
        panel.add(centerPanel, BorderLayout.CENTER);
    
        // Add a component listener to reset the fields when the panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                icPassportField.setText("IC/Passport Number");
                icPassportField.setForeground(Color.GRAY);
                usernameField.setText("Create Username");
                usernameField.setForeground(Color.GRAY);
                passwordField.setText("Create Password");
                passwordField.setForeground(Color.GRAY);
                passwordField.setEchoChar((char) 0);
                contactNumberField.setText("Contact Number");
                contactNumberField.setForeground(Color.GRAY);
            }
        });
    
        return panel;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePageGUI frame = new WelcomePageGUI();
            frame.setVisible(true);
        });
    }
}