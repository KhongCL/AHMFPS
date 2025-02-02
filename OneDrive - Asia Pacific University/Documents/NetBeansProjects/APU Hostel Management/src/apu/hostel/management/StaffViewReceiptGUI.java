package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class StaffViewReceiptGUI {
    private JFrame frame;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String[]> paymentDetailsMap;
    private APUHostelManagement.Staff staff;

    public StaffViewReceiptGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("View Receipts");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Back button panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new StaffGenerateReceiptGUI(staff);
            frame.dispose();
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        // Payment table
        tableModel = new DefaultTableModel(
            new Object[]{"Receipt ID", "Payment ID", "Resident ID", "Staff ID", "Start Date", 
                        "End Date", "Room ID", "Amount", "Payment Method", "Receipt Date"}, 0
        );
        paymentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // View Receipt button
        JButton viewReceiptButton = createButton("View Receipt", "view_receipt_icon.png");
        viewReceiptButton.addActionListener(e -> viewReceipt());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewReceiptButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadCompletedPayments();
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

    private void loadCompletedPayments() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);
        
        Map<String, String[]> receiptMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("receipts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] receipt = line.split(",");
                receiptMap.put(receipt[1], receipt); // Map payment ID to receipt
            }
        } catch (IOException e) {
            // Handle if receipts.txt doesn't exist yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] payment = line.split(",");
                if (payment[2] != null && !payment[2].isEmpty() && 
                    payment[9] != null && !payment[9].isEmpty() &&
                    payment[7].equalsIgnoreCase("paid") &&
                    payment[10].equalsIgnoreCase("completed") &&
                    receiptMap.containsKey(payment[0])) {
                    
                    String[] receipt = receiptMap.get(payment[0]);
                    paymentDetailsMap.put(row, new String[]{receipt[0], payment[0], payment[1], 
                        payment[2], payment[3], payment[4], payment[5], payment[6], 
                        payment[9], receipt[3]});
                    
                    tableModel.addRow(new Object[]{receipt[0], payment[0], payment[1], 
                        payment[2], payment[3], payment[4], payment[5], payment[6], 
                        payment[9], receipt[3]});
                    row++;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading payments", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewReceipt() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a receipt to view.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] receiptDetails = paymentDetailsMap.get(selectedRow);
        
        // Create receipt panel with real-world style
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Receipt header
        JLabel headerLabel = new JLabel("APU HOSTEL PAYMENT RECEIPT");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(headerLabel);
        receiptPanel.add(Box.createVerticalStrut(20));
        
        // Receipt details
        String[][] details = {
            {"Receipt ID:", receiptDetails[0]},
            {"Payment ID:", receiptDetails[1]},
            {"Resident ID:", receiptDetails[2]},
            {"Staff ID:", receiptDetails[3]},
            {"Start Date:", receiptDetails[4]},
            {"End Date:", receiptDetails[5]},
            {"Room ID:", receiptDetails[6]},
            {"Amount Paid:", "RM " + receiptDetails[7]},
            {"Payment Method:", receiptDetails[8]},
            {"Receipt Date:", receiptDetails[9]}
        };
        
        for (String[] detail : details) {
            JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            detailPanel.add(new JLabel(detail[0]));
            detailPanel.add(new JLabel(detail[1]));
            receiptPanel.add(detailPanel);
        }
        
        // Show receipt in dialog
        JOptionPane.showMessageDialog(frame, receiptPanel, 
            "Receipt Details", JOptionPane.PLAIN_MESSAGE);
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