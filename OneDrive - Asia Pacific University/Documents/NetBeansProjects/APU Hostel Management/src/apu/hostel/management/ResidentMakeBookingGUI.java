package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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

        // Adjust the size of the pricing panel
        pricingPanel.setPreferredSize(new Dimension(1024, 200));

        mainPanel.add(pricingPanel, BorderLayout.NORTH);

        // Display room pricing
        displayRoomPricing(tableModel);

        // Room Type Selection and Date Input Fields
        JPanel selectionAndDatePanel = new JPanel(new BorderLayout(10, 10));
        selectionAndDatePanel.setPreferredSize(new Dimension(1024, 300)); // Adjust the size of the panel

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
        Dimension buttonSize = new Dimension(512, 30); // Half the width of the frame
        standardButton.setPreferredSize(buttonSize);
        largeButton.setPreferredSize(buttonSize);
        familyButton.setPreferredSize(buttonSize);

        standardButton.addActionListener(e -> selectRoomType("Standard"));
        largeButton.addActionListener(e -> selectRoomType("Large"));
        familyButton.addActionListener(e -> selectRoomType("Family"));

        buttonPanel.add(standardButton);
        buttonPanel.add(largeButton);
        buttonPanel.add(familyButton);

        JPanel buttonPanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelContainer.add(buttonPanel);

        selectionPanel.add(buttonPanelContainer, BorderLayout.CENTER);

        selectionAndDatePanel.add(selectionPanel, BorderLayout.NORTH);

        // Date Input Fields
        JPanel datePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel startDateLabel = new JLabel("Enter start date of your stay (yyyy-MM-dd): ");
        JLabel endDateLabel = new JLabel("Enter end date of your stay (yyyy-MM-dd): ");
        startDateField = new JTextField(20); // Increase the length of the text field
        endDateField = new JTextField(20); // Increase the length of the text field

        // Set placeholder text
        startDateField.setText("yyyy-MM-dd");
        endDateField.setText("yyyy-MM-dd");
        startDateField.setForeground(Color.GRAY);
        endDateField.setForeground(Color.GRAY);

        // Shrink the height of the text input boxes
        Dimension textFieldDimension = new Dimension(200, 25);
        startDateField.setPreferredSize(textFieldDimension);
        endDateField.setPreferredSize(textFieldDimension);

        // Add focus listeners to clear placeholder text
        startDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (startDateField.getText().equals("yyyy-MM-dd")) {
                    startDateField.setText("");
                    startDateField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (startDateField.getText().isEmpty()) {
                    startDateField.setText("yyyy-MM-dd");
                    startDateField.setForeground(Color.GRAY);
                }
            }
        });

        endDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (endDateField.getText().equals("yyyy-MM-dd")) {
                    endDateField.setText("");
                    endDateField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (endDateField.getText().isEmpty()) {
                    endDateField.setText("yyyy-MM-dd");
                    endDateField.setForeground(Color.GRAY);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        datePanel.add(startDateLabel, gbc);
        gbc.gridx = 1;
        datePanel.add(startDateField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        datePanel.add(endDateLabel, gbc);
        gbc.gridx = 1;
        datePanel.add(endDateField, gbc);

        // Add the datePanel to the selectionAndDatePanel
        selectionAndDatePanel.add(datePanel, BorderLayout.CENTER);

        // Make Booking Button
        JButton makeBookingButton = new JButton("Make Booking");
        makeBookingButton.addActionListener(e -> makeBooking());

        JPanel makeBookingButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        makeBookingButtonPanel.add(makeBookingButton);

        selectionAndDatePanel.add(makeBookingButtonPanel, BorderLayout.SOUTH);

        // Add the selectionAndDatePanel to the center of the main panel
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

    private void makeBooking() {
        // Validate room type selection
        if (selectedRoomType == null) {
            JOptionPane.showMessageDialog(frame, "Please select a room type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Validate start date
        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (startDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(frame, "You cannot travel back in time. Please enter a valid start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] dateParts = startDateField.getText().split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            if (isInvalidDate(year, month, day)) {
                JOptionPane.showMessageDialog(frame, "This date does not exist, please input a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please enter the date in yyyy-MM-dd format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Validate end date
        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!endDate.isAfter(startDate)) {
                JOptionPane.showMessageDialog(frame, "The end date must be after the start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] dateParts = endDateField.getText().split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            if (isInvalidDate(year, month, day)) {
                JOptionPane.showMessageDialog(frame, "This date does not exist, please input a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please enter the date in yyyy-MM-dd format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Select an available room based on room type
        String roomID = APUHostelManagement.Resident.selectAvailableRoomByType1(selectedRoomType);
        if (roomID == null) {
            JOptionPane.showMessageDialog(frame, "No available rooms of the selected type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Generate a new PaymentID
        String paymentID = APUHostelManagement.Resident.generatePaymentID1();
    
        // Get the ResidentID of the logged-in user
        String residentID = this.residentID;
    
        // Calculate the payment amount
        String feeRateID = APUHostelManagement.Resident.getFeeRateID(roomID);
        double paymentAmount = APUHostelManagement.Resident.calculatePaymentAmount1(startDate, endDate, feeRateID);
    
        // Get the current date and time for BookingDateTime
        String bookingDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        // Add a new line to payments.txt
        boolean bookingSuccess = APUHostelManagement.Resident.addBookingToFile(paymentID, residentID, startDate, endDate, roomID, paymentAmount, bookingDateTime);
        if (!bookingSuccess) {
            JOptionPane.showMessageDialog(frame, "An error occurred while saving the booking.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Update room status to unavailable
        APUHostelManagement.Resident.updateRoomStatus1(roomID, "unavailable");
    
        // Print confirmation message
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        String roomNumber = APUHostelManagement.Resident.getRoomNumber(roomID);
        JOptionPane.showMessageDialog(frame, "Booking successful.\nPayment ID: " + paymentID + "\nResident ID: " + residentID + "\nStart Date: " + startDate + "\nEnd Date: " + endDate + "\nStay Duration: " + daysBetween + " days\nRoom Number: " + roomNumber + "\nPayment Amount: RM " + paymentAmount, "Success", JOptionPane.INFORMATION_MESSAGE);

        // Reset fields after successful booking
        resetFields();
    }

    private void resetFields() {
        selectedRoomType = null;
        startDateField.setText("yyyy-MM-dd");
        startDateField.setForeground(Color.GRAY);
        endDateField.setText("yyyy-MM-dd");
        endDateField.setForeground(Color.GRAY);
        standardButton.setBackground(null);
        largeButton.setBackground(null);
        familyButton.setBackground(null);
    }

    private boolean isInvalidDate(int year, int month, int day) {
        switch (month) {
            case 2 -> {
                if (day > 29 || (day == 29 && !Year.isLeap(year))) {
                    return true;
                }
            }
            case 4, 6, 9, 11 -> {
                if (day > 30) {
                    return true;
                }
            }
            default -> {
                if (day > 31) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentMakeBookingGUI();
        });
    }
}