package apu.hostel.management;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StaffGenerateReceiptGUI {
    private JFrame frame;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String[]> paymentDetailsMap;
    private APUHostelManagement.Staff staff;

    public StaffGenerateReceiptGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Generate Receipt");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));

        // Back button panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new StaffMainPageGUI(staff);
            frame.dispose();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        // View Receipts button
        JButton viewReceiptsButton = createButton("View Receipts", "view_receipt_icon.png");
        viewReceiptsButton.addActionListener(e -> {
            new StaffViewReceiptGUI(staff);
            frame.dispose();
        });
        topPanel.add(viewReceiptsButton, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Payment table
        tableModel = new DefaultTableModel(
            new Object[]{"Payment ID", "Resident ID", "Staff ID", "Start Date", "End Date", 
                        "Room ID", "Amount", "Payment Method", "Booking Date"}, 0
        );
        paymentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Generate Receipt button
        JButton generateReceiptButton = createButton("Generate Receipt", "generate_receipt_icon.png");
        generateReceiptButton.addActionListener(e -> generateReceipt());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(generateReceiptButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadEligiblePayments();
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

    private void loadEligiblePayments() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] payment = line.split(",");
                // Check if payment is eligible (has staff ID, payment method, paid status, and active status)
                if (payment[2] != null && !payment[2].isEmpty() && 
                    payment[9] != null && !payment[9].isEmpty() &&
                    payment[7].equalsIgnoreCase("paid") &&
                    payment[10].equalsIgnoreCase("active")) {
                    
                    paymentDetailsMap.put(row, payment);
                    tableModel.addRow(new Object[]{
                        payment[0], payment[1], payment[2], payment[3], payment[4],
                        payment[5], payment[6], payment[9], payment[8]
                    });
                    row++;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading payments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReceipt() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a payment to generate receipt for.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] selectedPayment = paymentDetailsMap.get(selectedRow);
        
        // Create payment details panel for confirmation
        JPanel detailsPanel = new JPanel(new BorderLayout());
        String[][] data = {
            {"Payment ID", selectedPayment[0]},
            {"Resident ID", selectedPayment[1]},
            {"Staff ID", selectedPayment[2]},
            {"Start Date", selectedPayment[3]},
            {"End Date", selectedPayment[4]},
            {"Room ID", selectedPayment[5]},
            {"Amount", "RM " + selectedPayment[6]},
            {"Payment Method", selectedPayment[9]},
            {"Booking Date", selectedPayment[8]}
        };
        JTable detailsTable = new JTable(data, new String[]{"Field", "Value"});
        detailsPanel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(frame, detailsPanel, 
            "Confirm Receipt Generation", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            if (APUHostelManagement.Staff.generateReceipt(
                    selectedPayment[0], 
                    staff.getStaffID())) {
                JOptionPane.showMessageDialog(frame, "Receipt generated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEligiblePayments();
            } else {
                JOptionPane.showMessageDialog(frame, "Error generating receipt", 
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
