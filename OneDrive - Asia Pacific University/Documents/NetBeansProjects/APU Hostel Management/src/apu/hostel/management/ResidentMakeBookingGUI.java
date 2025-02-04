package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ResidentMakeBookingGUI {
    private JFrame frame;
    private String selectedRoomType = null;
    private JButton standardButton;
    private JButton largeButton;
    private JButton familyButton;
    private APUHostelManagement.Resident resident; 
    private JTable pricingTable;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    
    public ResidentMakeBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Booking");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Make Booking - " + resident.getUsername());
        frame.setLocationRelativeTo(null);

        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(e -> {
            new ResidentManageBookingsGUI(resident);
            frame.dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Make Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        // Create a panel to hold the table and approve button
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel pricingPanel = new JPanel(new BorderLayout());
        JLabel pricingLabel = new JLabel("Room Pricing", JLabel.CENTER);
        pricingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pricingPanel.add(pricingLabel, BorderLayout.NORTH);

        String[] columnNames = {"Room Type", "Room Capacity", "Daily Rate (RM)", "Weekly Rate (RM)", "Monthly Rate (RM)", "Yearly Rate (RM)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        JTable pricingTable = new JTable(tableModel);

        pricingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pricingTable.getTableHeader().setReorderingAllowed(false);
        pricingTable.setRowHeight(30);
        pricingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        pricingTable.setFont(new Font("Arial", Font.PLAIN, 12));
        pricingTable.setGridColor(Color.LIGHT_GRAY);
        pricingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        pricingTable.setSelectionBackground(new Color(230, 240, 250));
        pricingTable.setSelectionForeground(Color.BLACK);
        pricingTable.setIntercellSpacing(new Dimension(5, 5));
        pricingTable.setShowGrid(true);
        pricingTable.setFillsViewportHeight(true);

        pricingTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                return c;
            }
        });

        pricingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = pricingTable.getSelectedRow();
                    if (row != -1) {
                        showDetailedPricing(row);
                    }
                }
            }
        });

        pricingTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Room Type
        pricingTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Capacity
        pricingTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Daily Rate
        pricingTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Weekly Rate
        pricingTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Monthly Rate
        pricingTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(pricingTable);
        pricingPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        JLabel hyperlinkLabel = new JLabel("<html><a href=''>How do we calculate our pricing?</a></html>");
        hyperlinkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hyperlinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hyperlinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPricingExplanation();
            }
        });
        
        hyperlinkLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                hyperlinkLabel.setText("<html><a href='' style='color:#FF4081'>How do we calculate our pricing?</a></html>");
            }
            public void mouseExited(MouseEvent evt) {
                hyperlinkLabel.setText("<html><a href=''>How do we calculate our pricing?</a></html>");
            }
        });

        pricingPanel.add(hyperlinkLabel, BorderLayout.SOUTH);

        
        pricingPanel.setPreferredSize(new Dimension(1024, 200));
        
        displayRoomPricing(tableModel);
        
        JPanel selectionAndDatePanel = new JPanel(new BorderLayout(10, 10));
        selectionAndDatePanel.setPreferredSize(new Dimension(1024, 300)); 

        
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JLabel selectionLabel = new JLabel("Select Room Type:", JLabel.CENTER);
        selectionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectionPanel.add(selectionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        standardButton = createButton("Standard", "standard_icon.png");
        largeButton = createButton("Large", "large_icon.png");
        familyButton = createButton("Family", "family_icon.png");

        
        Dimension buttonSize = new Dimension(250, 40); 
        standardButton.setPreferredSize(buttonSize);
        largeButton.setPreferredSize(buttonSize);
        familyButton.setPreferredSize(buttonSize);

        standardButton.addActionListener(e -> selectRoomType("Standard"));
        largeButton.addActionListener(e -> selectRoomType("Large"));
        familyButton.addActionListener(e -> selectRoomType("Family"));

        buttonPanel.add(standardButton);
        buttonPanel.add(largeButton);
        buttonPanel.add(familyButton);

        JPanel buttonPanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelContainer.add(buttonPanel);

        selectionPanel.add(buttonPanelContainer, BorderLayout.CENTER);

        selectionAndDatePanel.add(selectionPanel, BorderLayout.NORTH);

        
        JPanel datePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel startDateLabel = new JLabel("Enter start date of your stay (yyyy-MM-dd): ");
        JLabel endDateLabel = new JLabel("Enter end date of your stay (yyyy-MM-dd): ");
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner = new JSpinner(new SpinnerDateModel());

        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        endDateSpinner.setEditor(endDateEditor);

        Dimension textFieldDimension = new Dimension(200, 25);
        startDateSpinner.setPreferredSize(textFieldDimension);
        endDateSpinner.setPreferredSize(textFieldDimension);

        gbc.gridx = 0;
        gbc.gridy = 0;
        datePanel.add(startDateLabel, gbc);
        gbc.gridx = 1;
        datePanel.add(startDateSpinner, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        datePanel.add(endDateLabel, gbc);
        gbc.gridx = 1;
        datePanel.add(endDateSpinner, gbc);

        
        selectionAndDatePanel.add(datePanel, BorderLayout.CENTER);

        
        JButton makeBookingButton = createButton("Make Booking", "booking_icon.png");
        makeBookingButton.addActionListener(e -> showBookingDetails());

        JPanel makeBookingButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        makeBookingButtonPanel.add(makeBookingButton);

        selectionAndDatePanel.add(makeBookingButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(pricingPanel, BorderLayout.NORTH);
        contentPanel.add(selectionAndDatePanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        this.pricingTable = pricingTable;

        JComponent startComp = startDateSpinner.getEditor();
        JFormattedTextField startField = ((JSpinner.DefaultEditor) startComp).getTextField();
        startField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    endDateSpinner.requestFocus();
                }
            }
        });

        JComponent endComp = endDateSpinner.getEditor();
        JFormattedTextField endField = ((JSpinner.DefaultEditor) endComp).getTextField();
        endField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    makeBookingButton.doClick();
                }
            }
        });
 
        startField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                startDateSpinner.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255)));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                startDateSpinner.setBorder(null);
                validateDates();
            }
        });

        endField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                endDateSpinner.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255)));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                endDateSpinner.setBorder(null);
                validateDates();
            }
        });

        backButton.setMnemonic(KeyEvent.VK_B);
        makeBookingButton.setMnemonic(KeyEvent.VK_M);
        standardButton.setMnemonic(KeyEvent.VK_S);
        largeButton.setMnemonic(KeyEvent.VK_L);
        familyButton.setMnemonic(KeyEvent.VK_F);

        backButton.setToolTipText("Go back to manage bookings (Alt+B)");
        makeBookingButton.setToolTipText("Confirm booking details (Alt+M)");
        standardButton.setToolTipText("Select standard room type (Alt+S)");
        largeButton.setToolTipText("Select large room type (Alt+L)");
        familyButton.setToolTipText("Select family room type (Alt+F)");
        startDateSpinner.setToolTipText("<html>Click the up/down arrows to change date<br/>or type directly in yyyy-MM-dd format<br/>You can also click inside to edit manually<br/>Press Enter to move to end date<br/></html>");
        endDateSpinner.setToolTipText("<html>Click the up/down arrows to change date<br/>or type directly in yyyy-MM-dd format<br/>You can also click inside to edit manually<br/>Press Enter to confirm booking<br/></html>");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(standardButton);
        addButtonHoverEffect(largeButton);
        addButtonHoverEffect(familyButton);
        addButtonHoverEffect(makeBookingButton);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().registerKeyboardAction(e -> {
            backButton.doClick();
        }, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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

    private void displayRoomPricing(DefaultTableModel tableModel) {
        
        List<String[]> roomPricing = APUHostelManagement.Resident.getRoomPricing();
        for (String[] row : roomPricing) {
            tableModel.addRow(row);
        }
    }

    private void selectRoomType(String roomType) {
        selectedRoomType = roomType;
        Color selectedColor = new Color(200, 200, 200);
        
        // Reset buttons to their original Material Design colors
        standardButton.setBackground(new Color(187, 222, 251));    // Light Blue
        largeButton.setBackground(new Color(200, 230, 201));      // Light Green
        familyButton.setBackground(new Color(225, 190, 231));     // Light Purple
        
        // Reset all button states
        standardButton.putClientProperty("selected", false);
        largeButton.putClientProperty("selected", false);
        familyButton.putClientProperty("selected", false);
    
        switch (roomType) {
            case "Standard" -> {
                standardButton.putClientProperty("selected", true);
                standardButton.setBackground(selectedColor);
            }
            case "Large" -> {
                largeButton.putClientProperty("selected", true);
                largeButton.setBackground(selectedColor);
            }
            case "Family" -> {
                familyButton.putClientProperty("selected", true);
                familyButton.setBackground(selectedColor);
            }
        }
    }

    private void showBookingDetails() {
        if (selectedRoomType == null) {
            JOptionPane.showMessageDialog(frame, "Please select a room type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        java.util.Date startSpinnerDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endSpinnerDate = (java.util.Date) endDateSpinner.getValue();
    
        LocalDate startDate = startSpinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endSpinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
        if (startDate.isBefore(LocalDate.now())) {
            startDateSpinner.setBorder(BorderFactory.createLineBorder(Color.RED));
            JOptionPane.showMessageDialog(frame, 
                "You cannot travel back in time. Please enter a valid start date.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        startDateSpinner.setBorder(null);
    
        if (!endDate.isAfter(startDate)) {
            endDateSpinner.setBorder(BorderFactory.createLineBorder(Color.RED));
            JOptionPane.showMessageDialog(frame, "The end date must be after the start date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        endDateSpinner.setBorder(null);
    
        String roomID = APUHostelManagement.Resident.selectAvailableRoomByType1(selectedRoomType);
        if (roomID == null) {
            JOptionPane.showMessageDialog(frame, "No available rooms of the selected type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String paymentID = APUHostelManagement.Resident.generatePaymentID1();
        String residentID = resident.getResidentID();
        String feeRateID = APUHostelManagement.Resident.getFeeRateID(roomID);
        double paymentAmount = APUHostelManagement.Resident.calculatePaymentAmount(startDate, endDate, feeRateID);
        String bookingDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        String roomNumber = APUHostelManagement.Resident.getRoomNumber(roomID);
        String username = resident.getUsername();

        String bookingDetails = "<html><b style='font-size:14px;'>Booking Details:</b><br/><br/>" +
                                "Username: " + username + "<br/>" +
                                "Start Date: " + startDate + "<br/>" +
                                "End Date: " + endDate + "<br/>" +
                                "Stay Duration: " + daysBetween + " days<br/>" +
                                "Room Type: " + selectedRoomType + "<br/>" +
                                "Room Number: " + roomNumber + "<br/>" +
                                "Payment Amount: RM " + paymentAmount + "</html>";

        int confirm = JOptionPane.showConfirmDialog(frame, bookingDetails + "\n\nAre you sure you want to confirm this booking?", "Booking Details", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            
            boolean bookingSuccess = APUHostelManagement.Resident.addBookingToFile(paymentID, residentID, startDate, endDate, roomID, paymentAmount, bookingDateTime);
            if (!bookingSuccess) {
                JOptionPane.showMessageDialog(frame, "An error occurred while saving the booking.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            APUHostelManagement.Resident.updateRoomStatus1(roomID, "unavailable");

            
            JOptionPane.showMessageDialog(frame, "Booking successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

            
            resetFields();
        } else {
            
            resetFields();
        }
    }

    private void showDetailedPricing(int row) {
        String roomType = (String) pricingTable.getValueAt(row, 0);
        String capacity = (String) pricingTable.getValueAt(row, 1);
        String dailyRate = (String) pricingTable.getValueAt(row, 2);
        String weeklyRate = (String) pricingTable.getValueAt(row, 3);
        String monthlyRate = (String) pricingTable.getValueAt(row, 4);
        String yearlyRate = (String) pricingTable.getValueAt(row, 5);
    
        String details = String.format("""
            <html>
            <h2>%s Room Details</h2>
            <table>
            <tr><td>Capacity:</td><td>%s</td></tr>
            <tr><td>Daily Rate:</td><td>RM %s</td></tr>
            <tr><td>Weekly Rate:</td><td>RM %s</td></tr>
            <tr><td>Monthly Rate:</td><td>RM %s</td></tr>
            <tr><td>Yearly Rate:</td><td>RM %s</td></tr>
            </table>
            </html>""", 
            roomType, capacity, dailyRate, weeklyRate, monthlyRate, yearlyRate);
    
        JOptionPane.showMessageDialog(frame, details, 
            roomType + " Room Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetFields() {
        selectedRoomType = null;
        startDateSpinner.setValue(new java.util.Date());
        endDateSpinner.setValue(new java.util.Date());
        startDateSpinner.setBorder(null);
        endDateSpinner.setBorder(null);
        
        // Reset all button states
        standardButton.putClientProperty("selected", false);
        largeButton.putClientProperty("selected", false);
        familyButton.putClientProperty("selected", false);
        
        // Reset to original Material Design colors
        standardButton.setBackground(new Color(187, 222, 251));    // Light Blue
        largeButton.setBackground(new Color(200, 230, 201));      // Light Green
        familyButton.setBackground(new Color(225, 190, 231));     // Light Purple
    }

    private void showPricingExplanation() {
        String explanation = "<html><body style='width: 300px; padding: 10px;'>" +
                "<h2>How We Calculate Our Pricing</h2>" +
                "<p>The total payment amount is calculated based on the duration of your stay and the rates for the selected room type. " +
                "The calculation is done as follows:</p>" +
                "<ul>" +
                "<li>Each week is counted as 7 days, Each month is counted as 30 days and Each year is counted as 365 days.</li>" +
                "<li>First, the total number of days between the start and end dates is calculated.</li>" +
                "<li>The number of years, months, weeks, and remaining days are then determined from the total days.</li>" +
                "<li>The payment amount is calculated by multiplying the number of years, months, weeks, and days by their respective rates and summing them up.</li>" +
                "</ul>" +
                "<p>This ensures that you are charged accurately based on the duration of your stay.</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(frame, explanation, "Pricing Explanation", JOptionPane.INFORMATION_MESSAGE);
    }

    private void validateDates() {
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (start.isBefore(LocalDate.now())) {
            startDateSpinner.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        if (!end.isAfter(start)) {
            endDateSpinner.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
    }

    private void addButtonHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color darkerColor = getDarkerColor(originalColor);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Only apply hover effect if button is not selected
                if (button.getClientProperty("selected") == null || 
                    !(boolean)button.getClientProperty("selected")) {
                    button.setBackground(darkerColor);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(button.getForeground(), 2),
                        BorderFactory.createEmptyBorder(3, 13, 3, 13)
                    ));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Only restore original color if button is not selected
                if (button.getClientProperty("selected") == null || 
                    !(boolean)button.getClientProperty("selected")) {
                    button.setBackground(originalColor);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(button.getForeground(), 1),
                        BorderFactory.createEmptyBorder(4, 14, 4, 14)
                    ));
                }
            }
        });
    }
    
    private Color getDarkerColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 1.1f), Math.max(0, hsb[2] - 0.15f));
    }
    
    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setIconTextGap(15);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Enhanced Material Design colors with better contrast
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Standard")) {
                button.setBackground(new Color(187, 222, 251));    // Light Blue
                button.setForeground(new Color(25, 118, 210));    // Dark Blue
            } else if (text.contains("Large")) {
                button.setBackground(new Color(200, 230, 201));    // Light Green
                button.setForeground(new Color(46, 125, 50));     // Dark Green
            } else if (text.contains("Family")) {
                button.setBackground(new Color(225, 190, 231));    // Light Purple
                button.setForeground(new Color(106, 27, 154));    // Dark Purple
            } else if (text.contains("Make")) {
                button.setBackground(new Color(255, 236, 179));    // Light Amber
                button.setForeground(new Color(255, 111, 0));     // Dark Amber
            }
            
            // Add default border matching text color
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
            ));
            
            // Add shadow effect
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 14, 4, 14),
                    BorderFactory.createEmptyBorder(0, 0, 2, 0)
                )
            ));
            
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        return button;
    }
}