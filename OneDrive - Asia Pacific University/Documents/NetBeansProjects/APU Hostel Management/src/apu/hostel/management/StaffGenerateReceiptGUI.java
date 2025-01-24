package apu.hostel.management;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffGenerateReceiptGUI {
        private JFrame frame;
    private APUHostelManagement.Staff staff; // Add staff field

    // Add new constructor
    public StaffGenerateReceiptGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    public StaffGenerateReceiptGUI() {
        initialize();
    }

    /**
     * 
     */
    private void initialize() {
        frame = new JFrame("Generate Receipt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JLabel titleLabel = new JLabel("Generate Receipt", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(titleLabel, BorderLayout.NORTH);

        // Add Generate Receipt button
        JButton generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        generateReceiptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StaffGenerateReceiptGUI(staff); // Pass staff
                frame.dispose();
            }
        });
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
                                buttonPanel.add(generateReceiptButton, gbc);

        // Add View Receipt button
        gbc.gridy++;
        JButton viewReceiptButton = new JButton("View Receipt");
        viewReceiptButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        viewReceiptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                frame.dispose();
            }
        });
        buttonPanel.add(viewReceiptButton, gbc);

        // Add Back button
        gbc.gridy++;
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StaffMainPageGUI(staff); // Pass staff
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    
}
