package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentViewPaymentRecordsGUI {
    private JFrame frame;
    private JPanel viewPaymentRecordsPanel;
    private Map<Integer, String[]> paymentDetailsMap; // Map to store payment details
    private APUHostelManagement.Resident resident; // Add resident field
    private JTable table;

    // Add new constructor
    public ResidentViewPaymentRecordsGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("View Payment Records");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for the main panel

        // Top panel for the back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        viewPaymentRecordsPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(viewPaymentRecordsPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("View Payment Records", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        viewPaymentRecordsPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table model and table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Room Number", "Stay Duration", "Booking Date and Time", "Payment Amount"}, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        table.setRowHeight(30); // Increase row height
        JScrollPane scrollPane = new JScrollPane(table);
        viewPaymentRecordsPanel.add(scrollPane, BorderLayout.CENTER);

        // Load payment records and populate table
        List<String[]> relevantPayments = APUHostelManagement.Resident.viewPaymentRecords(resident.getResidentID());
        if (relevantPayments.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No payment records found for your account.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        paymentDetailsMap = new HashMap<>();
        int rowIndex = 0;
        for (String[] details : relevantPayments) {
            paymentDetailsMap.put(rowIndex, details);
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]); 
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        // Add View Payment Details button
        JButton viewButton = new JButton("View Payment Details");
        viewButton.setPreferredSize(new Dimension(200, 40)); // Adjusted button size
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showPaymentDetailsPopup(paymentDetailsMap.get(selectedRow));
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a payment record to view.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a panel for the bottom button
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewButton);
        viewPaymentRecordsPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showPaymentDetailsPopup(String[] details) {
        try {
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]);
            LocalDate startDate = LocalDate.parse(details[3]);
            LocalDate endDate = LocalDate.parse(details[4]); 
            long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);

            String[][] data = {
                {"Payment Status", details[7]},
                {"Start Date", startDate.toString()},
                {"End Date", endDate.toString()},
                {"Stay Duration", stayDuration + " days"},
                {"Payment Amount", "RM" + details[6]},
                {"Booking Date", details[8]},
                {"Room Number", roomNumber},
                {"Payment Method", details[9]},
                {"Booking Status", details[10]}
            };

            String[] columnNames = {"Category", "Details"};
            JTable detailsTable = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(detailsTable);

            JOptionPane optionPane = new JOptionPane(scrollPane, 
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(frame, "Payment Details");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(frame, 
                "Error parsing dates in payment record", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}