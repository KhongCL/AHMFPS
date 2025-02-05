// KHONG CHEE LEONG TP075846
// JUSTIN NG KEN HONG TP073469

package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StaffViewReceiptGUI {
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

    public StaffViewReceiptGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        this.filteredPaymentList = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("View Receipts");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("View Receipts - " + staff.getUsername());
        frame.setLocationRelativeTo(null);

        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(e -> {
            new StaffGenerateReceiptGUI(staff);
            frame.dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("View Receipts", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        // Create a panel to hold the table and approve button
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        sortButton = createButton("Sort", "sort_icon.png");
        JTextField searchField = new JTextField(20);
        searchField.setText("Search receipts...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
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
                    frame.setTitle("View Receipts - " + staff.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(new ArrayList<>(paymentDetailsMap.values()));
                    } else {
                        loadCompletedPayments();
                    }
                }
            } else {
                filterReceipts();
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
                        loadCompletedPayments();
                    }
                }
            } else {
                sortReceipts();
            }
        });

        searchButton.addActionListener(e -> searchReceipts(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("Search receipts...");
            searchField.setForeground(Color.GRAY);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadCompletedPayments();
            }
        });

        
        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField); 
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(
            new Object[]{"Receipt ID", "Payment ID", "Resident ID", "Staff ID", "Start Date", 
                        "End Date", "Room ID", "Amount (RM)", "Payment Method", "Receipt Date"}, 0
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

        paymentTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(1).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(100); 
        paymentTable.getColumnModel().getColumn(6).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(7).setPreferredWidth(80);  
        paymentTable.getColumnModel().getColumn(8).setPreferredWidth(100); 
        paymentTable.getColumnModel().getColumn(9).setPreferredWidth(100); 

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        
        JButton viewReceiptButton = createButton("View Receipt", "view_receipt_icon.png");
        viewReceiptButton.addActionListener(e -> viewReceipt());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewReceiptButton);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadCompletedPayments();
        frame.setVisible(true);

        searchField.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewReceipt();
                }
            }
        });

        filterButton.setMnemonic(KeyEvent.VK_F);
        sortButton.setMnemonic(KeyEvent.VK_S);
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        backButton.setMnemonic(KeyEvent.VK_B);
        viewReceiptButton.setMnemonic(KeyEvent.VK_V);

        backButton.setToolTipText("Go back to generate receipt page (Alt+B)");
        filterButton.setToolTipText("Filter receipts (Alt+F)");
        sortButton.setToolTipText("Sort receipts (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        viewReceiptButton.setToolTipText("View details of selected receipt (Alt+V)");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(viewReceiptButton);

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

    private void loadCompletedPayments() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);

        filteredPaymentList = null;
        currentFilterChoice = null;
        currentFilterValue = null;
        currentSortCategory = null;
        currentSortOrder = null;
        filterButton.setText("Filter");
        sortButton.setText("Sort");
        
        Map<String, String[]> receiptMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("receipts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] receipt = line.split(",");
                receiptMap.put(receipt[1], receipt); 
            }
        } catch (IOException e) {
            
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

            filteredPaymentList = new ArrayList<>(paymentDetailsMap.values());
            if (row == 0) {
                JOptionPane.showMessageDialog(frame, 
                    "No completed payments found.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
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

        String[] receiptDetails = filteredPaymentList.get(selectedRow);
        
        
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        JLabel headerLabel = new JLabel("APU HOSTEL PAYMENT RECEIPT");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(headerLabel);
        receiptPanel.add(Box.createVerticalStrut(20));
        
        
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
        
        
        JOptionPane.showMessageDialog(frame, receiptPanel, 
            "Receipt Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void filterReceipts() {
        String[] filterOptions = {"All", "Payment Method", "Room ID", "Resident ID"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Filter by:", "Filter Receipts", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);

        if (filterChoice == null) return;

        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("View Receipts - " + staff.getUsername());
            filteredPaymentList = new ArrayList<>(paymentDetailsMap.values());
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
            return;
        }

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
                    .map(p -> p[6])
                    .collect(Collectors.toSet());
                String[] rooms = uniqueRooms.toArray(new String[0]);
                value = (String) JOptionPane.showInputDialog(frame,
                    "Select Room ID:", "Filter Receipts",
                    JOptionPane.QUESTION_MESSAGE, null, rooms, rooms[0]);
            }
            case "Resident ID" -> {
                Set<String> uniqueResidents = paymentDetailsMap.values().stream()
                    .map(p -> p[2])
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
            .filter(receipt -> switch (currentFilterChoice) {
                case "Payment Method" -> receipt[8].equalsIgnoreCase(currentFilterValue);
                case "Room ID" -> receipt[6].equalsIgnoreCase(currentFilterValue);
                case "Resident ID" -> receipt[2].equalsIgnoreCase(currentFilterValue);
                default -> true;
            })
            .collect(Collectors.toList());

        if (!filteredPaymentList.isEmpty()) {
            frame.setTitle("View Receipts - " + staff.getUsername() + 
                " (Filtered: " + currentFilterValue + ", " + filteredPaymentList.size() + " receipts)");
        }

        if (currentSortCategory != null) {
            applySorting(filteredPaymentList);
        } else {
            updateTable(filteredPaymentList);
        }
    }

    private void sortReceipts() {
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

    private void searchReceipts(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || 
            searchQuery.equals("Search receipts...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadCompletedPayments();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase(); 
        List<String[]> searchList = filteredPaymentList != null ?
            filteredPaymentList : new ArrayList<>(paymentDetailsMap.values());
    
        List<String[]> searchResults = searchList.stream()
            .filter(receipt -> 
                receipt[0].toLowerCase().contains(lowerCaseQuery) || 
                receipt[1].toLowerCase().contains(lowerCaseQuery) || 
                receipt[2].toLowerCase().contains(lowerCaseQuery) || 
                receipt[3].toLowerCase().contains(lowerCaseQuery) || 
                receipt[4].toLowerCase().contains(lowerCaseQuery) || 
                receipt[5].toLowerCase().contains(lowerCaseQuery) || 
                receipt[6].toLowerCase().contains(lowerCaseQuery) || 
                receipt[7].toLowerCase().contains(lowerCaseQuery) || 
                receipt[8].toLowerCase().contains(lowerCaseQuery) ||   
                receipt[9].toLowerCase().contains(lowerCaseQuery)    
            )
            .collect(Collectors.toList());
    
        if (!searchResults.isEmpty()) {
            frame.setTitle("View Receipts - " + staff.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("View Receipts - " + staff.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No receipts found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadCompletedPayments(); 
            }
        }
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            filteredPaymentList = paymentDetailsMap.values().stream()
                .filter(receipt -> switch (currentFilterChoice) {
                    case "Payment Method" -> receipt[8].equalsIgnoreCase(currentFilterValue);
                    case "Room ID" -> receipt[6].equalsIgnoreCase(currentFilterValue);
                    case "Resident ID" -> receipt[2].equalsIgnoreCase(currentFilterValue);
                    default -> true;
                })
                .collect(Collectors.toList());
                
            if (currentSortCategory != null) {
                applySorting(filteredPaymentList);
            } else {
                updateTable(filteredPaymentList);
            }
        } else {
            loadCompletedPayments();
        }
    }

    private void applySorting(List<String[]> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<String[]> sortedList = new ArrayList<>(listToSort);
        
        Comparator<String[]> comparator = switch (currentSortCategory) {
            case "Amount" -> Comparator.comparing(p -> Double.parseDouble(p[7]));
            case "Start" -> Comparator.comparing(p -> p[4]);
            case "Room" -> Comparator.comparing(p -> p[6]);
            case "Resident" -> Comparator.comparing(p -> p[2]);
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

    private void updateTable(List<String[]> receiptList) {
        tableModel.setRowCount(0);

        if (receiptList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No receipts found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadCompletedPayments();
            }
            return;
        }
        
        filteredPaymentList = new ArrayList<>(receiptList);
        
        for (String[] receipt : receiptList) {
            tableModel.addRow(new Object[]{
                receipt[0],  
                receipt[1],  
                receipt[2],  
                receipt[3],  
                receipt[4],  
                receipt[5],  
                receipt[6],  
                receipt[7],  
                receipt[8],  
                receipt[9]   
            });
        }
    }

    private void addButtonHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color darkerColor = getDarkerColor(originalColor);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkerColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 2),
                    BorderFactory.createEmptyBorder(3, 13, 3, 13)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
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
            
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Filter") || text.contains("Sort")) {
                button.setBackground(new Color(187, 222, 251));    // Light Blue
                button.setForeground(new Color(25, 118, 210));    // Dark Blue
            } else if (text.contains("Search")) {
                button.setBackground(new Color(225, 190, 231));    // Light Purple
                button.setForeground(new Color(106, 27, 154));    // Dark Purple
            } else if (text.contains("Clear")) {
                button.setBackground(new Color(255, 205, 210));    // Light Red
                button.setForeground(new Color(198, 40, 40));     // Dark Red
            } else if (text.contains("View")) {
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