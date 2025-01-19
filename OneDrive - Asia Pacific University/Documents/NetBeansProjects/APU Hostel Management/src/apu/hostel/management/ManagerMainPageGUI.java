package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerMainPageGUI {
    private JFrame frame;

    public ManagerMainPageGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new GridLayout(7, 1));

        JLabel managerLabel = new JLabel("Manager Menu", JLabel.CENTER);
        frame.add(managerLabel);

        JButton approveUserButton = new JButton("Approve User Registration");
        approveUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerApproveUserRegistrationGUI();
                frame.dispose(); // Close the current window
            }
        });
        frame.add(approveUserButton);

        JButton searchUserButton = new JButton("Search, Update, Delete or Restore User");
        searchUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUsers();
            }
        });
        frame.add(searchUserButton);

        JButton fixRateButton = new JButton("Fix, Update, Delete or Restore Rate");
        fixRateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fixOrUpdateRate();
            }
        });
        frame.add(fixRateButton);

        JButton manageRoomsButton = new JButton("Manage Rooms");
        manageRoomsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageRooms();
            }
        });
        frame.add(manageRoomsButton);

        JButton updatePersonalInfoButton = new JButton("Update Personal Information");
        updatePersonalInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePersonalInformation();
            }
        });
        frame.add(updatePersonalInfoButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        frame.add(logoutButton);

        frame.setVisible(true);
    }

    private void searchUsers() {
        // Implement the logic for searching, updating, deleting, or restoring users
        JOptionPane.showMessageDialog(frame, "Search, Update, Delete or Restore User functionality to be implemented.");
    }

    private void fixOrUpdateRate() {
        // Implement the logic for fixing, updating, deleting, or restoring rates
        JOptionPane.showMessageDialog(frame, "Fix, Update, Delete or Restore Rate functionality to be implemented.");
    }

    private void manageRooms() {
        // Implement the logic for managing rooms
        JOptionPane.showMessageDialog(frame, "Manage Rooms functionality to be implemented.");
    }

    private void updatePersonalInformation() {
        // Implement the logic for updating personal information
        JOptionPane.showMessageDialog(frame, "Update Personal Information functionality to be implemented.");
    }

    private void logout() {
        // Implement the logic for logging out
        JOptionPane.showMessageDialog(frame, "Logging out...");
        frame.dispose();
        new WelcomePageGUI(); // Redirect to Welcome Page
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ManagerMainPageGUI window = new ManagerMainPageGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}