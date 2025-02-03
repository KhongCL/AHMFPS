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
    private Map<Integer, String[]> paymentDetailsMap; 
    private String selectedPaymentMethod = null; 
    private JButton creditCardButton;
    private JButton bankTransferButton;
    private JButton cashButton;
    private JDialog paymentDetailsDialog; 
    private DefaultTableModel tableModel; 
    private APUHostelManagement.Resident resident;
    private JTable table;

    public ResidentMakePaymentForBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Payment for Booking");
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

        makePaymentPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(makePaymentPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Make Payment for Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        makePaymentPanel.add(titleLabel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new Object[]{"Room Number", "Stay Duration", "Booking Date and Time", "Payment Amount (RM)"}, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table.setRowHeight(30); 
        JScrollPane scrollPane = new JScrollPane(table);
        makePaymentPanel.add(scrollPane, BorderLayout.CENTER);

        
        List<String[]> unpaidBookings = APUHostelManagement.Resident.getUnpaidBookingsForResident(resident.getResidentID());
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); 
        int rowIndex = 0;
        for (String[] details : unpaidBookings) {
            paymentDetailsMap.put(rowIndex, details); 
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        
        JButton payButton = createButton("Pay for Booking", "payment_icon2.png");
        payButton.setPreferredSize(new Dimension(200, 40)); 
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

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(payButton);
        makePaymentPanel.add(bottomPanel, BorderLayout.SOUTH);

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
                
            }
        });
    }

    private void showPaymentDetailsPopup(String[] details, int rowIndex) {
        selectedPaymentMethod = null; 

        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(details[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);
        String username = resident.getUsername(); 
        String roomType = APUHostelManagement.Resident.getRoomType(details[5]); 

        
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

        
        JPanel paymentPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JLabel paymentMethodLabel = new JLabel("Select Payment Method:");
        paymentPanel.add(paymentMethodLabel);

        
        creditCardButton = createButton("Credit Card", "credit_card_icon.png");
        bankTransferButton = createButton("Bank Transfer", "bank_transfer_icon.png");
        cashButton = createButton("Cash", "cash_icon.png");

        creditCardButton.addActionListener(e -> selectPaymentMethod("credit_card"));
        bankTransferButton.addActionListener(e -> selectPaymentMethod("bank_transfer"));
        cashButton.addActionListener(e -> selectPaymentMethod("cash"));

        
        Dimension buttonSize = new Dimension(200, 30);
        creditCardButton.setPreferredSize(buttonSize);
        bankTransferButton.setPreferredSize(buttonSize);
        cashButton.setPreferredSize(buttonSize);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(creditCardButton);
        buttonPanel.add(bankTransferButton);
        buttonPanel.add(cashButton);

        paymentPanel.add(buttonPanel);

        
        JButton confirmPaymentButton = createButton("Confirm Payment", "approve_icon.png");
        confirmPaymentButton.setPreferredSize(buttonSize);
        confirmPaymentButton.addActionListener(e -> confirmPayment(details[0], rowIndex));

        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmButtonPanel.add(confirmPaymentButton);

        paymentPanel.add(confirmButtonPanel);

        
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
        paymentDetailsDialog.dispose(); 
        if (success) {
            JOptionPane.showMessageDialog(frame, "Payment successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.removeRow(rowIndex); 
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while processing the payment.", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        return button;
    }

    private void addButtonHoverEffect(JButton button) {
        
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