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

public class ResidentCancelBookingGUI {
    private JFrame frame;
    private JPanel cancelBookingPanel;
    private DefaultTableModel tableModel; // Store the table model
    private Map<Integer, String[]> paymentDetailsMap; // Map to store payment details
    private APUHostelManagement.Resident resident; // Add resident field

    // Add new constructor
    public ResidentCancelBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize(resident.getResidentID());
    }

    public ResidentCancelBookingGUI() {
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
        frame = new JFrame("Cancel Booking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        cancelBookingPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(cancelBookingPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Cancel Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        cancelBookingPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table model and table
        tableModel = new DefaultTableModel(new Object[]{"Payment ID", "Room Number", "Stay Duration", "Payment Amount", "Booking Date Time", "Action"}, 0);
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
        cancelBookingPanel.add(scrollPane, BorderLayout.CENTER);

        // Load cancellable bookings and populate table
        List<String[]> cancellableBookings = APUHostelManagement.Resident.getCancellableBookingsForResident(residentID);
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); // Initialize the map
        int rowIndex = 0;
        for (String[] details : cancellableBookings) {
            paymentDetailsMap.put(rowIndex, details); // Store payment details in the map
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setActionCommand(String.valueOf(rowIndex)); // Set the action command to the row index
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int rowIndex = Integer.parseInt(e.getActionCommand());
                    showCancelBookingPopup(details, rowIndex);
                }
            });
            tableModel.addRow(new Object[]{details[0], roomNumber, stayDuration + " days", "RM" + details[6], details[8], cancelButton});
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
        cancelBookingPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showCancelBookingPopup(String[] details, int rowIndex) {
        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(details[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);

        String[][] data = {
                {"Payment ID", details[0]},
                {"Resident ID", details[1]},
                {"Start Date", startDate.toString()},
                {"End Date", endDate.toString()},
                {"Stay Duration", stayDuration + " days"},
                {"Room Number", roomNumber},
                {"Payment Amount", "RM" + details[6]},
                {"Payment Status", details[7]},
                {"Booking Date Time", details[8]},
                {"Booking Status", details[10]}
        };

        String[] columnNames = {"Category", "Details"};

        JTable detailsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(detailsTable);

        // Create a panel for confirmation
        JPanel confirmationPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JLabel confirmationLabel = new JLabel("Are you sure you want to cancel this booking?", JLabel.CENTER);
        confirmationPanel.add(confirmationLabel);

        // Create Yes and No buttons
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        yesButton.addActionListener(e -> {
            cancelBooking(details[0], rowIndex);
            ((JDialog) SwingUtilities.getWindowAncestor(yesButton)).dispose();
        });

        noButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(noButton)).dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        confirmationPanel.add(buttonPanel);

        // Create a panel to hold the details table and confirmation panel
        JPanel popupPanel = new JPanel(new BorderLayout(10, 10));
        popupPanel.add(scrollPane, BorderLayout.CENTER);
        popupPanel.add(confirmationPanel, BorderLayout.SOUTH);

        JDialog cancelBookingDialog = new JDialog(frame, "Booking Details", true);
        cancelBookingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        cancelBookingDialog.getContentPane().add(popupPanel);
        cancelBookingDialog.pack();
        cancelBookingDialog.setLocationRelativeTo(frame);
        cancelBookingDialog.setVisible(true);
    }

    private void cancelBooking(String paymentID, int rowIndex) {
        if (APUHostelManagement.Resident.cancelBooking(paymentID)) {
            JOptionPane.showMessageDialog(frame, "Booking cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.removeRow(rowIndex); // Remove the cancelled row from the table
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while cancelling the booking.", "Error", JOptionPane.ERROR_MESSAGE);
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
                        showCancelBookingPopup(paymentDetailsMap.get(row), row);
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
            new ResidentCancelBookingGUI();
        });
    }
}