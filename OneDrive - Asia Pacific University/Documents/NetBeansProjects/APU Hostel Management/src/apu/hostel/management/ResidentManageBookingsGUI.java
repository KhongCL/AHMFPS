package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentManageBookingsGUI {
    private JFrame frame;
    private APUHostelManagement.Resident resident; // Add resident field

    // Add new constructor
    public ResidentManageBookingsGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    public ResidentManageBookingsGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manage Bookings");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        JButton makeBookingButton = createButton("Make Booking", "booking_icon.png");
        makeBookingButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        makeBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakeBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(makeBookingButton, gbc);

        // Add Make Payment for Booking button
        gbc.gridy++;
        JButton makePaymentButton = createButton("Make Payment For Booking", "payment_icon.png");
        makePaymentButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakePaymentForBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(makePaymentButton, gbc);

        // Add Cancel Booking button
        gbc.gridy++;
        JButton cancelBookingButton = createButton("Cancel Booking", "cancel_icon.png");
        cancelBookingButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        cancelBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentCancelBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(cancelBookingButton, gbc);

        // Add Back button
        gbc.gridy++;
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);

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
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            // Add category-based colors
            if (text.contains("Cancel")) {
                button.setBackground(new Color(230, 240, 250)); // Light blue for user management
            } else if (text.contains("Make")) {
                button.setBackground(new Color(230, 250, 230)); // Light green for facility management
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
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
}