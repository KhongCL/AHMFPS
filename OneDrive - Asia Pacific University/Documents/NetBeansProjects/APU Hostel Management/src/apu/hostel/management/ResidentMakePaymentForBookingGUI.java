package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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

    public ResidentMakePaymentForBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize(resident.getResidentID());
    }

    public ResidentMakePaymentForBookingGUI() {
        String residentID = WelcomePageGUI.getCurrentResidentID(); // Retrieve the session for the currently logged-in resident
        if (residentID == null) {
            JOptionPane.showMessageDialog(null, "Please login as a resident to access this page.", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                new WelcomePageGUI();
            });
            return; // Ensure the rest of the constructor is not executed
        } else {
            initialize(residentID);
        }
    }

    private void initialize(String residentID) {
        frame = new JFrame("Make Payment for Booking");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for the main panel

        makePaymentPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(makePaymentPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Make Payment for Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        makePaymentPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table model and table
        tableModel = new DefaultTableModel(new Object[]{"Payment ID", "Resident ID", "Room Number", "Stay Duration", "Payment Amount", "Action"}, 0);
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the "Action" column is editable
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 5) {
                    return new ButtonRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(30); // Increase row height
        JScrollPane scrollPane = new JScrollPane(table);
        makePaymentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load unpaid bookings and populate table
        List<String[]> unpaidBookings = APUHostelManagement.Resident.getUnpaidBookingsForResident(residentID);
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); // Initialize the map
        int rowIndex = 0;
        for (String[] details : unpaidBookings) {
            paymentDetailsMap.put(rowIndex, details); // Store payment details in the map
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            JButton payButton = new JButton("Pay for Booking");
            payButton.setActionCommand(String.valueOf(rowIndex)); // Set the action command to the row index
            payButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int rowIndex = Integer.parseInt(e.getActionCommand());
                    showPaymentDetailsPopup(details, rowIndex);
                }
            });
            tableModel.addRow(new Object[]{details[0], details[1], roomNumber, stayDuration + " days", "RM" + details[6], payButton});
            rowIndex++;
        }

        // Add Back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(frame.getWidth(), 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI(resident);
                frame.dispose();
            }
        });

        // Create a panel for the bottom button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(backButton, BorderLayout.SOUTH);
        makePaymentPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showPaymentDetailsPopup(String[] details, int rowIndex) {
        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(details[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);

        String[][] data = {
                {"Payment ID", details[0]},
                {"Resident ID", details[1]},
                {"Staff ID", details[2]},
                {"Start Date", startDate.toString()},
                {"End Date", endDate.toString()},
                {"Stay Duration", stayDuration + " days"},
                {"Room Number", roomNumber},
                {"Payment Amount", "RM" + details[6]},
                {"Payment Status", details[7]},
                {"Booking Date and Time", details[8]},
                {"Payment Method", details[9]},
                {"Booking Status", details[10]}
        };

        String[] columnNames = {"Category", "Details"};

        JTable detailsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(detailsTable);

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

        // Create a panel to hold the details table and payment panel
        JPanel popupPanel = new JPanel(new BorderLayout(10, 10));
        popupPanel.add(scrollPane, BorderLayout.CENTER);
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
        if (success) {
            JOptionPane.showMessageDialog(frame, "Payment successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            paymentDetailsDialog.dispose(); // Close the payment details dialog
            tableModel.removeRow(rowIndex); // Remove the paid row from the table
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while processing the payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer for the "Action" column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                JButton button = (JButton) value;
                return button;
            }
            return this;
        }
    }

    // Custom editor for the "Action" column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof JButton) {
                button = (JButton) value;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showPaymentDetailsPopup(paymentDetailsMap.get(row), row);
                    }
                });
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentMakePaymentForBookingGUI();
        });
    }
}