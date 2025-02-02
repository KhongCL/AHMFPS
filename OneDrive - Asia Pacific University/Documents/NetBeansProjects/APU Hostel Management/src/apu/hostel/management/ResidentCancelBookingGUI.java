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

public class ResidentCancelBookingGUI {
    private JFrame frame;
    private JPanel cancelBookingPanel;
    private DefaultTableModel tableModel; // Store the table model
    private Map<Integer, String[]> paymentDetailsMap; // Map to store payment details
    private APUHostelManagement.Resident resident; // Add resident field
    private JTable table;

    // Add new constructor
    public ResidentCancelBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Cancel Booking");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setLocationRelativeTo(null);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI(resident);
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        cancelBookingPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(cancelBookingPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Cancel Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        cancelBookingPanel.add(titleLabel, BorderLayout.NORTH);

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
        cancelBookingPanel.add(scrollPane, BorderLayout.CENTER);

        // Load cancellable bookings and populate table
        List<String[]> cancellableBookings = APUHostelManagement.Resident.getCancellableBookingsForResident(resident.getResidentID());
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); // Initialize the map
        int rowIndex = 0;
        for (String[] details : cancellableBookings) {
            paymentDetailsMap.put(rowIndex, details); // Store payment details in the map
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        // Add Cancel button
        JButton cancelButton = createButton("Cancel Booking", "cancel_icon.png");
        cancelButton.setPreferredSize(new Dimension(200, 40)); // Adjusted button size
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showCancelBookingPopup(paymentDetailsMap.get(selectedRow), selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a booking to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a panel for the bottom button
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancelButton);
        cancelBookingPanel.add(bottomPanel, BorderLayout.SOUTH);

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

    private void showCancelBookingPopup(String[] details, int rowIndex) {
        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(details[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);
        String username = resident.getUsername(); // Assuming there's a getUsername() method
        String roomType = APUHostelManagement.Resident.getRoomType(details[5]); // Get room type based on room ID

        // Create a panel for booking details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.add(new JLabel("<html><div style='text-align: left;'><b style='font-size:14px;'>Booking Details:</b><br/><br/>" +
                                    "Username: " + username + "<br/>" +
                                    "Start Date: " + startDate + "<br/>" +
                                    "End Date: " + endDate + "<br/>" +
                                    "Stay Duration: " + stayDuration + " days<br/>" +
                                    "Room Type: " + roomType + "<br/>" +
                                    "Room Number: " + roomNumber + "<br/>" +
                                    "Booking Status: " + details[10] + "<br/>" +
                                    "Payment Amount: RM" + details[6] + "</div></html>"));

        // Create confirmation dialog
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(detailsPanel, BorderLayout.CENTER);
        JLabel confirmLabel = new JLabel("Are you sure you want to cancel this booking?");
        confirmLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(confirmLabel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Booking Details",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            cancelBooking(details[0], rowIndex, details[5]);
        }
    }

    private void cancelBooking(String paymentID, int rowIndex, String roomID) {
        if (APUHostelManagement.Resident.cancelBooking(paymentID)) {
            JOptionPane.showMessageDialog(frame, "Booking cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.removeRow(rowIndex); // Remove the cancelled row from the table
            APUHostelManagement.Resident.updateRoomStatus1(roomID, "available"); // Use existing method instead
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while cancelling the booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        // Don't set a default size here, let individual calls specify the size
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