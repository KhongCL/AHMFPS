package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import apu.hostel.management.ValidationListener;

public class ResidentManageProfileGUI {
    private JFrame frame;
    private JTextField icPassportField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField contactNumberField;
    private JTextArea icPassportErrorLabel;
    private JTextArea usernameErrorLabel;
    private JTextArea passwordErrorLabel;
    private JTextArea contactNumberErrorLabel;
    private APUHostelManagement.Resident resident;

    public ResidentManageProfileGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Update Personal Information");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components
    
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
    
        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
    
        topPanel.add(backButton, BorderLayout.WEST);
    
        frame.add(topPanel, BorderLayout.NORTH);
    
        // Input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
    
        inputPanel.add(new JLabel("IC/Passport Number:"), gbc);
        gbc.gridx = 1;
        icPassportField = new JTextField(resident.getIcPassportNumber());
        icPassportField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(icPassportField, gbc);
        gbc.gridx = 2;
        JLabel icPassportIconLabel = new JLabel();
        icPassportIconLabel.setPreferredSize(new Dimension(30, 30));
        inputPanel.add(icPassportIconLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        icPassportErrorLabel = createErrorLabel();
        inputPanel.add(icPassportErrorLabel, gbc);
        gbc.gridwidth = 1;
    
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(resident.getUsername());
        usernameField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(usernameField, gbc);
        gbc.gridx = 2;
        JLabel usernameIconLabel = new JLabel();
        usernameIconLabel.setPreferredSize(new Dimension(30, 30));
        inputPanel.add(usernameIconLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        usernameErrorLabel = createErrorLabel();
        inputPanel.add(usernameErrorLabel, gbc);
        gbc.gridwidth = 1;
    
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(resident.getPassword());
        passwordField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(passwordField, gbc);
        gbc.gridx = 2;
        JLabel passwordIconLabel = new JLabel();
        passwordIconLabel.setPreferredSize(new Dimension(30, 30));
        inputPanel.add(passwordIconLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        passwordErrorLabel = createErrorLabel();
        inputPanel.add(passwordErrorLabel, gbc);
        gbc.gridwidth = 1;
    
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        contactNumberField = new JTextField(resident.getContactNumber());
        contactNumberField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(contactNumberField, gbc);
        gbc.gridx = 2;
        JLabel contactNumberIconLabel = new JLabel();
        contactNumberIconLabel.setPreferredSize(new Dimension(30, 30));
        inputPanel.add(contactNumberIconLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        contactNumberErrorLabel = createErrorLabel();
        inputPanel.add(contactNumberErrorLabel, gbc);
        gbc.gridwidth = 1;
    
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
        addValidationListeners(icPassportIconLabel, usernameIconLabel, passwordIconLabel, contactNumberIconLabel);
    
        frame.setVisible(true);
    }

    private JTextArea createErrorLabel() {
        JTextArea errorLabel = new JTextArea();
        errorLabel.setPreferredSize(new Dimension(300, 60));
        errorLabel.setMaximumSize(new Dimension(300, 60));
        errorLabel.setLineWrap(true);
        errorLabel.setWrapStyleWord(true);
        errorLabel.setEditable(false);
        errorLabel.setOpaque(false);
        return errorLabel;
    }
    
    private void addValidationListeners(JLabel icPassportIconLabel, JLabel usernameIconLabel, JLabel passwordIconLabel, JLabel contactNumberIconLabel) {
        icPassportField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateICPassport(icPassportIconLabel);
            }
        });
        icPassportField.getDocument().addDocumentListener(new ValidationListener(() -> validateICPassport(icPassportIconLabel)));
    
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateUsername(usernameIconLabel);
            }
        });
        usernameField.getDocument().addDocumentListener(new ValidationListener(() -> validateUsername(usernameIconLabel)));
    
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validatePassword(passwordIconLabel);
            }
        });
        passwordField.getDocument().addDocumentListener(new ValidationListener(() -> validatePassword(passwordIconLabel)));
    
        contactNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateContactNumber(contactNumberIconLabel);
            }
        });
        contactNumberField.getDocument().addDocumentListener(new ValidationListener(() -> validateContactNumber(contactNumberIconLabel)));
    }
    
    private void validateICPassport(JLabel iconLabel) {
        String icPassport = icPassportField.getText().trim();
        if (!icPassport.equals(resident.getIcPassportNumber()) && !APUHostelManagement.isValidICPassport(icPassport)) {
            icPassportErrorLabel.setText(getICPassportErrorMessage(icPassport));
            icPassportErrorLabel.setForeground(Color.RED);
            icPassportField.setBorder(BorderFactory.createLineBorder(Color.RED));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add warning icon
        } else {
            icPassportErrorLabel.setText(""); // Remove check mark
            icPassportErrorLabel.setForeground(Color.GREEN);
            icPassportField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add check icon
        }
    }
    
    private void validateUsername(JLabel iconLabel) {
        String username = usernameField.getText().trim();
        if (!username.equals(resident.getUsername()) && !APUHostelManagement.isValidUsername(username)) {
            usernameErrorLabel.setText(getUsernameErrorMessage(username));
            usernameErrorLabel.setForeground(Color.RED);
            usernameField.setBorder(BorderFactory.createLineBorder(Color.RED));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add warning icon
        } else {
            usernameErrorLabel.setText(""); // Remove check mark
            usernameErrorLabel.setForeground(Color.GREEN);
            usernameField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add check icon
        }
    }
    
    private void validatePassword(JLabel iconLabel) {
        String password = new String(passwordField.getPassword()).trim();
        String username = usernameField.getText().trim();
        if (!password.equals(resident.getPassword()) && !APUHostelManagement.isValidPassword(password, username)) {
            passwordErrorLabel.setText(getPasswordErrorMessage(password, username));
            passwordErrorLabel.setForeground(Color.RED);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add warning icon
        } else {
            passwordErrorLabel.setText(""); // Remove check mark
            passwordErrorLabel.setForeground(Color.GREEN);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add check icon
        }
    }
    
    private void validateContactNumber(JLabel iconLabel) {
        String contactNumber = contactNumberField.getText().trim();
        if (!contactNumber.equals(resident.getContactNumber()) && !APUHostelManagement.isValidContactNumber(contactNumber)) {
            contactNumberErrorLabel.setText(getContactNumberErrorMessage(contactNumber));
            contactNumberErrorLabel.setForeground(Color.RED);
            contactNumberField.setBorder(BorderFactory.createLineBorder(Color.RED));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add warning icon
        } else {
            contactNumberErrorLabel.setText(""); // Remove check mark
            contactNumberErrorLabel.setForeground(Color.GREEN);
            contactNumberField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Add check icon
        }
    }

    private void updateProfile() {
        String newIcPassportNumber = icPassportField.getText().trim();
        String newUsername = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();
        String newContactNumber = contactNumberField.getText().trim();

        if (!newIcPassportNumber.equals(resident.getIcPassportNumber())) {
            if (!APUHostelManagement.isValidICPassport(newIcPassportNumber)) {
                JOptionPane.showMessageDialog(frame, "Invalid IC/Passport Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resident.setIcPassportNumber(newIcPassportNumber);
        }

        if (!newUsername.equals(resident.getUsername())) {
            if (!APUHostelManagement.isValidUsername(newUsername)) {
                JOptionPane.showMessageDialog(frame, "Invalid Username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resident.setUsername(newUsername);
        }

        if (!newPassword.equals(resident.getPassword())) {
            if (!APUHostelManagement.isValidPassword(newPassword, newUsername)) {
                JOptionPane.showMessageDialog(frame, "Invalid Password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resident.setPassword(newPassword);
        }

        if (!newContactNumber.equals(resident.getContactNumber())) {
            if (!APUHostelManagement.isValidContactNumber(newContactNumber)) {
                JOptionPane.showMessageDialog(frame, "Invalid Contact Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resident.setContactNumber(newContactNumber);
        }

        try {
            APUHostelManagement.Manager.updateFile("approved_residents.txt", resident);
            APUHostelManagement.Manager.updateFile("users.txt", resident);
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
}