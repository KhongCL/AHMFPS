package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
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
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        
        JButton updateInfoButton = createButton("Update Personal Information", "update_icon.png");
        updateInfoButton.setPreferredSize(new Dimension(300, 50)); 
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

        
        gbc.gridy++;
        JButton viewPaymentRecordsButton = createButton("View Payment Records", "payment_icon.png");
        viewPaymentRecordsButton.setPreferredSize(new Dimension(300, 50)); 
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

        
        gbc.gridy++;
        JButton manageBookingsButton = createButton("Manage Bookings", "booking_icon.png");
        manageBookingsButton.setPreferredSize(new Dimension(300, 50)); 
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

        
        updateInfoButton.setMnemonic(KeyEvent.VK_U);     
        viewPaymentRecordsButton.setMnemonic(KeyEvent.VK_V);   
        manageBookingsButton.setMnemonic(KeyEvent.VK_M);      
        logoutButton.setMnemonic(KeyEvent.VK_L);       

        
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
            
            if (text.contains("Update") || text.contains("View") || text.contains("Logout")) {
                button.setBackground(new Color(230, 240, 250)); 
            } else if (text.contains("Manage") || text.contains("Make")) {
                button.setBackground(new Color(230, 250, 230)); 
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }
}