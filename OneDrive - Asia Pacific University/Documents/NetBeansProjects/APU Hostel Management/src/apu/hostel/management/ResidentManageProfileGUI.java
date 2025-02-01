package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components
    
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
    
        // Back button
        JButton backButton = createButton("Back", "back_icon.png");
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
        JButton updateButton = createButton("Update Profile", "update_profile_icon.png");
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

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int choice = JOptionPane.showConfirmDialog(frame, 
                    "Are you sure you want to close this window?", "Confirm Close",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                // No need for else as the window will stay open by default
            }
        });
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
        if (!icPassport.equals(resident.getIcPassportNumber())) {
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
        if (!username.equals(resident.getUsername())) {
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
        if (!password.equals(resident.getPassword())) {
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
        if (!contactNumber.equals(resident.getContactNumber())) {
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
        boolean hasErrors = false;
        boolean hasChanges = false;
    
        try {
            // Check if any changes were made
            if (newIcPassportNumber.equals(resident.getIcPassportNumber()) &&
                newUsername.equals(resident.getUsername()) &&
                newPassword.equals(resident.getPassword()) &&
                newContactNumber.equals(resident.getContactNumber())) {
                JOptionPane.showMessageDialog(frame, "No changes were made to the profile.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
    
            // IC/Passport Number
            if (!newIcPassportNumber.equals(resident.getIcPassportNumber())) {
                String icError = APUHostelManagement.validateUpdateICPassport(newIcPassportNumber, resident.getIcPassportNumber());
                if (icError != null) {
                    int retry = JOptionPane.showConfirmDialog(frame, 
                        icError + "\nDo you want to try again?", 
                        "Error", JOptionPane.YES_NO_OPTION);
                    if (retry == JOptionPane.YES_OPTION) {
                        hasErrors = true;
                    } else {
                        icPassportField.setText(resident.getIcPassportNumber());
                    }
                } else if (!hasErrors) {
                    resident.setIcPassportNumber(newIcPassportNumber);
                    hasChanges = true;
                }
            }
    
            // Username  
            if (!newUsername.equals(resident.getUsername())) {
                String usernameError = APUHostelManagement.validateUpdateUsername(newUsername, resident.getUsername());
                if (usernameError != null) {
                    int retry = JOptionPane.showConfirmDialog(frame,
                        usernameError + "\nDo you want to try again?",
                        "Error", JOptionPane.YES_NO_OPTION);
                    if (retry == JOptionPane.YES_OPTION) {
                        hasErrors = true;
                    } else {
                        usernameField.setText(resident.getUsername());
                    }
                } else if (!hasErrors) {
                    resident.setUsername(newUsername);
                    hasChanges = true;
                }
            }
    
            // Password
            if (!newPassword.equals(resident.getPassword())) {
                String passwordError = APUHostelManagement.validateUpdatePassword(newPassword, newUsername);
                if (passwordError != null) {
                    int retry = JOptionPane.showConfirmDialog(frame,
                        passwordError + "\nDo you want to try again?",
                        "Error", JOptionPane.YES_NO_OPTION);
                    if (retry == JOptionPane.YES_OPTION) {
                        hasErrors = true;
                    } else {
                        passwordField.setText(resident.getPassword());
                    }
                } else if (!hasErrors) {
                    resident.setPassword(newPassword);
                    hasChanges = true;
                }
            }
    
            // Contact Number
            if (!newContactNumber.equals(resident.getContactNumber())) {
                String contactError = APUHostelManagement.validateUpdateContactNumber(newContactNumber, resident.getContactNumber());
                if (contactError != null) {
                    int retry = JOptionPane.showConfirmDialog(frame,
                        contactError + "\nDo you want to try again?",
                        "Error", JOptionPane.YES_NO_OPTION);
                    if (retry == JOptionPane.YES_OPTION) {
                        hasErrors = true;
                    } else {
                        contactNumberField.setText(resident.getContactNumber());
                    }
                } else if (!hasErrors) {
                    resident.setContactNumber(newContactNumber);
                    hasChanges = true;
                }
            }
    
            if (!hasErrors && hasChanges) {
                updateUserFiles();
                JOptionPane.showMessageDialog(frame, "Profile updated successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new ResidentManageProfileGUI(resident);
            }
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUserFiles() throws IOException {
        String role = resident.getRole();
        if (role.equals("manager")) {
            APUHostelManagement.Manager.updateFile("approved_managers.txt", resident);
        } else if (role.equals("staff")) {
            APUHostelManagement.Manager.updateFile("approved_staffs.txt", resident);
        } else if (role.equals("resident")) {
            APUHostelManagement.Manager.updateFile("approved_residents.txt", resident);
        }
        APUHostelManagement.Manager.updateFile("users.txt", resident);
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        // Don't set a default size here, let individual calls specify the size
        return button;
    }
}