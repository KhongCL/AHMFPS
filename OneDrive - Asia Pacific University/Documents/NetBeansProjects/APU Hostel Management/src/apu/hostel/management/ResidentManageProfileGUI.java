// KHONG CHEE LEONG TP075846
// JUSTIN NG KEN HONG TP073469

package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Update Personal Information - " + resident.getUsername());
        frame.setLocationRelativeTo(null);
    
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10)); 
    
        JLabel titleLabel = new JLabel("Update Personal Information", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);
    
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 0, 10);
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
        gbc.insets = new Insets(0, 10, 5, 10);
        icPassportErrorLabel = createErrorLabel();
        inputPanel.add(icPassportErrorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
    
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
        gbc.insets = new Insets(0, 10, 5, 10);
        usernameErrorLabel = createErrorLabel();
        inputPanel.add(usernameErrorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
    
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));  
        passwordField = new JPasswordField(resident.getPassword());
        passwordField.setPreferredSize(new Dimension(300, 30));
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton showHideButton = new JButton(new ImageIcon(new ImageIcon("images/show_icon.png")
            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        showHideButton.setPreferredSize(new Dimension(35, 35));
        showHideButton.setBorderPainted(false);
        showHideButton.setContentAreaFilled(false);
        showHideButton.setFocusPainted(false);
        showHideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideButton.setToolTipText("Show/Hide password");
        showHideButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '*') {
                passwordField.setEchoChar((char) 0);
                showHideButton.setIcon(new ImageIcon(new ImageIcon("images/hide_icon.png")
                    .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } else {
                passwordField.setEchoChar('*');
                showHideButton.setIcon(new ImageIcon(new ImageIcon("images/show_icon.png")
                    .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))); 
            }
        });
        passwordPanel.add(showHideButton, BorderLayout.EAST);

        inputPanel.add(passwordPanel, gbc);

        gbc.gridx = 2;
        JLabel passwordIconLabel = new JLabel();
        passwordIconLabel.setPreferredSize(new Dimension(30, 30));
        inputPanel.add(passwordIconLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 5, 10);
        passwordErrorLabel = createErrorLabel();
        inputPanel.add(passwordErrorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
    
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
        gbc.insets = new Insets(0, 10, 5, 10);
        contactNumberErrorLabel = createErrorLabel();
        inputPanel.add(contactNumberErrorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
    
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);
    

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JButton updateButton = createButton("Update Profile", "update_profile_icon.png");
        updateButton.setPreferredSize(new Dimension(175, 40));
        updateButton.addActionListener(e -> updateProfile());
        buttonPanel.add(updateButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    
        
        addValidationListeners(icPassportIconLabel, usernameIconLabel, passwordIconLabel, contactNumberIconLabel);
    
        frame.setVisible(true);

        backButton.setMnemonic(KeyEvent.VK_B);    
        updateButton.setMnemonic(KeyEvent.VK_U);   

        backButton.setToolTipText("Go back to main page (Alt+B)");
        updateButton.setToolTipText("Update profile information (Alt+U)");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(updateButton);

        addFocusHighlight(icPassportField);
        addFocusHighlight(usernameField);
        addFocusHighlight(passwordField);
        addFocusHighlight(contactNumberField);

        ActionListener enterAction = e -> updateButton.doClick();
        icPassportField.addActionListener(enterAction);
        usernameField.addActionListener(enterAction);
        passwordField.addActionListener(enterAction);
        contactNumberField.addActionListener(enterAction);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().registerKeyboardAction(e -> {
            backButton.doClick();
        }, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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
                
            }
        });
    }

    private JTextArea createErrorLabel() {
        JTextArea errorLabel = new JTextArea();
        errorLabel.setPreferredSize(new Dimension(300, 30));
        errorLabel.setMaximumSize(new Dimension(300, 30));
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
            
            if (newIcPassportNumber.equals(resident.getIcPassportNumber()) &&
                newUsername.equals(resident.getUsername()) &&
                newPassword.equals(resident.getPassword()) &&
                newContactNumber.equals(resident.getContactNumber())) {
                JOptionPane.showMessageDialog(frame, "No changes were made to the profile.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
    
            
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

    private void addButtonHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color darkerColor = getDarkerColor(originalColor);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkerColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 2),
                    BorderFactory.createEmptyBorder(3, 13, 3, 13)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
            }
        });
    }
    
    private Color getDarkerColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 1.1f), Math.max(0, hsb[2] - 0.15f));
    }
    
    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setIconTextGap(15);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Update")) {
                button.setBackground(new Color(225, 190, 231));    // Light Purple
                button.setForeground(new Color(106, 27, 154));    // Dark Purple
            }
            
            // Add default border matching text color
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
            ));
            
            // Add shadow effect
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 14, 4, 14),
                    BorderFactory.createEmptyBorder(0, 0, 2, 0)
                )
            ));
            
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        return button;
    }

    private void addFocusHighlight(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(66, 133, 244)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
    }
}