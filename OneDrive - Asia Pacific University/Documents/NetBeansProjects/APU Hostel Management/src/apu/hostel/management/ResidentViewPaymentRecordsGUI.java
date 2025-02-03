package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResidentViewPaymentRecordsGUI {
    private JFrame frame;
    private JPanel viewPaymentRecordsPanel;
    private Map<Integer, String[]> paymentDetailsMap; 
    private APUHostelManagement.Resident resident; 
    private JTable table;
    private JButton filterButton;
    private JButton sortButton;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private String currentSortCategory = null;
    private String currentSortOrder = null;
    private List<String[]> filteredPaymentList;
    private DefaultTableModel tableModel;

    
    public ResidentViewPaymentRecordsGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        this.filteredPaymentList = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("View Payment Records");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("View Payment Records - " + resident.getUsername());
        frame.setLocationRelativeTo(null);
        
        
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("View Payment Records", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        sortButton = createButton("Sort", "sort_icon.png");
        JTextField searchField = new JTextField(20);
        searchField.setText("Search payments...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add focus listener for search field placeholder
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
                    frame.setTitle("View Payment Records - " + resident.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(new ArrayList<>(paymentDetailsMap.values()));
                    } else {
                        loadPaymentRecords();
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
                        loadPaymentRecords();
                    }
                }
            } else {
                sortPayments();
            }
        });

        searchButton.addActionListener(e -> searchPayments(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("Search payments...");
            searchField.setForeground(Color.GRAY);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPaymentRecords();
            }
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton); 
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        frame.add(topPanel, BorderLayout.NORTH);

        viewPaymentRecordsPanel = new JPanel(new BorderLayout(10, 10));
        viewPaymentRecordsPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(viewPaymentRecordsPanel, BorderLayout.CENTER);
        
        tableModel = new DefaultTableModel(
            new Object[]{"Room Number", "Stay Duration", "Booking Date and Time", "Payment Amount (RM)", "Payment Method"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30); 
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionBackground(new Color(230, 240, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        
        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(100); 
        table.getColumnModel().getColumn(2).setPreferredWidth(150); 
        table.getColumnModel().getColumn(3).setPreferredWidth(100); 
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        viewPaymentRecordsPanel.add(scrollPane, BorderLayout.CENTER);

        
        List<String[]> relevantPayments = APUHostelManagement.Resident.viewPaymentRecords(resident.getResidentID());
        if (relevantPayments.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No payment records found for your account.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        paymentDetailsMap = new HashMap<>();
        int rowIndex = 0;
        for (String[] details : relevantPayments) {
            paymentDetailsMap.put(rowIndex, details);
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]); 
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        
        JButton viewButton = createButton("View Payment Details", "view_detail_icon.png");
        viewButton.setPreferredSize(new Dimension(200, 40)); 
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showPaymentDetailsPopup(filteredPaymentList.get(selectedRow));
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Please select a payment record to view.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewButton);
        viewPaymentRecordsPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadPaymentRecords();
        frame.setVisible(true);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !searchField.getText().equals("Search payments...")) {
                    searchButton.doClick();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showPaymentDetailsPopup(filteredPaymentList.get(row));
                    }
                }
            }
        });

        backButton.setMnemonic(KeyEvent.VK_B);
        filterButton.setMnemonic(KeyEvent.VK_F);
        sortButton.setMnemonic(KeyEvent.VK_S);
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        viewButton.setMnemonic(KeyEvent.VK_V);

        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter payments (Alt+F)");
        sortButton.setToolTipText("Sort payments (Alt+S)"); 
        searchButton.setToolTipText("Search payments (Enter)");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        viewButton.setToolTipText("View payment details for selected record (Alt+V)");

        
        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton); 
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(viewButton);

        
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

    private void showPaymentDetailsPopup(String[] details) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a payment record to view.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] paymentDetails = filteredPaymentList.get(selectedRow);
        if (paymentDetails == null) return;
    
        if (details == null) {
            return;
        }
        
        // Rest of the method remains the same
        String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]);
        long stayDuration = calculateStayDuration(details[3], details[4]);
    
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel headerLabel = new JLabel("APU HOSTEL PAYMENT DETAILS");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentPanel.add(headerLabel);
        paymentPanel.add(Box.createVerticalStrut(20));
    
        String[][] detailsArray = {
            {"Payment ID:", details[0]},
            {"Payment Status:", details[7]},
            {"Start Date:", details[3]},
            {"End Date:", details[4]},
            {"Stay Duration:", stayDuration + " days"},
            {"Room Number:", roomNumber},
            {"Payment Amount:", "RM " + details[6]},
            {"Booking Date:", details[8]},
            {"Payment Method:", formatPaymentMethod(details[9])},
            {"Booking Status:", details[10]}
        };
    
        for (String[] detail : detailsArray) {
            JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            detailPanel.add(new JLabel(detail[0]));
            detailPanel.add(new JLabel(detail[1]));
            paymentPanel.add(detailPanel);
        }
    
        JOptionPane.showMessageDialog(frame, paymentPanel, 
            "Payment Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void loadPaymentRecords() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);
        
        List<String[]> relevantPayments = APUHostelManagement.Resident.viewPaymentRecords(resident.getResidentID());
        if (relevantPayments.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No payment records found for your account.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        filteredPaymentList = new ArrayList<>(relevantPayments);  

        currentFilterChoice = null;
        currentFilterValue = null;
        currentSortCategory = null;
        currentSortOrder = null;
        filterButton.setText("Filter");
        sortButton.setText("Sort");      
        
        int rowIndex = 0;
        for (String[] details : relevantPayments) {
            paymentDetailsMap.put(rowIndex, details);
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]);
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            String paymentMethod = details[9] != null && !details[9].equals("null") ? 
                capitalize(details[9].replace("_", " ")) : "-";
                
            tableModel.addRow(new Object[]{
                roomNumber,
                stayDuration + " days", 
                details[8],
                "RM" + details[6],
                paymentMethod
            });
            rowIndex++;
        }
    }

    private void filterPayments() {
        String[] filterOptions = {"All", "Payment Method", "Room Number"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Filter by:", "Filter Payments", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);
    
        if (filterChoice == null) return;
    
        // Create a backup of original data for filtering
        List<String[]> originalData = new ArrayList<>(filteredPaymentList); 
    
        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("View Payment Records - " + resident.getUsername());
            filteredPaymentList = new ArrayList<>(paymentDetailsMap.values());
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
            return;
        }
    
        String value = null;
        String displayValue = null;
    
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
                    displayValue = displayMethods[index];
                }
            }
            case "Room Number" -> {
                Set<String> uniqueRooms = paymentDetailsMap.values().stream()
                    .map(p -> APUHostelManagement.Resident.getRoomNumber(p[5]))
                    .collect(Collectors.toSet());
                String[] rooms = uniqueRooms.toArray(new String[0]);
                value = (String) JOptionPane.showInputDialog(frame,
                    "Select Room Number:", "Filter Payments",
                    JOptionPane.QUESTION_MESSAGE, null, rooms, rooms[0]);
                displayValue = value;
            }
        }
    
        if (value == null) return;
    
        currentFilterChoice = filterChoice;
        currentFilterValue = value;
        filterButton.setText("Filter: " + displayValue);
    
        List<String[]> filtered = originalData.stream()
            .filter(payment -> switch (currentFilterChoice) {
                case "Payment Method" -> payment[9] != null && 
                                    !payment[9].equals("null") && 
                                    payment[9].equalsIgnoreCase(currentFilterValue);
                case "Room Number" -> APUHostelManagement.Resident.getRoomNumber(payment[5])
                                    .equals(currentFilterValue);
                default -> true;
            })
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                "No payments found for selected filter.",
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            updateTable(originalData);
            return;
        }

        frame.setTitle("View Payment Records - " + resident.getUsername() + 
            " (Filtered: " + displayValue + ", " + filtered.size() + " payments)");
        if (currentSortCategory != null) {
            applySorting(filtered);
        } else {
            updateTable(filtered);
        }
    }

    private void sortPayments() {
        String[] options = {
            "Amount (Low-High)", "Amount (High-Low)", 
            "Stay Duration (Shortest)", "Stay Duration (Longest)",
            "Booking Date (Newest)", "Booking Date (Oldest)"
        };
        
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Payments", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null) return;

        currentSortCategory = choice.split(" ")[0];
        currentSortOrder = choice.contains("High-Low") || 
                        choice.contains("Longest") || 
                        choice.contains("Newest") ? "Descending" : "Ascending";
        sortButton.setText("Sort: " + currentSortCategory);

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
                loadPaymentRecords();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
        List<String[]> searchList = filteredPaymentList != null ? 
            filteredPaymentList : new ArrayList<>(paymentDetailsMap.values());
    
        List<String[]> searchResults = searchList.stream()
            .filter(payment -> {
                long stayDuration = calculateStayDuration(payment[3], payment[4]);
                String stayDurationStr = String.valueOf(stayDuration);
                
                String roomNumber = APUHostelManagement.Resident.getRoomNumber(payment[5]);
                
                return roomNumber.toLowerCase().contains(lowerCaseQuery) ||
                    stayDurationStr.toLowerCase().contains(lowerCaseQuery) ||
                    payment[8].toLowerCase().contains(lowerCaseQuery) || // Booking Date
                    payment[6].toLowerCase().contains(lowerCaseQuery) || // Amount
                    payment[9].toLowerCase().contains(lowerCaseQuery);   // Payment Method
            })
            .collect(Collectors.toList());
    
        if (!searchResults.isEmpty()) {
            frame.setTitle("View Payment Records - " + resident.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("View Payment Records - " + resident.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No payments found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPaymentRecords();
            }
        }
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            filteredPaymentList = paymentDetailsMap.values().stream()
                .filter(payment -> switch (currentFilterChoice) {
                    case "Payment Method" -> payment[9] != null && 
                                        !payment[9].equals("null") && 
                                        payment[9].equalsIgnoreCase(currentFilterValue);
                    case "Room Number" -> APUHostelManagement.Resident.getRoomNumber(payment[5])
                                        .equals(currentFilterValue);
                    default -> true;
                })
                .collect(Collectors.toList());
                
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
        } else {
            if (currentSortCategory != null) {
                applySorting(new ArrayList<>(paymentDetailsMap.values()));
            } else {
                loadPaymentRecords();
            }
        }
    }

    private void applySorting(List<String[]> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<String[]> sortedList = new ArrayList<>(listToSort);
        
        Comparator<String[]> comparator = switch (currentSortCategory) {
            case "Amount" -> Comparator.comparing(p -> Double.parseDouble(p[6]));
            case "Stay" -> Comparator.comparing(p -> calculateStayDuration(p[3], p[4]));
            case "Booking" -> Comparator.comparing(p -> p[8]);
            default -> null;
        };
    
        if (comparator != null) {
            if (currentSortOrder.equals("Descending")) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            updateTable(sortedList);
        }
    }

    private void updateTable(List<String[]> paymentList) {
        tableModel.setRowCount(0);
            
        if (paymentList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No payments found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadPaymentRecords();
            }
            return;
        }
        
        filteredPaymentList = new ArrayList<>(paymentList);
        
        for (String[] payment : paymentList) {
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(payment[5]);
            long stayDuration = calculateStayDuration(payment[3], payment[4]);
            
            tableModel.addRow(new Object[]{
                roomNumber,
                stayDuration + " days",
                payment[8],
                "RM" + payment[6],
                formatPaymentMethod(payment[9])
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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String formatPaymentMethod(String method) {
        if (method == null || method.equals("null")) {
            return "-";
        }
        return capitalize(method.replace("_", " "));
    }
}