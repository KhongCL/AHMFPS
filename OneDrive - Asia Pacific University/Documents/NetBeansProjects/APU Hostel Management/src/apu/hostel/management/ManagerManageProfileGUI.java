package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ManagerManageProfileGUI {
    private JFrame frame;
    private JTextField icPassportField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField contactNumberField;
    private JTextArea icPassportErrorLabel;
    private JTextArea usernameErrorLabel;
    private JTextArea passwordErrorLabel;
    private JTextArea contactNumberErrorLabel;
    private APUHostelManagement.Manager manager;

    public ManagerManageProfileGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
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
                new ManagerMainPageGUI(manager);
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
        icPassportField = new JTextField(manager.getIcPassportNumber());
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
        usernameField = new JTextField(manager.getUsername());
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
        passwordField = new JPasswordField(manager.getPassword());
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
        contactNumberField = new JTextField(manager.getContactNumber());
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
        if (!icPassport.equals(manager.getIcPassportNumber())) {
            try {
                APUHostelManagement.validateICPassport(icPassport);
                setValidField(icPassportField, icPassportErrorLabel, iconLabel);
            } catch (Exception e) {
                setInvalidField(icPassportField, icPassportErrorLabel, iconLabel, e.getMessage());
            }
        } else {
            setValidField(icPassportField, icPassportErrorLabel, iconLabel);
        }
    }
    
    private void validateUsername(JLabel iconLabel) {
        String username = usernameField.getText().trim();
        if (!username.equals(manager.getUsername())) {
            try {
                APUHostelManagement.validateUsername(username);
                setValidField(usernameField, usernameErrorLabel, iconLabel);
            } catch (Exception e) {
                setInvalidField(usernameField, usernameErrorLabel, iconLabel, e.getMessage());
            }
        } else {
            setValidField(usernameField, usernameErrorLabel, iconLabel);
        }
    }
    
    private void validatePassword(JLabel iconLabel) {
        String password = new String(passwordField.getPassword()).trim();
        String username = usernameField.getText().trim();
        if (!password.equals(manager.getPassword())) {
            try {
                APUHostelManagement.validatePassword(password, username);
                setValidField(passwordField, passwordErrorLabel, iconLabel);
            } catch (Exception e) {
                setInvalidField(passwordField, passwordErrorLabel, iconLabel, e.getMessage());
            }
        } else {
            setValidField(passwordField, passwordErrorLabel, iconLabel);
        }
    }
    
    private void validateContactNumber(JLabel iconLabel) {
        String contactNumber = contactNumberField.getText().trim();
        if (!contactNumber.equals(manager.getContactNumber())) {
            try {
                APUHostelManagement.validateContactNumber(contactNumber);
                setValidField(contactNumberField, contactNumberErrorLabel, iconLabel);
            } catch (Exception e) {
                setInvalidField(contactNumberField, contactNumberErrorLabel, iconLabel, e.getMessage());
            }
        } else {
            setValidField(contactNumberField, contactNumberErrorLabel, iconLabel);
        }
    }
    
    private void setValidField(JComponent field, JTextArea errorLabel, JLabel iconLabel) {
        errorLabel.setText("");
        errorLabel.setForeground(Color.GREEN);
        field.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        iconLabel.setIcon(new ImageIcon(new ImageIcon("images/green_check_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
    }
    
    private void setInvalidField(JComponent field, JTextArea errorLabel, JLabel iconLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(Color.RED);
        field.setBorder(BorderFactory.createLineBorder(Color.RED));
        iconLabel.setIcon(new ImageIcon(new ImageIcon("images/red_warning_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
    }

    private void updateProfile() {
        String newIcPassportNumber = icPassportField.getText().trim();
        String newUsername = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();
        String newContactNumber = contactNumberField.getText().trim();
    
        try {
            if (!newIcPassportNumber.equals(manager.getIcPassportNumber())) {
                APUHostelManagement.validateICPassport(newIcPassportNumber);
                manager.setIcPassportNumber(newIcPassportNumber);
            }
    
            if (!newUsername.equals(manager.getUsername())) {
                APUHostelManagement.validateUsername(newUsername);
                manager.setUsername(newUsername);
            }
    
            if (!newPassword.equals(manager.getPassword())) {
                APUHostelManagement.validatePassword(newPassword, newUsername);
                manager.setPassword(newPassword);
            }
    
            if (!newContactNumber.equals(manager.getContactNumber())) {
                APUHostelManagement.validateContactNumber(newContactNumber);
                manager.setContactNumber(newContactNumber);
            }
    
            updateUserFiles();
            JOptionPane.showMessageDialog(frame, "Profile updated successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUserFiles() throws IOException {
        String role = manager.getRole();
        if (role.equals("manager")) {
            APUHostelManagement.Manager.updateFile("approved_managers.txt", manager);
        } else if (role.equals("staff")) {
            APUHostelManagement.Manager.updateFile("approved_staffs.txt", manager);
        } else if (role.equals("resident")) {
            APUHostelManagement.Manager.updateFile("approved_residents.txt", manager);
        }
        APUHostelManagement.Manager.updateFile("users.txt", manager);
    }
}