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
    private JButton standardButton;
    private JButton largeButton;
    private JButton familyButton;

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

        // Main Panel to hold table and other components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

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

        mainPanel.add(pricingPanel, BorderLayout.NORTH);

        // Display room pricing
        displayRoomPricing(tableModel);

        // Room Type Selection and Date Input Fields
        JPanel selectionAndDatePanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Room Type Selection
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JLabel selectionLabel = new JLabel("Select Room Type:", JLabel.CENTER);
        selectionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectionPanel.add(selectionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        standardButton = new JButton("Standard");
        largeButton = new JButton("Large");
        familyButton = new JButton("Family");

        // Adjust button size
        standardButton.setPreferredSize(new Dimension(200, 30)); // Increase width, reduce height
        largeButton.setPreferredSize(new Dimension(200, 30)); // Increase width, reduce height
        familyButton.setPreferredSize(new Dimension(200, 30)); // Increase width, reduce height

        standardButton.addActionListener(e -> selectRoomType("Standard"));
        largeButton.addActionListener(e -> selectRoomType("Large"));
        familyButton.addActionListener(e -> selectRoomType("Family"));

        buttonPanel.add(standardButton);
        buttonPanel.add(largeButton);
        buttonPanel.add(familyButton);

        selectionPanel.add(buttonPanel, BorderLayout.CENTER);

        // Date Input Fields
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel startDateLabel = new JLabel("Enter start date of your stay (yyyy-MM-dd): ");
        JLabel endDateLabel = new JLabel("Enter end date of your stay (yyyy-MM-dd): ");
        startDateField = new JTextField();
        endDateField = new JTextField();

        // Set placeholder text
        startDateField.setText("Start Date");
        endDateField.setText("End Date");
        startDateField.setForeground(Color.GRAY);
        endDateField.setForeground(Color.GRAY);

        // Add focus listeners to clear placeholder text
        startDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (startDateField.getText().equals("Start Date")) {
                    startDateField.setText("");
                    startDateField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (startDateField.getText().isEmpty()) {
                    startDateField.setText("Start Date");
                    startDateField.setForeground(Color.GRAY);
                }
            }
        });

        endDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (endDateField.getText().equals("End Date")) {
                    endDateField.setText("");
                    endDateField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (endDateField.getText().isEmpty()) {
                    endDateField.setText("End Date");
                    endDateField.setForeground(Color.GRAY);
                }
            }
        });

        datePanel.add(startDateLabel);
        datePanel.add(startDateField);
        datePanel.add(endDateLabel);
        datePanel.add(endDateField);

        selectionAndDatePanel.add(selectionPanel);
        selectionAndDatePanel.add(datePanel);

        mainPanel.add(selectionAndDatePanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);

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

    private void selectRoomType(String roomType) {
        selectedRoomType = roomType;
        standardButton.setBackground(null);
        largeButton.setBackground(null);
        familyButton.setBackground(null);

        switch (roomType) {
            case "Standard":
                standardButton.setBackground(Color.LIGHT_GRAY);
                break;
            case "Large":
                largeButton.setBackground(Color.LIGHT_GRAY);
                break;
            case "Family":
                familyButton.setBackground(Color.LIGHT_GRAY);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentMakeBookingGUI();
        });
    }
}