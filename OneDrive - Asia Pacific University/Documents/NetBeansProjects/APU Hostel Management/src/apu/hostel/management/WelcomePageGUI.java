package apu.hostel.management;

import javax.swing.*;
import apu.hostel.management.APUHostelManagement.User;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;


public class WelcomePageGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public WelcomePageGUI() {
        new APUHostelManagement();
    
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

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> {
            if (e.isAltDown()) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_HOME:
                        cardLayout.show(mainPanel, "RoleSelection");
                        return true;
                    case KeyEvent.VK_LEFT:
                        // Handle back navigation for each panel
                        if (mainPanel.getComponent(0).isVisible()) {
                            return true; // Already at home
                        }
                        cardLayout.previous(mainPanel);
                        return true;
                }
            }
            return false;
        });
    }

    private JPanel createRoleSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                setTitle("APU Hostel Management System - Role Selection");
            }
        });

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

        managerButton.setToolTipText("Login as Manager (Alt+M)");
        staffButton.setToolTipText("Login as Staff (Alt+S)");
        residentButton.setToolTipText("Login as Resident (Alt+R)");

        addButtonHoverEffect(managerButton);
        addButtonHoverEffect(staffButton); 
        addButtonHoverEffect(residentButton);

        managerButton.setMnemonic(KeyEvent.VK_M);  // Alt+M
        staffButton.setMnemonic(KeyEvent.VK_S);    // Alt+S
        residentButton.setMnemonic(KeyEvent.VK_R); // Alt+R

        return panel;
    }

    private JPanel createAuthCodePanel(String title, String nextCard) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                setTitle("APU Hostel Management System - " + title); 
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "RoleSelection"));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "back");
        panel.getActionMap().put("back", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override 
            public void actionPerformed(ActionEvent e) {
                backButton.doClick();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 19, 19)); // Adjusted grid layout
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38)); // Adjusted border

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Adjusted font size

        JTextField authCodeField = new JTextField();
        authCodeField.setFont(new Font("Arial", Font.PLAIN, 24)); // Adjusted font size
        authCodeField.setForeground(Color.GRAY);
        authCodeField.setBorder(BorderFactory.createCompoundBorder(
            authCodeField.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
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
        authCodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitButton.doClick();
                }
            }
        });
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
                SwingUtilities.invokeLater(() -> authCodeField.requestFocusInWindow());
            }
        });

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(submitButton);
        backButton.setMnemonic(KeyEvent.VK_B);     // Alt+B
        backButton.setToolTipText("Go back (Alt+B)");
        submitButton.setToolTipText("Submit the authorization code (Alt+S)");
        submitButton.setMnemonic(KeyEvent.VK_S);  // Alt+S
        
        return panel;
        }

    private JPanel createLoginPanel(String title, String menuCard) {
        JPanel panel = new JPanel(new BorderLayout());
    
        // Back button panel at top
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21));
        backButton.setPreferredSize(new Dimension(102, 57));
        backButton.setMaximumSize(new Dimension(102, 57));
        backButton.setMinimumSize(new Dimension(102, 57));
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "RoleSelection"));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "back");
        panel.getActionMap().put("back", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override 
            public void actionPerformed(ActionEvent e) {
                backButton.doClick();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);
    
        // Center panel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38));
        centerPanel.setPreferredSize(new Dimension(600, 400));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0; // Allow horizontal stretching
        gbc.weighty = 0.0; // Don't stretch vertically
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
    
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
    
        // Username section
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(usernameLabel, gbc);
    
        gbc.gridy++;
        gbc.gridwidth = 2;
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameField.setForeground(Color.GRAY);
        usernameField.setText("Username");
        usernameField.setPreferredSize(new Dimension(300, 35));
        centerPanel.add(usernameField, gbc);
    
        // Username field placeholder behavior
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (usernameField.getText().equals("Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setForeground(Color.GRAY);
                    usernameField.setText("Username");
                }
            }
        });
    
        // Password section
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(passwordLabel, gbc);
    
        gbc.gridy++;
        gbc.gridwidth = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24));
        passwordField.setForeground(Color.GRAY);
        passwordField.setText("Password");
        passwordField.setEchoChar((char) 0);
        passwordField.setPreferredSize(new Dimension(300, 35));
        centerPanel.add(passwordField, gbc);
    
        // Show/Hide icon
        JLabel showHideIcon = new JLabel(new ImageIcon(new ImageIcon("images/show_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        showHideIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideIcon.setPreferredSize(new Dimension(35, 35));
        gbc.gridx = 1;
        centerPanel.add(showHideIcon, gbc);
    
        // Password field placeholder and toggle behavior
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                }
            }
            public void focusLost(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("Password");
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    
        showHideIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (passwordField.getEchoChar() == '*') {
                    passwordField.setEchoChar((char) 0);
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/hide_icon.png")
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } else {
                    passwordField.setEchoChar('*');
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png")
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                }
            }
        });
    
        // Login button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 24));
        loginButton.setPreferredSize(new Dimension(120, 35));
        centerPanel.add(loginButton, gbc);
    
        // Register link
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Don't have an account? Register here");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);
        centerPanel.add(registerButton, gbc);
    
        // Enter key handlers
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });
    
        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = null;
    
            try {
                if (title.equals("Resident Login Page")) {
                    user = APUHostelManagement.loginResident(username, password);
                } else if (title.equals("Staff Login Page")) {
                    user = APUHostelManagement.loginStaff(username, password);
                } else if (title.equals("Manager Login Page")) {
                    user = APUHostelManagement.loginManager(username, password);
                }
    
                if (user != null) {
                    if (user.getIsActive()) {
                        if (user instanceof APUHostelManagement.Resident) {
                            new ResidentMainPageGUI((APUHostelManagement.Resident) user);
                        } else if (user instanceof APUHostelManagement.Staff) {
                            new StaffMainPageGUI((APUHostelManagement.Staff) user);
                        } else if (user instanceof APUHostelManagement.Manager) {
                            new ManagerMainPageGUI((APUHostelManagement.Manager) user);
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Your account is deactivated. Please contact the administrator.",
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", 
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during login", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // Register button action
        registerButton.addActionListener(e -> {
            if (title.equals("Resident Login Page")) {
                cardLayout.show(mainPanel, "ResidentRegistration");
            } else if (title.equals("Staff Login Page")) {
                cardLayout.show(mainPanel, "StaffRegistration");
            } else if (title.equals("Manager Login Page")) {
                cardLayout.show(mainPanel, "ManagerRegistration");
            }
        });
    
        // Reset fields when panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                usernameField.setText("Username");
                usernameField.setForeground(Color.GRAY);
                passwordField.setText("Password");
                passwordField.setForeground(Color.GRAY);
                passwordField.setEchoChar((char) 0);
                showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png")
                    .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
                setTitle("APU Hostel Management System - " + title);
            }
        });
    
        // Button effects and mnemonics
        addButtonHoverEffect(backButton);
        addButtonHoverEffect(loginButton);
    
        backButton.setMnemonic(KeyEvent.VK_B);
        loginButton.setMnemonic(KeyEvent.VK_L);
        registerButton.setMnemonic(KeyEvent.VK_R);
    
        // Tooltips
        backButton.setToolTipText("Go back (Alt+B)");
        loginButton.setToolTipText("Login (Alt+L)");
        registerButton.setToolTipText("Register (Alt+R)");
        usernameField.setToolTipText("3-12 characters, letters, numbers and underscore only");
        passwordField.setToolTipText("8-12 chars with uppercase, number & special char");
    
        // Add focus highlights
        addFocusHighlight(usernameField);
        addFocusHighlight(passwordField);
    
        panel.add(centerPanel, BorderLayout.CENTER);
    
        return panel;
    }

    private JPanel createRegistrationPanel(String title, String registerMethod) {
        JPanel panel = new JPanel(new BorderLayout());
    
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21));
        backButton.setPreferredSize(new Dimension(102, 57));
        backButton.setMaximumSize(new Dimension(102, 57));
        backButton.setMinimumSize(new Dimension(102, 57));
        
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
    
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 0.0; // Allow horizontal stretching
        gbc.weighty = 0.0; // Don't stretch vertically
        gbc.fill = GridBagConstraints.NONE;
    
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        centerPanel.add(titleLabel, gbc);
    
        // Format requirements hyperlink
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel formatLink = new JLabel("<html><u>View Format Requirements</u></html>");
        formatLink.setHorizontalAlignment(SwingConstants.CENTER);
        formatLink.setForeground(Color.BLUE);
        formatLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formatLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showFormatRequirements();
            }
        });
        gbc.gridy++;
        centerPanel.add(formatLink, gbc);
    
        // IC/Passport section
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel icPassportLabel = createFieldLabel("IC/Passport Number:");
        centerPanel.add(icPassportLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JTextField icPassportField = new JTextField();
        icPassportField.setFont(new Font("Arial", Font.PLAIN, 24));
        icPassportField.setForeground(Color.GRAY);
        icPassportField.setText("IC/Passport Number");
        icPassportField.setPreferredSize(new Dimension(300, 40));
        icPassportField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        centerPanel.add(icPassportField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JLabel icPassportIconLabel = new JLabel();
        icPassportIconLabel.setPreferredSize(new Dimension(30, 30));
        centerPanel.add(icPassportIconLabel, gbc);
    
        // IC/Passport field placeholder behavior
        icPassportField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (icPassportField.getText().equals("IC/Passport Number")) {
                    icPassportField.setText("");
                    icPassportField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (icPassportField.getText().isEmpty()) {
                    icPassportField.setForeground(Color.GRAY);
                    icPassportField.setText("IC/Passport Number");
                }
            }
        });
    
        // Username section
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        JLabel createUsernameLabel = createFieldLabel("Create Username:");
        centerPanel.add(createUsernameLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameField.setForeground(Color.GRAY);
        usernameField.setText("Create Username");
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        centerPanel.add(usernameField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JLabel usernameIconLabel = new JLabel();
        usernameIconLabel.setPreferredSize(new Dimension(30, 30));
        centerPanel.add(usernameIconLabel, gbc);
    
        // Username field placeholder behavior
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (usernameField.getText().equals("Create Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setForeground(Color.GRAY);
                    usernameField.setText("Create Username");
                }
            }
        });
    
        // Password section
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        JLabel createPasswordLabel = createFieldLabel("Create Password:");
        centerPanel.add(createPasswordLabel, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24));
        passwordField.setForeground(Color.GRAY);
        passwordField.setText("Create Password");
        passwordField.setEchoChar((char) 0);
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        centerPanel.add(passwordField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JLabel passwordIconLabel = new JLabel();
        passwordIconLabel.setPreferredSize(new Dimension(30, 30));
        centerPanel.add(passwordIconLabel, gbc);

        // Password field placeholder and toggle behavior
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals("Create Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                }
            }
            public void focusLost(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("Create Password");
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    
        // Show/Hide icon
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel showHideIcon = new JLabel(new ImageIcon(new ImageIcon("images/show_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        showHideIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideIcon.setPreferredSize(new Dimension(20, 20));
        passwordIconLabel.setPreferredSize(new Dimension(30, 30));
        iconPanel.add(showHideIcon);
        iconPanel.add(passwordIconLabel);
        centerPanel.add(iconPanel, gbc);
    
        showHideIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (passwordField.getEchoChar() == '*') {
                    passwordField.setEchoChar((char) 0);
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/hide_icon.png")
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } else {
                    passwordField.setEchoChar('*');
                    showHideIcon.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png")
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                }
            }
        });
    
        // Contact Number section
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        JLabel contactNumberLabel = createFieldLabel("Contact Number:");
        centerPanel.add(contactNumberLabel, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JTextField contactNumberField = new JTextField();
        contactNumberField.setFont(new Font("Arial", Font.PLAIN, 24));
        contactNumberField.setForeground(Color.GRAY);
        contactNumberField.setText("Contact Number");
        contactNumberField.setPreferredSize(new Dimension(300, 40));
        contactNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        centerPanel.add(contactNumberField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JLabel contactNumberIconLabel = new JLabel();
        contactNumberIconLabel.setPreferredSize(new Dimension(30, 30));
        centerPanel.add(contactNumberIconLabel, gbc);

        // Contact Number field placeholder behavior
        contactNumberField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (contactNumberField.getText().equals("Contact Number")) {
                    contactNumberField.setText("");
                    contactNumberField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (contactNumberField.getText().isEmpty()) {
                    contactNumberField.setForeground(Color.GRAY);
                    contactNumberField.setText("Contact Number");
                }
            }
        });
    
        // Register button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 24));
        registerButton.setPreferredSize(new Dimension(200, 50));
        centerPanel.add(registerButton, gbc);
    
        // Enter key handlers
        icPassportField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    usernameField.requestFocus();
                }
            }
        });
    
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    contactNumberField.requestFocus();
                }
            }
        });
    
        contactNumberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerButton.doClick();
                }
            }
        });
    
        // Register button action
        registerButton.addActionListener(e -> {
            String icPassportNumber = icPassportField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String contactNumber = contactNumberField.getText();
    
            try {
                if (registerMethod.equals("registerManager")) {
                    APUHostelManagement.registerManager(icPassportNumber, username, password, contactNumber);
                } else if (registerMethod.equals("registerResident")) {
                    APUHostelManagement.registerResident(icPassportNumber, username, password, contactNumber);
                } else if (registerMethod.equals("registerStaff")) {
                    APUHostelManagement.registerStaff(icPassportNumber, username, password, contactNumber);
                }
    
                JOptionPane.showMessageDialog(this, "Registration successful.");
                cardLayout.show(mainPanel, "RoleSelection");
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
            }
        });
    
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
                icPassportField.requestFocusInWindow();
            }
        });

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(registerButton);
    
        backButton.setMnemonic(KeyEvent.VK_B);
        registerButton.setMnemonic(KeyEvent.VK_R);
        
        addFocusHighlight(icPassportField);
        addFocusHighlight(usernameField);
        addFocusHighlight(passwordField);
        addFocusHighlight(contactNumberField);

        backButton.setToolTipText("Go back (Alt+B)");
        registerButton.setToolTipText("Register (Alt+R)");
        icPassportField.setToolTipText("IC/Passport number in the format XXXXXX-XX-XXXX");
        usernameField.setToolTipText("3-12 characters, letters, numbers and underscore only");
        passwordField.setToolTipText("8-12 chars with uppercase, number & special char");
        contactNumberField.setToolTipText("Contact number in the format XXX-XXXXXXX");
    
        // Add validation listeners
        addValidationListeners(icPassportField, usernameField, passwordField, contactNumberField,
            icPassportIconLabel, usernameIconLabel, passwordIconLabel, contactNumberIconLabel);
    
        return panel;
    }
    
    private void validateICPassport(JTextField field, JLabel iconLabel) {
        String value = field.getText().trim();
        if (!value.equals("IC/Passport Number")) {
            try {
                APUHostelManagement.validateICPassport(value);
                setValidField(field, iconLabel);
            } catch (Exception e) {
                setInvalidField(field, iconLabel, e.getMessage());
            }
        }
    }
    
    private void validateUsername(JTextField field, JLabel iconLabel) {
        String value = field.getText().trim();
        if (!value.equals("Create Username")) {
            try {
                APUHostelManagement.validateUsername(value);
                setValidField(field, iconLabel);
            } catch (Exception e) {
                setInvalidField(field, iconLabel, e.getMessage());
            }
        }
    }
    
    private void validatePassword(JPasswordField field, JTextField usernameField, JLabel iconLabel) {
        String password = new String(field.getPassword()).trim();
        String username = usernameField.getText().trim();
        if (!password.equals("Create Password")) {
            try {
                APUHostelManagement.validatePassword(password, username);
                setValidField(field, iconLabel);
            } catch (Exception e) {
                setInvalidField(field, iconLabel, e.getMessage());
            }
        }
    }
    
    private void validateContactNumber(JTextField field, JLabel iconLabel) {
        String value = field.getText().trim();
        if (!value.equals("Contact Number")) {
            try {
                APUHostelManagement.validateContactNumber(value);
                setValidField(field, iconLabel);
            } catch (Exception e) {
                setInvalidField(field, iconLabel, e.getMessage());
            }
        }
    }
    
    private void setValidField(JComponent field, JLabel iconLabel) {
        field.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
    }
    
    private void setInvalidField(JComponent field, JLabel iconLabel, String message) {
        field.setBorder(BorderFactory.createLineBorder(Color.RED));
        iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
    }
    
    private void addValidationListeners(JTextField icPassportField, JTextField usernameField, 
        JPasswordField passwordField, JTextField contactNumberField,
        JLabel icPassportIconLabel, JLabel usernameIconLabel, 
        JLabel passwordIconLabel, JLabel contactNumberIconLabel) {
    
        icPassportField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                validateICPassport(icPassportField, icPassportIconLabel);
            }
        });
        icPassportField.getDocument().addDocumentListener(
            new ValidationListener(() -> validateICPassport(icPassportField, icPassportIconLabel)));
    
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                validateUsername(usernameField, usernameIconLabel);
            }
        });
        usernameField.getDocument().addDocumentListener(
            new ValidationListener(() -> validateUsername(usernameField, usernameIconLabel)));
    
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                validatePassword(passwordField, usernameField, passwordIconLabel);
            }
        });
        passwordField.getDocument().addDocumentListener(
            new ValidationListener(() -> validatePassword(passwordField, usernameField, passwordIconLabel)));
    
        contactNumberField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                validateContactNumber(contactNumberField, contactNumberIconLabel);
            }
        });
        contactNumberField.getDocument().addDocumentListener(
            new ValidationListener(() -> validateContactNumber(contactNumberField, contactNumberIconLabel)));
    }
    
    private void showErrorDialog(String message) {
        String userFriendlyMessage = String.format(
            "Error: %s\n\nPlease check:\n" +
            "• Input formats are correct\n" +
            "• Required fields are filled\n",
            message
        );
        JOptionPane.showMessageDialog(
            this,
            userFriendlyMessage,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
    
    private void addFocusHighlight(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            public void focusLost(FocusEvent e) {
                String text = "";
                if (field instanceof JTextField) {
                    text = ((JTextField) field).getText();
                } else if (field instanceof JPasswordField) {
                    text = new String(((JPasswordField) field).getPassword());
                }
                
                if (!text.isEmpty()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }
            }
        });
    }
    
    private void showFormatRequirements() {
        String requirements = 
            "<html><body style='width: 300px; padding: 10px;'>" +
            "<h2>Format Requirements</h2>" +
            "<ul>" +
            "<li>IC: XXXXXX-XX-XXXX or Passport: AXXXXXXXX</li>" +
            "<li>Username: 3-12 chars, letters, numbers, underscore (_)</li>" +
            "<li>Password: 8-12 chars, 1 uppercase, 1 number, 1 special (!@#$%^&*())</li>" +
            "<li>Contact: 01X-XXX-XXXX (Example: 012-345-6789)</li>" +
            "</ul></body></html>";
    
        JOptionPane.showMessageDialog(this, requirements, 
            "Format Requirements", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePageGUI frame = new WelcomePageGUI();
            frame.setVisible(true);
        });
    }
}