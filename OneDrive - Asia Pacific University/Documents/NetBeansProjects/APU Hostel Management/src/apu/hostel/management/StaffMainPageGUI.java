package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class StaffMainPageGUI {
    private JFrame frame;
    private APUHostelManagement.Staff staff;

    public StaffMainPageGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    public StaffMainPageGUI() {
        initialize();
    }

    private void initialize() {
        String staffName = (staff != null) ? staff.getUsername() : "Unknown Staff";
        frame = new JFrame("Staff Menu - " + staffName);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);


        JLabel titleLabel = new JLabel("Staff Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + staffName, JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        
        JButton updateProfileButton = createButton("Update Personal Information", "profile_icon.png");
        updateProfileButton.setPreferredSize(new Dimension(300, 50)); 
        updateProfileButton.addActionListener(e -> {
            if (staff != null) {
                new StaffManageProfileGUI(staff);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(updateProfileButton, gbc);

        
        gbc.gridy++;
        JButton makePaymentButton = createButton("Make Payment for Resident", "payment_icon.png");
        makePaymentButton.setPreferredSize(new Dimension(300, 50)); 
        makePaymentButton.addActionListener(e -> {
            if (staff != null) {
                new StaffMakePaymentForResidentGUI(staff);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(makePaymentButton, gbc);

        
        gbc.gridy++;
        JButton generateReceiptButton = createButton("Generate Receipt", "generate_receipt_icon.png");
        generateReceiptButton.setPreferredSize(new Dimension(300, 50)); 
        generateReceiptButton.addActionListener(e -> {
            if (staff != null) {
                new StaffGenerateReceiptGUI(staff);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(generateReceiptButton, gbc);

        
        gbc.gridy++;
        JButton logoutButton = createButton("Logout", "logout_icon.png");
        logoutButton.setPreferredSize(new Dimension(300, 50)); 
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                logout();
            }
        });
        buttonPanel.add(logoutButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        updateProfileButton.setMnemonic(KeyEvent.VK_U); 
        makePaymentButton.setMnemonic(KeyEvent.VK_M); 
        generateReceiptButton.setMnemonic(KeyEvent.VK_G); 
        logoutButton.setMnemonic(KeyEvent.VK_L); 

        updateProfileButton.setToolTipText("Update your personal information (Alt+U)");
        makePaymentButton.setToolTipText("Make payment for a resident (Alt+M)");
        generateReceiptButton.setToolTipText("Generate a receipt (Alt+G)");
        logoutButton.setToolTipText("Logout from the system (Alt+L)");

        addButtonHoverEffect(updateProfileButton);
        addButtonHoverEffect(makePaymentButton);
        addButtonHoverEffect(generateReceiptButton);
        addButtonHoverEffect(logoutButton);

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

        frame.setLocationRelativeTo(null); 

        
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().registerKeyboardAction(e -> {
            int choice = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                logout();
            }
        }, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void logout() {
        if (staff != null) {
            staff.logout();
            JOptionPane.showMessageDialog(frame, 
                "Logout successful. Thank you for using the system.",
                "Logging Out",
                JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new WelcomePageGUI().setVisible(true);
        }
    }

    private void addButtonHoverEffect(JButton button) {
        
        Color originalColor = button.getBackground();
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor); 
            }
        });
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            
            if (text.contains("Update") || text.contains("Logout")) {
                button.setBackground(new Color(230, 240, 250)); 
            } else if (text.contains("Make") || text.contains("Generate")) {
                button.setBackground(new Color(230, 250, 230)); 
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }
}