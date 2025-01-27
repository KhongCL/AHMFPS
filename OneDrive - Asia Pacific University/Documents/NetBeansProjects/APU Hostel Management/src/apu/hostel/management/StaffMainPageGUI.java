package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        frame = new JFrame("Staff Main Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JLabel titleLabel = new JLabel("Staff Main Page", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Update Personal Information button
        JButton updateProfileButton = new JButton("Update Personal Information");
        updateProfileButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        updateProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (staff == null) {
                    JOptionPane.showMessageDialog(frame, "Please login to update personal information.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    new StaffManageProfileGUI(staff); // Launch StaffManageProfileGUI with staff info
                    frame.dispose();
                }
            }
        });
        buttonPanel.add(updateProfileButton, gbc);

        // Make Payment for Resident button
        gbc.gridy++;
        JButton makePaymentButton = new JButton("Make Payment for Resident");
        makePaymentButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StaffMakePaymentForResidentGUI(staff); // Pass staff
                frame.dispose();
            }
        });
        buttonPanel.add(makePaymentButton, gbc);

        // Generate Receipt button
        gbc.gridy++;
        JButton generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        generateReceiptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StaffGenerateReceiptGUI(staff); // Pass staff  
                frame.dispose();
            }
        });
        buttonPanel.add(generateReceiptButton, gbc);

        // Logout button
        gbc.gridy++;
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        buttonPanel.add(logoutButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void logout() {
        if (staff != null) {
            staff.logout(); 
        }
        JOptionPane.showMessageDialog(frame, "Logging out...");
        frame.dispose();
        WelcomePageGUI welcomePage = new WelcomePageGUI();
        welcomePage.setVisible(true);
    }
}