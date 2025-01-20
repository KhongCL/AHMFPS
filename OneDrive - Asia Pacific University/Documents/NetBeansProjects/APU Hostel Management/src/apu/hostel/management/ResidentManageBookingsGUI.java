package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentManageBookingsGUI {
    private JFrame frame;

    public ResidentManageBookingsGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manage Bookings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JLabel titleLabel = new JLabel("Manage Bookings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add Make Booking button
        JButton makeBookingButton = new JButton("Make Booking");
        makeBookingButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        makeBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakeBookingGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(makeBookingButton, gbc);

        // Add Make Payment for Booking button
        gbc.gridy++;
        JButton makePaymentButton = new JButton("Make Payment for Booking");
        makePaymentButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakePaymentForBookingGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(makePaymentButton, gbc);

        // Add Cancel Booking button
        gbc.gridy++;
        JButton cancelBookingButton = new JButton("Cancel Booking");
        cancelBookingButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        cancelBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentCancelBookingGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(cancelBookingButton, gbc);

        // Add Back button
        gbc.gridy++;
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentManageBookingsGUI();
        });
    }
}