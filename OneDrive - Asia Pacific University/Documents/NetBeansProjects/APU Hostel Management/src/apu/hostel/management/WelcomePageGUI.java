package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePageGUI {
    private JFrame frame;
    private JTextField authCodeField;
    private String selectedRole;

    public WelcomePageGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("APU Hostel Management Fees Payment System (AHMFPS)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        JLabel welcomeLabel = new JLabel("Welcome to APU Hostel Management Fees Payment System (AHMFPS)", JLabel.CENTER);
        frame.add(welcomeLabel);

        JLabel roleLabel = new JLabel("Please choose your role:", JLabel.CENTER);
        frame.add(roleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton managerButton = new JButton("Manager");
        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRoleSelection("manager");
            }
        });
        buttonPanel.add(managerButton);

        JButton staffButton = new JButton("Staff");
        staffButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRoleSelection("staff");
            }
        });
        buttonPanel.add(staffButton);

        JButton residentButton = new JButton("Resident");
        residentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRoleSelection("resident");
            }
        });
        buttonPanel.add(residentButton);

        frame.add(buttonPanel);

        authCodeField = new JTextField();
        frame.add(authCodeField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAuthCodeSubmission();
            }
        });
        frame.add(submitButton);

        frame.setVisible(true);
    }

    private void handleRoleSelection(String role) {
        selectedRole = role;
        if (role.equals("manager") || role.equals("staff")) {
            authCodeField.setVisible(true);
        } else {
            authCodeField.setVisible(false);
            showRegisterOrLoginDialog(role);
        }
    }

    private void handleAuthCodeSubmission() {
        String authCode = authCodeField.getText();
        if (APUHostelManagement.isValidAuthCode(authCode, selectedRole)) {
            showRegisterOrLoginDialog(selectedRole);
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid authorization code. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterOrLoginDialog(String role) {
        JDialog dialog = new JDialog(frame, "Choose Action", true);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.setSize(300, 200);

        JLabel label = new JLabel("You have chosen " + role + ". Please choose an action:", JLabel.CENTER);
        dialog.add(label);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showRegistrationForm(role);
            }
        });
        dialog.add(registerButton);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showLoginForm(role);
            }
        });
        dialog.add(loginButton);

        dialog.setVisible(true);
    }

    private void showRegistrationForm(String role) {
        JDialog registrationDialog = new JDialog(frame, "Register " + role, true);
        registrationDialog.setLayout(new GridLayout(5, 2));
        registrationDialog.setSize(400, 300);

        JLabel icPassportLabel = new JLabel("IC/Passport Number:");
        JTextField icPassportField = new JTextField();
        registrationDialog.add(icPassportLabel);
        registrationDialog.add(icPassportField);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        registrationDialog.add(usernameLabel);
        registrationDialog.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        registrationDialog.add(passwordLabel);
        registrationDialog.add(passwordField);

        JLabel contactNumberLabel = new JLabel("Contact Number:");
        JTextField contactNumberField = new JTextField();
        registrationDialog.add(contactNumberLabel);
        registrationDialog.add(contactNumberField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the appropriate registration method from APUHostelManagement
                if (role.equals("manager")) {
                    APUHostelManagement.registerManager(icPassportField.getText(), usernameField.getText(), new String(passwordField.getPassword()), contactNumberField.getText());
                } else if (role.equals("staff")) {
                    APUHostelManagement.registerStaff(icPassportField.getText(), usernameField.getText(), new String(passwordField.getPassword()), contactNumberField.getText());
                } else if (role.equals("resident")) {
                    APUHostelManagement.registerResident(icPassportField.getText(), usernameField.getText(), new String(passwordField.getPassword()), contactNumberField.getText());
                }
                registrationDialog.dispose();
            }
        });
        registrationDialog.add(submitButton);

        registrationDialog.setVisible(true);
    }

    private void showLoginForm(String role) {
        JDialog loginDialog = new JDialog(frame, "Login " + role, true);
        loginDialog.setLayout(new GridLayout(3, 2));
        loginDialog.setSize(400, 200);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        loginDialog.add(usernameLabel);
        loginDialog.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the appropriate login method from APUHostelManagement
                if (role.equals("manager")) {
                    APUHostelManagement.loginManager(usernameField.getText(), new String(passwordField.getPassword()));
                } else if (role.equals("staff")) {
                    APUHostelManagement.loginStaff(usernameField.getText(), new String(passwordField.getPassword()));
                } else if (role.equals("resident")) {
                    APUHostelManagement.loginResident(usernameField.getText(), new String(passwordField.getPassword()));
                }
                loginDialog.dispose();
            }
        });
        loginDialog.add(submitButton);

        loginDialog.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WelcomePageGUI window = new WelcomePageGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}