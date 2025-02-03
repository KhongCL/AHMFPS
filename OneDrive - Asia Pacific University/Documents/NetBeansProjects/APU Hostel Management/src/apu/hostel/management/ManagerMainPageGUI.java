package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ManagerMainPageGUI {
    private JFrame frame;
    private APUHostelManagement.Manager manager;

    public ManagerMainPageGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    public ManagerMainPageGUI() {
        initialize();
    }

    private void initialize() {
        
        String managerName = (manager != null) ? manager.getUsername() : "Unknown Manager";
        frame = new JFrame("Manager Menu - " + managerName);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JLabel managerLabel = new JLabel("Manager Menu", JLabel.CENTER);
        managerLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        frame.add(managerLabel, BorderLayout.NORTH);

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + managerName, JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(managerLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton approveUserButton = createButton("Approve User Registration", "approve_icon.png");
        approveUserButton.setPreferredSize(new Dimension(300, 50));
        approveUserButton.addActionListener(e -> {
            if (manager != null) {
                new ManagerApproveUsersRegistrationGUI(manager);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(approveUserButton, gbc);

        gbc.gridy++;
        JButton searchUserButton = createButton("Search, Update, Delete or Restore User", "search_icon.png");
        searchUserButton.setPreferredSize(new Dimension(300, 50));
        searchUserButton.addActionListener(e -> {
            if (manager != null) {
                new ManagerManageUsersGUI(manager);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(searchUserButton, gbc);

        gbc.gridy++;
        JButton fixRateButton = createButton("Fix, Update, Delete or Restore Rate", "rate_icon.png");
        fixRateButton.setPreferredSize(new Dimension(300, 50));
        fixRateButton.addActionListener(e -> {
            if (manager != null) {
                new ManagerManageRatesGUI(manager);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(fixRateButton, gbc);

        gbc.gridy++;
        JButton manageRoomsButton = createButton("Manage Rooms", "room_icon.png");
        manageRoomsButton.setPreferredSize(new Dimension(300, 50));
        manageRoomsButton.addActionListener(e -> {
            if (manager != null) {
                new ManagerManageRoomsGUI(manager);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(manageRoomsButton, gbc);

        gbc.gridy++;
        JButton updatePersonalInfoButton = createButton("Update Personal Information", "profile_icon.png");
        updatePersonalInfoButton.setPreferredSize(new Dimension(300, 50));
        updatePersonalInfoButton.addActionListener(e -> {
            if (manager != null) {
                new ManagerManageProfileGUI(manager);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Session expired. Please login again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(updatePersonalInfoButton, gbc);

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

        approveUserButton.setMnemonic(KeyEvent.VK_A);  
        searchUserButton.setMnemonic(KeyEvent.VK_S);   
        fixRateButton.setMnemonic(KeyEvent.VK_F);      
        manageRoomsButton.setMnemonic(KeyEvent.VK_M);  
        updatePersonalInfoButton.setMnemonic(KeyEvent.VK_U); 
        logoutButton.setMnemonic(KeyEvent.VK_L);       

        approveUserButton.setToolTipText("Approve pending user registrations (Alt+A)");
        searchUserButton.setToolTipText("Search and manage user accounts (Alt+S)");
        fixRateButton.setToolTipText("Manage room rates (Alt+F)");
        manageRoomsButton.setToolTipText("Manage hostel rooms (Alt+M)");
        updatePersonalInfoButton.setToolTipText("Update your profile information (Alt+U)");
        logoutButton.setToolTipText("Logout from the system (Alt+L)");

        addButtonHoverEffect(approveUserButton);
        addButtonHoverEffect(searchUserButton);
        addButtonHoverEffect(fixRateButton);
        addButtonHoverEffect(manageRoomsButton);
        addButtonHoverEffect(updatePersonalInfoButton);
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
        if (manager != null) {
            manager.logout();
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
            
            if (text.contains("Approve") || text.contains("Search") || text.contains("Update") || text.contains("Logout")) {
                button.setBackground(new Color(230, 240, 250)); 
            } else if (text.contains("Fix") || text.contains("Rooms")) {
                button.setBackground(new Color(230, 250, 230)); 
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }
}