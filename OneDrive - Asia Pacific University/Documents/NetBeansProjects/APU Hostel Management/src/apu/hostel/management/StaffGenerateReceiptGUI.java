package apu.hostel.management;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.KeyAdapter;
import java.time.LocalDate; 
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Set;

public class StaffGenerateReceiptGUI {
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

    public StaffGenerateReceiptGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Generate Receipt");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Generate Receipt - " + staff.getUsername());
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

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        sortButton = createButton("Sort", "sort_icon.png");
        JTextField searchField = new JTextField(20);
        searchField.setText("Search receipts...");
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search receipts...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search receipts...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");
        JButton clearButton = createButton("Clear", "clear_icon.png");

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
                    frame.setTitle("Generate Receipt - " + staff.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(new ArrayList<>(paymentDetailsMap.values()));
                    } else {
                        loadEligiblePayments();
                    }
                }
            } else {
                filterPayments();
            }
        });

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
                        loadEligiblePayments();
                    }
                }
            } else {
                sortPayments();
            }
        });

        searchButton.addActionListener(e -> searchPayments(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("Search receipts...");
            searchField.setForeground(Color.GRAY);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadEligiblePayments();
            }
        });

        // View Receipts button
        JButton viewReceiptsButton = createButton("View Receipts", "view_receipt_icon.png");
        viewReceiptsButton.addActionListener(e -> {
            new StaffViewReceiptGUI(staff);
            frame.dispose();
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);

        // Payment table
        tableModel = new DefaultTableModel(
            new Object[]{"Payment ID", "Resident ID", "Staff ID", "Start Date", "End Date", 
                        "Room ID", "Amount (RM)", "Payment Method", "Booking Date"}, 0
        ){
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
        paymentTable.setIntercellSpacing(new Dimension(5, 5));
        paymentTable.setShowGrid(true);
        paymentTable.setFillsViewportHeight(true);

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
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Staff ID
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Date
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(100); // End Date
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Room ID
        paymentTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Amount
        paymentTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Payment Method
        paymentTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Booking Date

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Generate Receipt button
        JButton generateReceiptButton = createButton("Generate Receipt", "generate_receipt_icon.png");
        generateReceiptButton.addActionListener(e -> generateReceipt());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(generateReceiptButton);
        bottomPanel.add(viewReceiptsButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadEligiblePayments();
        frame.setVisible(true);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        backButton.setMnemonic(KeyEvent.VK_B);
        filterButton.setMnemonic(KeyEvent.VK_F);
        sortButton.setMnemonic(KeyEvent.VK_S); 
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        viewReceiptsButton.setMnemonic(KeyEvent.VK_V);
        generateReceiptButton.setMnemonic(KeyEvent.VK_G);

        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter receipts (Alt+F)");
        sortButton.setToolTipText("Sort receipts (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        viewReceiptsButton.setToolTipText("View generated receipts (Alt+V)");
        generateReceiptButton.setToolTipText("Generate receipt for selected payment (Alt+G)");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(viewReceiptsButton);
        addButtonHoverEffect(generateReceiptButton);

        paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    generateReceipt();
                }
            }
        });

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
            if (row == 0) {
                JOptionPane.showMessageDialog(frame, 
                    "No eligible payments found.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
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
        JPanel detailsPanel = new JPanel(new BorderLayout(10,10));
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
        detailsTable.setEnabled(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);
        
        JLabel confirmLabel = new JLabel("Are you sure to generate receipt for this payment?");
        confirmLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        detailsPanel.add(confirmLabel, BorderLayout.SOUTH);

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

    private void filterPayments() {
        String[] filterOptions = {"All", "Payment Method", "Room ID", "Resident ID"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Filter by:", "Filter Receipts", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);
    
        if (filterChoice == null) return;
    
        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            if (currentSortCategory != null) {
                applySorting(new ArrayList<>(paymentDetailsMap.values()));
            } else {
                loadEligiblePayments();
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
                    "Select payment method:", "Filter Receipts",
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
                    "Select Room ID:", "Filter Receipts",
                    JOptionPane.QUESTION_MESSAGE, null, rooms, rooms[0]);
            }
            case "Resident ID" -> {
                Set<String> uniqueResidents = paymentDetailsMap.values().stream()
                    .map(p -> p[1])
                    .collect(Collectors.toSet());
                String[] residents = uniqueResidents.toArray(new String[0]);
                value = (String) JOptionPane.showInputDialog(frame,
                    "Select Resident ID:", "Filter Receipts",
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
            frame.setTitle("Generate Receipt - " + staff.getUsername() + 
                " (Filtered: " + currentFilterValue + ", " + filteredPaymentList.size() + " receipts)");
        }
    
        if (currentSortCategory != null) {
            applySorting(filteredPaymentList);
        } else {
            updateTable(filteredPaymentList);
        }
    }
    
    private void sortPayments() {
        String[] options = {
            "Amount (Low-High)", "Amount (High-Low)", 
            "Start Date (Newest)", "Start Date (Oldest)",
            "Room ID (A-Z)", "Room ID (Z-A)",
            "Resident ID (A-Z)", "Resident ID (Z-A)"
        };
        
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Receipts", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (choice == null) return;
    
        currentSortCategory = choice.split(" ")[0];
        currentSortOrder = choice.contains("High-Low") || 
                          choice.contains("Newest") || 
                          choice.contains("Z-A") ? "Descending" : "Ascending";
        sortButton.setText("Sort: " + currentSortCategory);
    
        List<String[]> listToSort = filteredPaymentList != null ? 
            filteredPaymentList : new ArrayList<>(paymentDetailsMap.values());
        applySorting(listToSort);
    }
    
    private void searchPayments(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || 
            searchQuery.equals("Search receipts...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadEligiblePayments();
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
                payment[9].toLowerCase().contains(lowerCaseQuery)    // Payment Method
            )
            .collect(Collectors.toList());
    
        if (!searchResults.isEmpty()) {
            frame.setTitle("Generate Receipt - " + staff.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("Generate Receipt - " + staff.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No receipts found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadEligiblePayments();
            }
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
                
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
        } else {
            loadEligiblePayments();
        }
    }
    
    private void applySorting(List<String[]> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<String[]> sortedList = new ArrayList<>(listToSort);
        
        Comparator<String[]> comparator = switch (currentSortCategory) {
            case "Amount" -> Comparator.comparing(p -> Double.parseDouble(p[6]));
            case "Start" -> Comparator.comparing(p -> p[3]);
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
    
    private void updateTable(List<String[]> paymentList) {
        tableModel.setRowCount(0);
        for (String[] payment : paymentList) {
            tableModel.addRow(new Object[]{
                payment[0],  // Payment ID
                payment[1],  // Resident ID
                payment[2],  // Staff ID
                payment[3],  // Start Date
                payment[4],  // End Date
                payment[5],  // Room ID
                payment[6],  // Amount 
                payment[9],  // Payment Method
                payment[8]   // Booking Date
            });
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
