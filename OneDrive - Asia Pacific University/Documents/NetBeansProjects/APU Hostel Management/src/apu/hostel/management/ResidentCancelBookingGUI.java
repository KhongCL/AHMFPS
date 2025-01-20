package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentCancelBookingGUI {
    private JFrame frame;

    public ResidentCancelBookingGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Cancel Booking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI();
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Cancel Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(titleLabel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentCancelBookingGUI();
        });
    }
}