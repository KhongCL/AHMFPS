package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagerManageProfileGUI {
    private JFrame frame;
    private JTextField icPassportField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField contactNumberField;
    private JLabel icPassportErrorLabel;
    private JLabel usernameErrorLabel;
    private JLabel passwordErrorLabel;
    private JLabel contactNumberErrorLabel;
    private APUHostelManagement.Manager manager;

    public ManagerManageProfileGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Update Personal Information");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager);
                frame.dispose();
            }
        });

        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 3, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        inputPanel.add(new JLabel("IC/Passport Number:"));
        icPassportField = new JTextField(manager.getIcPassportNumber());
        inputPanel.add(icPassportField);
        icPassportErrorLabel = new JLabel();
        inputPanel.add(icPassportErrorLabel);

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(manager.getUsername());
        inputPanel.add(usernameField);
        usernameErrorLabel = new JLabel();
        inputPanel.add(usernameErrorLabel);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(manager.getPassword());
        inputPanel.add(passwordField);
        passwordErrorLabel = new JLabel();
        inputPanel.add(passwordErrorLabel);

        inputPanel.add(new JLabel("Contact Number:"));
        contactNumberField = new JTextField(manager.getContactNumber());
        inputPanel.add(contactNumberField);
        contactNumberErrorLabel = new JLabel();
        inputPanel.add(contactNumberErrorLabel);

        frame.add(inputPanel, BorderLayout.CENTER);

        // Update button
        JButton updateButton = new JButton("Update Profile");
        updateButton.setPreferredSize(new Dimension(150, 40));
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add input validation listeners
        addValidationListeners();

        frame.setVisible(true);
    }

    private void addValidationListeners() {
        icPassportField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateICPassport();
            }
        });
        icPassportField.getDocument().addDocumentListener(new ValidationListener(this::validateICPassport));

        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateUsername();
            }
        });
        usernameField.getDocument().addDocumentListener(new ValidationListener(this::validateUsername));

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validatePassword();
            }
        });
        passwordField.getDocument().addDocumentListener(new ValidationListener(this::validatePassword));

        contactNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateContactNumber();
            }
        });
        contactNumberField.getDocument().addDocumentListener(new ValidationListener(this::validateContactNumber));
    }

    private void validateICPassport() {
        String icPassport = icPassportField.getText().trim();
        if (!APUHostelManagement.isValidICPassport(icPassport)) {
            icPassportErrorLabel.setText(getICPassportErrorMessage(icPassport));
            icPassportErrorLabel.setForeground(Color.RED);
        } else {
            icPassportErrorLabel.setText("\u2713"); // Check mark
            icPassportErrorLabel.setForeground(Color.GREEN);
        }
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (!APUHostelManagement.isValidUsername(username)) {
            usernameErrorLabel.setText(getUsernameErrorMessage(username));
            usernameErrorLabel.setForeground(Color.RED);
        } else {
            usernameErrorLabel.setText("\u2713"); // Check mark
            usernameErrorLabel.setForeground(Color.GREEN);
        }
    }

    private void validatePassword() {
        String password = new String(passwordField.getPassword()).trim();
        String username = usernameField.getText().trim();
        if (!APUHostelManagement.isValidPassword(password, username)) {
            passwordErrorLabel.setText(getPasswordErrorMessage(password, username));
            passwordErrorLabel.setForeground(Color.RED);
        } else {
            passwordErrorLabel.setText("\u2713"); // Check mark
            passwordErrorLabel.setForeground(Color.GREEN);
        }
    }

    private void validateContactNumber() {
        String contactNumber = contactNumberField.getText().trim();
        if (!APUHostelManagement.isValidContactNumber(contactNumber)) {
            contactNumberErrorLabel.setText(getContactNumberErrorMessage(contactNumber));
            contactNumberErrorLabel.setForeground(Color.RED);
        } else {
            contactNumberErrorLabel.setText("\u2713"); // Check mark
            contactNumberErrorLabel.setForeground(Color.GREEN);
        }
    }

    private void updateProfile() {
        String newIcPassportNumber = icPassportField.getText().trim();
        String newUsername = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();
        String newContactNumber = contactNumberField.getText().trim();

        if (!newIcPassportNumber.equals(manager.getIcPassportNumber())) {
            if (!APUHostelManagement.isValidICPassport(newIcPassportNumber)) {
                JOptionPane.showMessageDialog(frame, "Invalid IC/Passport Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.setIcPassportNumber(newIcPassportNumber);
        }

        if (!newUsername.equals(manager.getUsername())) {
            if (!APUHostelManagement.isValidUsername(newUsername)) {
                JOptionPane.showMessageDialog(frame, "Invalid Username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.setUsername(newUsername);
        }

        if (!newPassword.equals(manager.getPassword())) {
            if (!APUHostelManagement.isValidPassword(newPassword, newUsername)) {
                JOptionPane.showMessageDialog(frame, "Invalid Password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.setPassword(newPassword);
        }

        if (!newContactNumber.equals(manager.getContactNumber())) {
            if (!APUHostelManagement.isValidContactNumber(newContactNumber)) {
                JOptionPane.showMessageDialog(frame, "Invalid Contact Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.setContactNumber(newContactNumber);
        }

        try {
            APUHostelManagement.Manager.updateFile("approved_managers.txt", manager);
            APUHostelManagement.Manager.updateFile("users.txt", manager);
            JOptionPane.showMessageDialog(frame, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while updating the profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getICPassportErrorMessage(String icPassport) {
        if (icPassport.length() != 14 && icPassport.length() != 9) {
            return "Invalid IC/Passport format. IC format: xxxxxx-xx-xxxx, Passport format: one alphabet followed by 8 numbers.";
        }
        try {
            if (!APUHostelManagement.isUnique(icPassport, "", "")) {
                return "IC/Passport number already exists. Please use a different IC/Passport number.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the IC/Passport number.";
        }
        return "Invalid IC/Passport number.";
    }

    private String getUsernameErrorMessage(String username) {
        if (username.length() < 3 || username.length() > 12) {
            return "Username must be between 3 and 12 characters long.";
        }
        for (char c : username.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return "Username can only contain letters, numbers, and underscores.";
            }
        }
        if (!username.matches(".*[a-zA-Z]+.*")) {
            return "Username must contain at least one letter.";
        }
        try {
            if (!APUHostelManagement.isUnique("", username, "")) {
                return "Username already exists. Please choose a different username.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the username.";
        }
        return "Invalid username.";
    }

    private String getPasswordErrorMessage(String password, String username) {
        if (password.length() < 8 || password.length() > 12) {
            return "Password must be between 8 and 12 characters long.";
        }
        if (password.contains(username)) {
            return "Password cannot be similar to the username.";
        }
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");
        boolean hasInvalidChar = password.matches(".*[^a-zA-Z0-9!@#$%^&*()].*");
        if (!(hasNumber && hasSpecialChar && hasUppercase)) {
            return "Password must contain at least one number, one special character (!@#$%^&*()), and one uppercase letter.";
        }
        if (hasInvalidChar) {
            return "Password contains invalid characters. Only !@#$%^&*() are allowed as special characters.";
        }
        return "Invalid password.";
    }

    private String getContactNumberErrorMessage(String contactNumber) {
        if (contactNumber.length() != 12 || !contactNumber.startsWith("01") || contactNumber.charAt(3) != '-' || contactNumber.charAt(7) != '-' || !contactNumber.replace("-", "").matches("\\d+")) {
            return "Invalid contact number format. The correct format is 01X-XXX-XXXX.";
        }
        try {
            if (!APUHostelManagement.isUnique("", "", contactNumber)) {
                return "Contact number already exists. Please choose a different contact number.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the contact number.";
        }
        return "Invalid contact number.";
    }

    public static void main(String[] args) {
        // Load manager information from files
        APUHostelManagement.Manager manager = null;
        try {
            List<APUHostelManagement.Manager> managers = APUHostelManagement.Manager.readManagersFromFile("approved_managers.txt");
            for (APUHostelManagement.Manager m : managers) {
                if (m.getManagerID().equals("M01")) { // Example manager ID
                    manager = m;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (manager != null) {
            APUHostelManagement.Manager finalManager = manager;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        ManagerManageProfileGUI window = new ManagerManageProfileGUI(finalManager);
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("Manager not found.");
        }
    }
}

class ValidationListener implements javax.swing.event.DocumentListener {
    private Runnable validationFunction;

    public ValidationListener(Runnable validationFunction) {
        this.validationFunction = validationFunction;
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run();
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run();
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run();
    }
}