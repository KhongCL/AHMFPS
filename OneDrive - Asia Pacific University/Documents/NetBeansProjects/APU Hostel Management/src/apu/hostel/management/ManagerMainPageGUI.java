package apu.hostel.management;

import javax.swing.*;
import java.awt.*;

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
        frame = new JFrame("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JLabel managerLabel = new JLabel("Manager Menu", JLabel.CENTER);
        managerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(managerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton approveUserButton = new JButton("Approve User Registration");
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
        JButton searchUserButton = new JButton("Search, Update, Delete or Restore User");
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
        JButton fixRateButton = new JButton("Fix, Update, Delete or Restore Rate");
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
        JButton manageRoomsButton = new JButton("Manage Rooms");
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
        JButton updatePersonalInfoButton = new JButton("Update Personal Information");
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
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(300, 50));
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void logout() {
        if (manager != null) {
            manager.logout();
        }
        JOptionPane.showMessageDialog(frame, "Logging out...");
        frame.dispose();
        new WelcomePageGUI().setVisible(true);
    }
}