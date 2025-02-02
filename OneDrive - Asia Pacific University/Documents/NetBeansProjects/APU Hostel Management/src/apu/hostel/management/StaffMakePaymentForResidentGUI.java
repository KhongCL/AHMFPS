package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class StaffMakePaymentForResidentGUI {
    private JFrame frame;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String[]> paymentDetailsMap;
    private APUHostelManagement.Staff staff;
    private JButton filterButton;
    private JButton sortButton;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private String currentSortCategory = null;
    private String currentSortOrder = null;
    private List<String[]> filteredPaymentList;

    public StaffMakePaymentForResidentGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Make Payment for Resident");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Make Payment for Resident - " + staff.getUsername());
        frame.setLocationRelativeTo(null);

        // Back button panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new StaffMainPageGUI(staff);
            frame.dispose();
        });

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        filterButton.addActionListener(e -> {
            if (filterButton.getText().startsWith("Filter: ")) {
                int choice = JOptionPane.showConfirmDialog(frame,
                    "Do you want to clear the current filter?",
                    "Clear Filter",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    filterButton.setText("Filter");
                    currentFilterChoice = null;
                    currentFilterValue = null;
                    frame.setTitle("Make Payment for Resident - " + staff.getUsername());
                    if (currentSortCategory != null) {
                        // Use the payment details map values instead of paymentList
                        List<String[]> paymentDataList = new ArrayList<>(paymentDetailsMap.values());
                        applySorting(paymentDataList);
                    } else {
                        loadPendingPayments();
                    }
                }
            } else {
                filterPayments();
            }
        });

        sortButton = createButton("Sort", "sort_icon.png");
        sortButton.addActionListener(e -> {
            if (sortButton.getText().startsWith("Sort: ")) {
                int choice = JOptionPane.showConfirmDialog(frame,
                    "Do you want to clear the current sort?",
                    "Clear Sort",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    sortButton.setText("Sort");
                    currentSortCategory = null;
                    currentSortOrder = null;
                    if (currentFilterChoice != null) {
                        reapplyCurrentFilter();
                    } else {
                        loadPendingPayments();
                    }
                }
            } else {
                sortPayments();
            }
        });

        JTextField searchField = new JTextField(20);
        searchField.setText("Search payments...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search payments...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search payments...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");
        JButton clearButton = createButton("Clear", "clear_icon.png");
        searchButton.addActionListener(e -> searchPayments(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("Search payments...");
            searchField.setForeground(Color.GRAY);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPendingPayments();
            }
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        topPanel.add(filterPanel, BorderLayout.EAST);

        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        // Payment table
        tableModel = new DefaultTableModel(
            new Object[]{"Payment ID", "Resident ID", "Room ID", "Start Date", "End Date", 
                        "Stay Duration (Days)", "Amount (RM)", "Payment Method"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.getTableHeader().setReorderingAllowed(false);
        paymentTable.setRowHeight(25);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        paymentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        paymentTable.setSelectionBackground(new Color(230, 240, 250));
        paymentTable.setSelectionForeground(Color.BLACK);
        paymentTable.setGridColor(Color.LIGHT_GRAY);
        paymentTable.setRowHeight(25);
        paymentTable.setIntercellSpacing(new Dimension(5, 5));
        paymentTable.setShowGrid(true);
        paymentTable.setFillsViewportHeight(true);

        // Add zebra striping
        paymentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        paymentTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Payment ID  
        paymentTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Resident ID
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Room ID
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Date
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(100); // End Date
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Stay Duration
        paymentTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Amount
        paymentTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Payment Method

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

        // Add after frame.setVisible(true);
        backButton.setMnemonic(KeyEvent.VK_B);    // Alt+B
        filterButton.setMnemonic(KeyEvent.VK_F);  // Alt+F 
        sortButton.setMnemonic(KeyEvent.VK_S);    // Alt+S
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        makePaymentButton.setMnemonic(KeyEvent.VK_M);   // Alt+M

        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter payments (Alt+F)");
        sortButton.setToolTipText("Sort payments (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        makePaymentButton.setToolTipText("Process payment for selected booking (Alt+M)");

        // Add button hover effects
        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(makePaymentButton);

        paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showPaymentConfirmation();
                }
            }
        });

        // Add Enter key support
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        // Add Escape key support
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
                        payment[5],  // Room ID
                        payment[3],  // Start Date
                        payment[4],  // End Date
                        calculateStayDuration(payment[3], payment[4]), // Stay Duration
                        "RM" + payment[6], // Amount
                        payment[9]   // Payment Method
                    });
                    row++;
                }
            }
            if (row == 0) {
                JOptionPane.showMessageDialog(frame, 
                    "No pending payments found.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
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

    private void filterPayments() {
        String[] filterOptions = {"All", "Payment Method", "Room ID", "Resident ID"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Filter by:", "Filter Payments", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);
    
        if (filterChoice == null) return;
    
        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            // Keep current sort if exists
            if (currentSortCategory != null) {
                applySorting(new ArrayList<>(paymentDetailsMap.values()));
            } else {
                loadPendingPayments();
            }
            return;
        }
    
        // Get filter value based on choice
        String value = null;

        switch (filterChoice) {
            case "Payment Method" -> {
                String[] methods = {"credit_card", "cash", "bank_transfer"};
                String[] displayMethods = {"Credit Card", "Cash", "Bank Transfer"};
                int index = JOptionPane.showOptionDialog(frame,
                    "Select payment method:", "Filter Payments",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, displayMethods, displayMethods[0]);
                if (index >= 0) {
                    value = methods[index];
                }
            }
            case "Room ID" -> {
                Set<String> uniqueRooms = paymentDetailsMap.values().stream()
                    .map(p -> p[5])
                    .collect(Collectors.toSet());
                String[] rooms = uniqueRooms.toArray(new String[0]);
                value = (String) JOptionPane.showInputDialog(frame,
                    "Select Room ID:", "Filter Payments",
                    JOptionPane.QUESTION_MESSAGE, null, rooms, rooms[0]);
            }
            case "Resident ID" -> {
                Set<String> uniqueResidents = paymentDetailsMap.values().stream()
                    .map(p -> p[1])
                    .collect(Collectors.toSet());
                String[] residents = uniqueResidents.toArray(new String[0]);
                value = (String) JOptionPane.showInputDialog(frame,
                    "Select Resident ID:", "Filter Payments",
                    JOptionPane.QUESTION_MESSAGE, null, residents, residents[0]);
            }
        }
    
        if (value == null) return;
    
        currentFilterChoice = filterChoice;
        currentFilterValue = value;
        filterButton.setText("Filter: " + value);
    
        filteredPaymentList = paymentDetailsMap.values().stream()
            .filter(payment -> switch (currentFilterChoice) {
                case "Payment Method" -> payment[9].equalsIgnoreCase(currentFilterValue);
                case "Room ID" -> payment[5].equalsIgnoreCase(currentFilterValue);
                case "Resident ID" -> payment[1].equalsIgnoreCase(currentFilterValue);
                default -> true;
            })
            .collect(Collectors.toList());

        if (!filteredPaymentList.isEmpty()) {
            frame.setTitle("Make Payment for Resident - " + staff.getUsername() + 
                " (Filtered: " + currentFilterValue + ", " + filteredPaymentList.size() + " payments)");
        }

        // Apply current sort if exists
        if (currentSortCategory != null) {
            applySorting(filteredPaymentList);
        } else {
            updateTable(filteredPaymentList);
        }
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            filteredPaymentList = paymentDetailsMap.values().stream()
                .filter(payment -> switch (currentFilterChoice) {
                    case "Payment Method" -> payment[9].equalsIgnoreCase(currentFilterValue);
                    case "Room ID" -> payment[5].equalsIgnoreCase(currentFilterValue);
                    case "Resident ID" -> payment[1].equalsIgnoreCase(currentFilterValue);
                    default -> true;
                })
                .collect(Collectors.toList());
                
            // Apply current sort if exists
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
        } else {
            loadPendingPayments();
        }
    }

    private void sortPayments() {
        String[] options = {
            "Amount (Low-High)", "Amount (High-Low)", 
            "Start Date (Newest)", "Start Date (Oldest)",
            "Stay Duration (Shortest)", "Stay Duration (Longest)",
            "Room ID (A-Z)", "Room ID (Z-A)",
            "Resident ID (A-Z)", "Resident ID (Z-A)"
        };
        
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Payments", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (choice == null) return;
    
        currentSortCategory = choice.split(" ")[0];
        currentSortOrder = choice.contains("High-Low") || choice.contains("Longest") || 
                          choice.contains("Newest") || choice.contains("Z-A") ? "Descending" : "Ascending";
        sortButton.setText("Sort: " + currentSortCategory);
    
        // Sort currently filtered list if exists, otherwise sort all payments
        List<String[]> listToSort = filteredPaymentList != null ? 
            filteredPaymentList : new ArrayList<>(paymentDetailsMap.values());
        applySorting(listToSort);
    }

    private void searchPayments(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || 
            searchQuery.equals("Search payments...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPendingPayments();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
        List<String[]> searchResults = paymentDetailsMap.values().stream()
            .filter(payment -> 
                payment[0].toLowerCase().contains(lowerCaseQuery) || // Payment ID
                payment[1].toLowerCase().contains(lowerCaseQuery) || // Resident ID
                payment[5].toLowerCase().contains(lowerCaseQuery) || // Room ID
                payment[3].toLowerCase().contains(lowerCaseQuery) || // Start Date
                payment[4].toLowerCase().contains(lowerCaseQuery) || // End Date
                payment[6].toLowerCase().contains(lowerCaseQuery) || // Amount
                payment[9].toLowerCase().contains(lowerCaseQuery)  // Payment Method
            )
            .collect(Collectors.toList());
    
        if (!searchResults.isEmpty()) {
            frame.setTitle("Make Payment for Resident - " + staff.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("Make Payment for Resident - " + staff.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No payments found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            // Reload original data
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPendingPayments();
            }
        }
    }

    private void applySorting(List<String[]> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<String[]> sortedList = new ArrayList<>(listToSort);
        
        Comparator<String[]> comparator = switch (currentSortCategory) {
            case "Amount" -> Comparator.comparing(p -> Double.parseDouble(p[6]));
            case "Start" -> Comparator.comparing(p -> p[3]);
            case "Stay" -> Comparator.comparing(p -> calculateStayDuration(p[3], p[4]));
            case "Room" -> Comparator.comparing(p -> p[5]);
            case "Resident" -> Comparator.comparing(p -> p[1]);
            default -> null;
        };
    
        if (comparator != null) {
            if (currentSortOrder.equals("Descending")) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            sortButton.setText("Sort: " + currentSortCategory.split(" ")[0]);
            updateTable(sortedList);
        } else {
            sortButton.setText("Sort");
        }
    }

    private void updateFilteredPayments() {
        List<String[]> filteredPaymentList = paymentDetailsMap.values().stream()
            .filter(payment -> switch (currentFilterChoice) {
                case "Payment Method" -> payment[9].equalsIgnoreCase(currentFilterValue);
                case "Room ID" -> payment[5].equalsIgnoreCase(currentFilterValue);
                case "Resident ID" -> payment[1].equalsIgnoreCase(currentFilterValue);
                default -> true;
            })
            .collect(Collectors.toList());

        if (!filteredPaymentList.isEmpty()) {
            frame.setTitle("Make Payment for Resident - " + staff.getUsername() + 
                " (Filtered: " + currentFilterValue + ", " + filteredPaymentList.size() + " payments)");
        }

        updateTable(filteredPaymentList);
    }

    private void updateTable(List<String[]> paymentList) {
        tableModel.setRowCount(0);
        for (String[] payment : paymentList) {
            tableModel.addRow(new Object[]{
                payment[0],  // Payment ID
                payment[1],  // Resident ID
                payment[5],  // Room ID
                payment[3],  // Start Date
                payment[4],  // End Date
                calculateStayDuration(payment[3], payment[4]),  // Stay Duration
                "RM" + payment[6],  // Amount
                payment[9]   // Payment Method
            });
        }
    }

    private long calculateStayDuration(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ChronoUnit.DAYS.between(start, end);
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