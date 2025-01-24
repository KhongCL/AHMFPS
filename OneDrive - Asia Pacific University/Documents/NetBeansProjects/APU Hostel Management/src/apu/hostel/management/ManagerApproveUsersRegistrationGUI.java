package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ManagerApproveUsersRegistrationGUI {
    private JFrame frame;
    private JPanel rolePanel;
    private List<APUHostelManagement.User> selectedRoleList;
    private APUHostelManagement.Manager manager; // Add manager field

    // Add new constructor
    public ManagerApproveUsersRegistrationGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    public ManagerApproveUsersRegistrationGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Approve User Registration");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout());

        // Back button to return to ManagerMainPageGUI
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Back button in Choose Role to Approve page clicked");
                new ManagerMainPageGUI(manager); // Pass manager back
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        // Role selection panel
        rolePanel = new JPanel(new BorderLayout());
        rolePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JLabel roleLabel = new JLabel("Choose the role to approve:", JLabel.CENTER);
        rolePanel.add(roleLabel, BorderLayout.NORTH);

        JPanel roleButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton managerButton = new JButton("Manager");
        managerButton.setPreferredSize(new Dimension(150, 40));
        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUnapprovedUsers("Manager");
            }
        });
        roleButtonPanel.add(managerButton, gbc);

        gbc.gridy++;
        JButton staffButton = new JButton("Staff");
        staffButton.setPreferredSize(new Dimension(150, 40));
        staffButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUnapprovedUsers("Staff");
            }
        });
        roleButtonPanel.add(staffButton, gbc);

        gbc.gridy++;
        JButton residentButton = new JButton("Resident");
        residentButton.setPreferredSize(new Dimension(150, 40));
        residentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUnapprovedUsers("Resident");
            }
        });
        roleButtonPanel.add(residentButton, gbc);

        rolePanel.add(roleButtonPanel, BorderLayout.CENTER);
        frame.add(rolePanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void loadUnapprovedUsers(String role) {
        try {
            if (role.equals("Manager")) {
                selectedRoleList = APUHostelManagement.User.readFromFile("unapproved_managers.txt");
            } else if (role.equals("Staff")) {
                selectedRoleList = APUHostelManagement.User.readFromFile("unapproved_staffs.txt");
            } else if (role.equals("Resident")) {
                selectedRoleList = APUHostelManagement.User.readFromFile("unapproved_residents.txt");
            }

            if (selectedRoleList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No unapproved " + role.toLowerCase() + "s found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            new ManagerApproveUsersTableGUI(role, selectedRoleList, manager);
            frame.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading users.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}