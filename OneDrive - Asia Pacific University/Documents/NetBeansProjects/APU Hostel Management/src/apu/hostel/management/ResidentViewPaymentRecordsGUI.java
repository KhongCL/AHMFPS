package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentViewPaymentRecordsGUI {
    private JFrame frame;
    private JPanel viewPaymentRecordsPanel;
    private Map<Integer, String[]> paymentDetailsMap; // Map to store payment details

    public ResidentViewPaymentRecordsGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("View Payment Records");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for the main panel

        viewPaymentRecordsPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(viewPaymentRecordsPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("View Payment Records", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        viewPaymentRecordsPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table model and table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Payment ID", "Payment Amount", "Booking Date", "Action"}, 0);
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the "Action" column is editable
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 3) {
                    return new ButtonRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(30); // Increase row height
        JScrollPane scrollPane = new JScrollPane(table);
        viewPaymentRecordsPanel.add(scrollPane, BorderLayout.CENTER);

        // Load payment records and populate table
        String residentID = WelcomePageGUI.getCurrentResidentID(); // Retrieve residentID
        List<String[]> relevantPayments = APUHostelManagement.Resident.viewPaymentRecords(residentID);
        paymentDetailsMap = new HashMap<>(); // Initialize the map
        int rowIndex = 0;
        for (String[] details : relevantPayments) {
            paymentDetailsMap.put(rowIndex, details); // Store payment details in the map
            JButton viewButton = new JButton("View");
            viewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showPaymentDetailsPopup(details);
                }
            });
            tableModel.addRow(new Object[]{details[0], "RM" + details[6], details[8], viewButton});
            rowIndex++;
        }

        // Add Back button (same as in ResidentUpdatePersonalInformationGUI)
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 21)); // Adjusted font size
        backButton.setPreferredSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMaximumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.setMinimumSize(new Dimension(102, 57)); // Adjusted button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI();
                frame.dispose();
            }
        });

        // Create a panel for the bottom button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(backButton, BorderLayout.SOUTH);
        viewPaymentRecordsPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showPaymentDetailsPopup(String[] details) {
        String roomNumber = getRoomNumber(details[5]);
        LocalDate startDate = LocalDate.parse(details[3]);
        LocalDate endDate = LocalDate.parse(details[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);

        String[][] data = {
                {"Payment ID", details[0]},
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

        JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(frame, "Payment Details");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private String getRoomNumber(String roomID) {
        // Read room data from rooms.txt and store it in a map
        Map<String, String> roomMap = new HashMap<>();
        try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
            String line;
            while ((line = roomReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    roomMap.put(parts[0], parts[3]); // Assuming parts[0] is RoomID and parts[3] is RoomNumber
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the room data.");
        }
        return roomMap.getOrDefault(roomID, "Unknown Room");
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
                        showPaymentDetailsPopup(paymentDetailsMap.get(row));
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
            new ResidentViewPaymentRecordsGUI();
        });
    }
}