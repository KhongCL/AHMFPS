package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ResidentMainPageGUI {
    private JFrame frame;
    private APUHostelManagement.Resident resident;

    public ResidentMainPageGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    public ResidentMainPageGUI() {
        initialize();
    }

    private void initialize() {
        String residentName = (resident != null) ? resident.getUsername() : "Unknown Resident";
        frame = new JFrame("Resident Menu - " + residentName);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Resident Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + residentName, JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Update Personal Information button
        JButton updateInfoButton = createButton("Update Personal Information", "update_icon.png");
        updateInfoButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        updateInfoButton.addActionListener(e -> {
            if (resident != null) {
                new ResidentManageProfileGUI(resident);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(updateInfoButton, gbc);

        // Add View Payment Records button
        gbc.gridy++;
        JButton viewPaymentRecordsButton = createButton("View Payment Records", "payment_icon.png");
        viewPaymentRecordsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        viewPaymentRecordsButton.addActionListener(e -> {
            if (resident != null) {
                new ResidentViewPaymentRecordsGUI(resident);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(viewPaymentRecordsButton, gbc);

        // Add Manage Bookings button
        gbc.gridy++;
        JButton manageBookingsButton = createButton("Manage Bookings", "booking_icon.png");
        manageBookingsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        manageBookingsButton.addActionListener(e -> {
            if (resident != null) {
                new ResidentManageBookingsGUI(resident);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(manageBookingsButton, gbc);

        // Add Logout button
        gbc.gridy++;
        JButton logoutButton = createButton("Logout", "logout_icon.png");
        logoutButton.setPreferredSize(new Dimension(300, 50)); // Set button size
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

        // Add after creating buttons
        updateInfoButton.setMnemonic(KeyEvent.VK_U);     // Alt+U
        viewPaymentRecordsButton.setMnemonic(KeyEvent.VK_V);   // Alt+V
        manageBookingsButton.setMnemonic(KeyEvent.VK_M);      // Alt+M
        logoutButton.setMnemonic(KeyEvent.VK_L);       // Alt+L

        // Add tooltips
        updateInfoButton.setToolTipText("Update your personal information (Alt+U)");
        viewPaymentRecordsButton.setToolTipText("View your payment history (Alt+V)");
        manageBookingsButton.setToolTipText("Manage your room bookings (Alt+M)");
        logoutButton.setToolTipText("Logout from the system (Alt+L)");

        addButtonHoverEffect(updateInfoButton);
        addButtonHoverEffect(viewPaymentRecordsButton);
        addButtonHoverEffect(manageBookingsButton);
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
                // No need for else as the window will stay open by default
            }
        });

        frame.setLocationRelativeTo(null); // Center window on screen

        // Add escape key listener for logout
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
        if (resident != null) {
            resident.logout();
            JOptionPane.showMessageDialog(frame, 
                "Logout successful. Thank you for using the system.",
                "Logging Out",
                JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new WelcomePageGUI().setVisible(true);
        }
    }

    private void addButtonHoverEffect(JButton button) {
        // Store the original background color
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
            // Add category-based colors
            if (text.contains("Update") || text.contains("View") || text.contains("logout")) {
                button.setBackground(new Color(230, 240, 250)); // Light blue for user management
            } else if (text.contains("Manage") || text.contains("Make")) {
                button.setBackground(new Color(230, 250, 230)); // Light green for facility management
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }
}