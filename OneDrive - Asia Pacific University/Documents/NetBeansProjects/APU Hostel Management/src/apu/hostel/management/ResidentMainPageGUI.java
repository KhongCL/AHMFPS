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

public class ResidentMainPageGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel viewPaymentRecordsPanel;

    public ResidentMainPageGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Resident Main Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new CardLayout(10, 10)); // Use CardLayout for switching panels

        mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, "Main Panel");

        JLabel titleLabel = new JLabel("Resident Main Page", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add Update Personal Information button
        JButton updateInfoButton = new JButton("Update Personal Information");
        updateInfoButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        updateInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentUpdatePersonalInformationGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(updateInfoButton, gbc);

        // Add View Payment Records button
        gbc.gridy++;
        JButton viewPaymentRecordsButton = new JButton("View Payment Records");
        viewPaymentRecordsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        viewPaymentRecordsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showViewPaymentRecordsPanel();
            }
        });
        buttonPanel.add(viewPaymentRecordsButton, gbc);

        // Add Manage Bookings button
        gbc.gridy++;
        JButton manageBookingsButton = new JButton("Manage Bookings");
        manageBookingsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        manageBookingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI();
                frame.dispose();
            }
        });
        buttonPanel.add(manageBookingsButton, gbc);

        // Add Logout button
        gbc.gridy++;
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        buttonPanel.add(logoutButton, gbc);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Initialize the view payment records panel
        viewPaymentRecordsPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(viewPaymentRecordsPanel, "View Payment Records Panel");

        frame.setVisible(true);
    }

    private void showViewPaymentRecordsPanel() {
        viewPaymentRecordsPanel.removeAll();

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
        for (String[] details : relevantPayments) {
            JButton viewButton = new JButton("View");
            viewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showPaymentDetailsPopup(details);
                }
            });
            tableModel.addRow(new Object[]{details[0], details[6], details[8], viewButton});
        }

        // Add Back button
        JButton backButton = new JButton("Back to Resident Menu");
        backButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
                cl.show(frame.getContentPane(), "Main Panel");
            }
        });
        viewPaymentRecordsPanel.add(backButton, BorderLayout.SOUTH);

        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "View Payment Records Panel");
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
                {"Payment Amount", details[6]},
                {"Booking Date", details[8]},
                {"Room Number", roomNumber},
                {"Payment Method", details[9]},
                {"Booking Status", details[10]}
        };

        String[] columnNames = {"Category", "Details"};

        JTable detailsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(detailsTable);

        JOptionPane.showMessageDialog(frame, scrollPane, "Payment Details", JOptionPane.INFORMATION_MESSAGE);
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

    private void logout() {
        JOptionPane.showMessageDialog(frame, "Logging out...");
        frame.dispose();
        WelcomePageGUI welcomePage = new WelcomePageGUI();
        welcomePage.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResidentMainPageGUI();
        });
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
                        showPaymentDetailsPopup((String[]) table.getValueAt(row, 0));
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
}