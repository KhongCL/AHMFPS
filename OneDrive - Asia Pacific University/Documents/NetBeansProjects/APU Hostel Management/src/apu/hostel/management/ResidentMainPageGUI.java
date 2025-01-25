package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentMainPageGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private APUHostelManagement.Resident resident;

    public ResidentMainPageGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

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

        // Update Personal Information button
        JButton updateInfoButton = new JButton("Update Personal Information");
        updateInfoButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        updateInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (resident == null) {
                    JOptionPane.showMessageDialog(frame, "Please login to update personal information.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    new ResidentManageProfileGUI(resident); // Launch ResidentManageProfileGUI with resident info
                    frame.dispose();
                }
            }
        });
        buttonPanel.add(updateInfoButton, gbc);

        // Add View Payment Records button
        gbc.gridy++;
        JButton viewPaymentRecordsButton = new JButton("View Payment Records");
        viewPaymentRecordsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        viewPaymentRecordsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentViewPaymentRecordsGUI(resident); // Pass resident
                frame.dispose(); // Close the Resident Main Page
            }
        });
        buttonPanel.add(viewPaymentRecordsButton, gbc);

        // Add Manage Bookings button
        gbc.gridy++;
        JButton manageBookingsButton = new JButton("Manage Bookings");
        manageBookingsButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        manageBookingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI(resident); // Pass resident
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

        frame.setVisible(true);
    }

    private void logout() {
        JOptionPane.showMessageDialog(frame, "Logging out...");
        frame.dispose();
        WelcomePageGUI welcomePage = new WelcomePageGUI();
        welcomePage.setVisible(true);
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
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}