package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StaffMakePaymentForResidentGUI {
    private JFrame frame;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String[]> paymentDetailsMap;
    private APUHostelManagement.Staff staff;

    public StaffMakePaymentForResidentGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Payment for Resident");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Back button panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new StaffMainPageGUI(staff);
            frame.dispose();
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        // Payment table
        tableModel = new DefaultTableModel(
            new Object[]{"Payment ID", "Resident ID", "Amount"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Make Payment button
        JButton makePaymentButton = createButton("Make Payment", "payment_icon2.png");
        makePaymentButton.addActionListener(e -> showPaymentConfirmation());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(makePaymentButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadPendingPayments();
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

    private void loadPendingPayments() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] payment = line.split(",");
                // Check if payment status is "pending" and booking status is "active"
                if (payment[7].equalsIgnoreCase("pending") && payment[10].equalsIgnoreCase("active")) {
                    paymentDetailsMap.put(row, payment);
                    tableModel.addRow(new Object[]{
                        payment[0],  // Payment ID
                        payment[1],  // Resident ID
                        "RM" + payment[6]  // Amount
                    });
                    row++;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading payments", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPaymentConfirmation() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a payment to make payment for.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] selectedPayment = paymentDetailsMap.get(selectedRow);
        
        // Create payment details table
        String[][] data = {
            {"Payment ID", selectedPayment[0]},
            {"Resident ID", selectedPayment[1]}, 
            {"Staff ID", selectedPayment[2]},
            {"Start Date", selectedPayment[3]},
            {"End Date", selectedPayment[4]},
            {"Room ID", selectedPayment[5]},
            {"Payment Amount", "RM" + selectedPayment[6]},
            {"Payment Status", selectedPayment[7]},
            {"Booking DateTime", selectedPayment[8]},
            {"Payment Method", selectedPayment[9]},
            {"Booking Status", selectedPayment[10]}
        };

        String[] columnNames = {"Field", "Value"};
        JTable detailsTable = new JTable(data, columnNames);
        detailsTable.setEnabled(false);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);
        
        JLabel confirmLabel = new JLabel("Are you sure to make payment for this booking?");
        confirmLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(confirmLabel, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, 
            "Payment Confirmation",
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            if (APUHostelManagement.Staff.processPendingPayment(
                    selectedPayment[0], 
                    staff.getStaffID())) {
                JOptionPane.showMessageDialog(frame, "Payment processed successfully");
                loadPendingPayments(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(frame, "Error processing payment", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
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