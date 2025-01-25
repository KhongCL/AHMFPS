package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentMakePaymentForBookingGUI {
    private JFrame frame;
    private JPanel makePaymentPanel;
    private Map<Integer, String[]> paymentDetailsMap; // Map to store payment details
    private String selectedPaymentMethod = null; // Store the selected payment method
    private JButton creditCardButton;
    private JButton bankTransferButton;
    private JButton cashButton;
    private JDialog paymentDetailsDialog; // Store the payment details dialog
    private DefaultTableModel tableModel; // Store the table model
    private APUHostelManagement.Resident resident;
    private JTable table;

    public ResidentMakePaymentForBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Payment for Booking");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for the main panel

        // Top panel for the back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI(resident);
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        makePaymentPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(makePaymentPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Make Payment for Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        makePaymentPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table model and table
        tableModel = new DefaultTableModel(new Object[]{"Room Number", "Stay Duration", "Booking Date and Time", "Payment Amount"}, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        table.setRowHeight(30); // Increase row height
        JScrollPane scrollPane = new JScrollPane(table);
        makePaymentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load unpaid bookings and populate table
        List<String[]> unpaidBookings = APUHostelManagement.Resident.getUnpaidBookingsForResident(resident.getResidentID());
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); // Initialize the map
        int rowIndex = 0;
        for (String[] details : unpaidBookings) {
            paymentDetailsMap.put(rowIndex, details); // Store payment details in the map
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        // Add Pay for Booking button
        JButton payButton = new JButton("Pay for Booking");
        payButton.setPreferredSize(new Dimension(200, 40)); // Adjusted button size
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showPaymentDetailsPopup(paymentDetailsMap.get(selectedRow), selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a booking to pay for.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a panel for the bottom button
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(payButton);
        makePaymentPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showPaymentDetailsPopup(String[] details, int rowIndex) {
        selectedPaymentMethod = null; // Reset the selected payment method each time the popup is shown

        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(details[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);
        String username = resident.getUsername(); // Assuming there's a getUsername() method
        String roomType = APUHostelManagement.Resident.getRoomType(details[5]); // Get room type based on room ID

        // Create a panel for payment details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.add(new JLabel("<html><div style='text-align: left;'><b style='font-size:14px;'>Payment Details:</b><br/><br/>" +
                                    "Username: " + username + "<br/>" +
                                    "Start Date: " + startDate + "<br/>" +
                                    "End Date: " + endDate + "<br/>" +
                                    "Stay Duration: " + stayDuration + " days<br/>" +
                                    "Room Type: " + roomType + "<br/>" +
                                    "Room Number: " + roomNumber + "<br/>" +
                                    "Booking Status: " + details[10] + "<br/>" +
                                    "Payment Amount: RM" + details[6] + "</div></html>"));

        // Create a panel for payment method selection and confirmation
        JPanel paymentPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JLabel paymentMethodLabel = new JLabel("Select Payment Method:");
        paymentPanel.add(paymentMethodLabel);

        // Create payment method buttons
        creditCardButton = new JButton("Credit Card");
        bankTransferButton = new JButton("Bank Transfer");
        cashButton = new JButton("Cash");

        creditCardButton.addActionListener(e -> selectPaymentMethod("credit_card"));
        bankTransferButton.addActionListener(e -> selectPaymentMethod("bank_transfer"));
        cashButton.addActionListener(e -> selectPaymentMethod("cash"));

        // Set button size to half the width of the popup and center them
        Dimension buttonSize = new Dimension(200, 30);
        creditCardButton.setPreferredSize(buttonSize);
        bankTransferButton.setPreferredSize(buttonSize);
        cashButton.setPreferredSize(buttonSize);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(creditCardButton);
        buttonPanel.add(bankTransferButton);
        buttonPanel.add(cashButton);

        paymentPanel.add(buttonPanel);

        // Create confirm payment button
        JButton confirmPaymentButton = new JButton("Confirm Payment");
        confirmPaymentButton.setPreferredSize(buttonSize);
        confirmPaymentButton.addActionListener(e -> confirmPayment(details[0], rowIndex));

        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmButtonPanel.add(confirmPaymentButton);

        paymentPanel.add(confirmButtonPanel);

        // Create a panel to hold the details panel and payment panel
        JPanel popupPanel = new JPanel(new BorderLayout(10, 10));
        popupPanel.add(detailsPanel, BorderLayout.CENTER);
        popupPanel.add(paymentPanel, BorderLayout.SOUTH);

        paymentDetailsDialog = new JDialog(frame, "Payment Details", true);
        paymentDetailsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        paymentDetailsDialog.getContentPane().add(popupPanel);
        paymentDetailsDialog.pack();
        paymentDetailsDialog.setLocationRelativeTo(frame);
        paymentDetailsDialog.setVisible(true);
    }

    private void selectPaymentMethod(String paymentMethod) {
        selectedPaymentMethod = paymentMethod;
        creditCardButton.setBackground(null);
        bankTransferButton.setBackground(null);
        cashButton.setBackground(null);

        switch (paymentMethod) {
            case "credit_card":
                creditCardButton.setBackground(Color.LIGHT_GRAY);
                break;
            case "bank_transfer":
                bankTransferButton.setBackground(Color.LIGHT_GRAY);
                break;
            case "cash":
                cashButton.setBackground(Color.LIGHT_GRAY);
                break;
        }
    }

    private void confirmPayment(String paymentID, int rowIndex) {
        if (selectedPaymentMethod == null) {
            JOptionPane.showMessageDialog(frame, "Please select a payment method.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = APUHostelManagement.Resident.updatePaymentStatusAndMethod(paymentID, selectedPaymentMethod);
        paymentDetailsDialog.dispose(); // Close the payment details dialog before showing the success message
        if (success) {
            JOptionPane.showMessageDialog(frame, "Payment successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.removeRow(rowIndex); // Remove the paid row from the table
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while processing the payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}