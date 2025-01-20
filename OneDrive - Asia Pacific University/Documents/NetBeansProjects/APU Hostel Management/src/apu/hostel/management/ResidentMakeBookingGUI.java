package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResidentMakeBookingGUI {
    private JFrame frame;
    private String selectedRoomType = null;
    private JTextField startDateField;
    private JTextField endDateField;
    private String residentID;

    public ResidentMakeBookingGUI() {
        residentID = WelcomePageGUI.getCurrentResidentID(); // Retrieve the session for the currently logged-in resident
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Booking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        // Title Label
        JLabel titleLabel = new JLabel("Make Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        frame.add(titleLabel, BorderLayout.NORTH);

        // Room Pricing Table
        JPanel pricingPanel = new JPanel(new BorderLayout());
        JLabel pricingLabel = new JLabel("Room Pricing", JLabel.CENTER);
        pricingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pricingPanel.add(pricingLabel, BorderLayout.NORTH);

        String[] columnNames = {"Room Type", "Room Capacity", "Daily Rate", "Weekly Rate", "Monthly Rate", "Yearly Rate"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable pricingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pricingTable);
        pricingPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(pricingPanel, BorderLayout.CENTER);

        // Display room pricing
        displayRoomPricing(tableModel);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(frame.getWidth(), 50)); // Set button size
        backButton.addActionListener(e -> {
            new ResidentManageBookingsGUI();
            frame.dispose();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void displayRoomPricing(DefaultTableModel tableModel) {
        // Call the getRoomPricing method from APUHostelManagement and populate the table
        List<String[]> roomPricing = APUHostelManagement.Resident.getRoomPricing();
        for (String[] row : roomPricing) {
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentMakeBookingGUI();
        });
    }
}