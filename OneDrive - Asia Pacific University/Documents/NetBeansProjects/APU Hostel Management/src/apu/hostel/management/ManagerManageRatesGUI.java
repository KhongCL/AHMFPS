package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ManagerManageRatesGUI {
    private JFrame frame;
    private JTable rateTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.FeeRate> rateList;
    private APUHostelManagement.Manager manager; 
    private List<APUHostelManagement.FeeRate> filteredRateList;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private String currentSortCategory = null;
    private String currentSortOrder = null;
    private JButton filterButton;
    private JButton sortButton;

    
    public ManagerManageRatesGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    public ManagerManageRatesGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Fix, Update, Delete or Restore Rate");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Manage Rates - " + manager.getUsername());
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        
        JButton addButton = createButton("Set Initial Rates", "add_icon.png");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInitialRates();
            }
        });

        
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager); 
                frame.dispose();
            }
        });

        JPanel filterSortSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
                    frame.setTitle("Manage Rates - " + manager.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(rateList);
                    } else {
                        loadRates();
                    }
                }
            } else {
                filterRates();
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
                        loadRates();
                    }
                }
            } else {
                sortRates();
            }
        });
        JTextField searchField = new JTextField(20);
        searchField.setText("Search rates...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search rates...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search rates...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");
        searchButton.addActionListener(e -> searchRates(searchField.getText()));
        JButton clearButton = createButton("Clear", "clear_icon.png");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRates();
            }
        });

        filterSortSearchPanel.add(filterButton);
        filterSortSearchPanel.add(sortButton);
        filterSortSearchPanel.add(searchField);
        filterSortSearchPanel.add(searchButton);
        filterSortSearchPanel.add(clearButton);

        topPanel.add(filterSortSearchPanel, BorderLayout.EAST);
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new Object[]{"FeeRateID", "RoomType", "DailyRate (RM)", "WeeklyRate (RM)", "MonthlyRate (RM)", "YearlyRate (RM)", "IsActive"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        rateTable = new JTable(tableModel);
        
        rateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rateTable.getTableHeader().setReorderingAllowed(false);
        rateTable.setRowHeight(25);
        rateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        rateTable.setFont(new Font("Arial", Font.PLAIN, 12)); 
        rateTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rateTable.setSelectionBackground(new Color(230, 240, 250));
        rateTable.setSelectionForeground(Color.BLACK);
        rateTable.setGridColor(Color.LIGHT_GRAY);
        rateTable.setIntercellSpacing(new Dimension(5, 5));
        rateTable.setShowGrid(true);
        rateTable.setFillsViewportHeight(true);

        
        rateTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        rateTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        rateTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        rateTable.getColumnModel().getColumn(2).setPreferredWidth(100); 
        rateTable.getColumnModel().getColumn(3).setPreferredWidth(100); 
        rateTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        rateTable.getColumnModel().getColumn(5).setPreferredWidth(100); 
        rateTable.getColumnModel().getColumn(6).setPreferredWidth(70);  

        rateTable.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateRate();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(rateTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        
        loadRates();

        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = createButton("Update", "update_profile_icon.png");
        JButton deleteButton = createButton("Delete", "delete_icon.png");
        JButton restoreButton = createButton("Restore", "restore_icon.png");
        JButton deleteAllButton = createButton("Delete All", "delete_all_icon.png");
        JButton restoreAllButton = createButton("Restore All", "restore_all_icon.png");

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRate();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRate();
            }
        });

        restoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreRate();
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllRates();
            }
        });

        restoreAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreAllRates();
            }
        });

        actionPanel.add(addButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(restoreButton);
        actionPanel.add(deleteAllButton);
        actionPanel.add(restoreAllButton);

        frame.add(actionPanel, BorderLayout.SOUTH);

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
        addButton.setMnemonic(KeyEvent.VK_I);   
        updateButton.setMnemonic(KeyEvent.VK_U); 
        deleteButton.setMnemonic(KeyEvent.VK_D); 
        restoreButton.setMnemonic(KeyEvent.VK_R); 
        deleteAllButton.setMnemonic(KeyEvent.VK_L); 
        restoreAllButton.setMnemonic(KeyEvent.VK_T); 

        
        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter rates (Alt+F)");
        sortButton.setToolTipText("Sort rates (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        addButton.setToolTipText("Set initial rates (Alt+I)");
        updateButton.setToolTipText("Update selected rate (Alt+U)");
        deleteButton.setToolTipText("Delete selected rate (Alt+D)");
        restoreButton.setToolTipText("Restore selected rate (Alt+R)");
        deleteAllButton.setToolTipText("Delete all rates (Alt+L)");
        restoreAllButton.setToolTipText("Restore all rates (Alt+T)");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(addButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(updateButton);
        addButtonHoverEffect(deleteButton);  
        addButtonHoverEffect(restoreButton);
        addButtonHoverEffect(deleteAllButton);
        addButtonHoverEffect(restoreAllButton);

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

    private void loadRates() {
        tableModel.setRowCount(0); 
        try {
            rateList = APUHostelManagement.FeeRate.readFromFile("fee_rates.txt");
            filteredRateList = new ArrayList<>(rateList);
            for (APUHostelManagement.FeeRate rate : rateList) {
                tableModel.addRow(new Object[]{
                    rate.getFeeRateID(),
                    rate.getRoomType(),
                    rate.getDailyRate(),
                    rate.getWeeklyRate(),
                    rate.getMonthlyRate(),
                    rate.getYearlyRate(),
                    rate.isActive()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading rates.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setInitialRates() {
        String feeRateID = APUHostelManagement.Manager.generateFeeRateID(rateList.size());
        
        String[] roomTypes = {"Standard", "Large", "Family"};
        String roomType = (String) JOptionPane.showInputDialog(frame, "Select Room Type:", "Set Initial Rates", 
            JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
        if (roomType == null) return;
    
        try {
            double dailyRate = APUHostelManagement.Manager.validateRate("Daily Rate", getValidatedRate("Daily Rate", 0));
            double weeklyRate = APUHostelManagement.Manager.validateRate("Weekly Rate", getValidatedRate("Weekly Rate", 0));
            double monthlyRate = APUHostelManagement.Manager.validateRate("Monthly Rate", getValidatedRate("Monthly Rate", 0)); 
            double yearlyRate = APUHostelManagement.Manager.validateRate("Yearly Rate", getValidatedRate("Yearly Rate", 0));
    
            
            String message = String.format("""
                Fee Rate Details:
                Fee Rate ID: %s
                Room Type: %s
                Daily Rate: %.2f
                Weekly Rate: %.2f
                Monthly Rate: %.2f
                Yearly Rate: %.2f
                
                Are you sure you want to add this rate?""",
                feeRateID, roomType, dailyRate, weeklyRate, monthlyRate, yearlyRate);
    
            int confirm = JOptionPane.showConfirmDialog(frame, message, "Confirm Rate Addition", JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(frame, "Rate addition cancelled.");
                return;
            }
    
            rateList.add(new APUHostelManagement.FeeRate(feeRateID, roomType.toLowerCase(), dailyRate, weeklyRate, monthlyRate, yearlyRate, true));
            saveRatesToFile();
            reapplyCurrentFilter();
            
            
            int addMore = JOptionPane.showConfirmDialog(frame, "Do you want to add another rate?","Add Another Rate", JOptionPane.YES_NO_OPTION);
            if (addMore == JOptionPane.YES_OPTION) {
                setInitialRates(); 
            }
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateRate() {
        int selectedRow = rateTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.FeeRate rateToUpdate = filteredRateList.get(selectedRow);
        String[] options = {"Room Type", "Daily Rate", "Weekly Rate", "Monthly Rate", "Yearly Rate"};
        String attributeToUpdate = (String) JOptionPane.showInputDialog(frame, 
            "Select attribute to update:", "Update Rate", 
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (attributeToUpdate == null) return;
    
        List<String> restrictedFeeRateIDs = APUHostelManagement.Manager.getRestrictedFeeRateIDs();
        boolean updated = false;
    
        switch (attributeToUpdate) {
            case "Room Type" -> {
                if (restrictedFeeRateIDs.contains(rateToUpdate.getFeeRateID())) {
                    JOptionPane.showMessageDialog(frame, 
                        "Cannot update room type for fee rate ID: " + rateToUpdate.getFeeRateID() + 
                        " as it is currently being used.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String[] roomTypes = {"Standard", "Large", "Family"};
                String newRoomType = (String) JOptionPane.showInputDialog(frame, 
                    "Select new Room Type:", "Update Room Type", 
                    JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
                if (newRoomType == null) return;
                if (newRoomType.toLowerCase().equals(rateToUpdate.getRoomType())) {
                    showSameValueMessage("Room Type");
                    return;
                }
                rateToUpdate.setRoomType(newRoomType.toLowerCase());
                updated = true;
            }
            case "Daily Rate" -> {
                double newRate = getValidatedRate("Daily Rate", rateToUpdate.getDailyRate());
                if (newRate == -1) return;
                if (newRate == rateToUpdate.getDailyRate()) {
                    showSameValueMessage("Daily Rate");
                    return;
                }
                rateToUpdate.setDailyRate(newRate);
                updated = true;
            }
            case "Weekly Rate" -> {
                double newRate = getValidatedRate("Weekly Rate", rateToUpdate.getWeeklyRate());
                if (newRate == -1) return;
                if (newRate == rateToUpdate.getWeeklyRate()) {
                    showSameValueMessage("Weekly Rate");
                    return;
                }
                rateToUpdate.setWeeklyRate(newRate);
                updated = true;
            }
            case "Monthly Rate" -> {
                double newRate = getValidatedRate("Monthly Rate", rateToUpdate.getMonthlyRate());
                if (newRate == -1) return;
                if (newRate == rateToUpdate.getMonthlyRate()) {
                    showSameValueMessage("Monthly Rate");
                    return;
                }
                rateToUpdate.setMonthlyRate(newRate);
                updated = true;
            }
            case "Yearly Rate" -> {
                double newRate = getValidatedRate("Yearly Rate", rateToUpdate.getYearlyRate());
                if (newRate == -1) return;
                if (newRate == rateToUpdate.getYearlyRate()) {
                    showSameValueMessage("Yearly Rate");
                    return;
                }
                rateToUpdate.setYearlyRate(newRate);
                updated = true;
            }
        }
    
        if (updated) {
            saveRatesToFile();
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "Rate updated successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showSameValueMessage(String fieldName) {
        JOptionPane.showMessageDialog(frame,
            "The " + fieldName + " is the same as the original value.",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRate() {
        int selectedIndex = rateTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.FeeRate rateToDelete = filteredRateList.get(selectedIndex);

        if (!rateToDelete.isActive()) {
            JOptionPane.showMessageDialog(frame, "The selected rate is already deleted.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> restrictedFeeRateIDs = APUHostelManagement.Manager.getRestrictedFeeRateIDs();
        if (restrictedFeeRateIDs.contains(rateToDelete.getFeeRateID())) {
            JOptionPane.showMessageDialog(frame, "Cannot delete fee rate ID: " + rateToDelete.getFeeRateID() + " as it is currently being used.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this rate?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        rateToDelete.setActive(false);

        saveRatesToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "Rate deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreRate() {
        int selectedIndex = rateTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.FeeRate rateToRestore = filteredRateList.get(selectedIndex);

        if (rateToRestore.isActive()) {
            JOptionPane.showMessageDialog(frame, "The selected rate is already restored.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore this rate?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        rateToRestore.setActive(true);

        saveRatesToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "Rate restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAllRates() {
        List<String> restrictedFeeRateIDs = APUHostelManagement.Manager.getRestrictedFeeRateIDs();

        List<APUHostelManagement.FeeRate> deletableRates = new ArrayList<>();
        for (APUHostelManagement.FeeRate rate : rateList) {
            if (!restrictedFeeRateIDs.contains(rate.getFeeRateID())) {
                deletableRates.add(rate);
            }
        }

        if (deletableRates.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No deletable rates available.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete all these rates? This action cannot be undone. You can restore all rates on the menu.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.FeeRate rate : deletableRates) {
            rate.setActive(false);
        }

        saveRatesToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "All rates deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreAllRates() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore all these rates?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.FeeRate rate : rateList) {
            rate.setActive(true);
        }

        saveRatesToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "All rates restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private double getValidatedRate(String rateType, double currentRate) {
        double rate = -1;
        while (rate <= 0) {
            String input = JOptionPane.showInputDialog(frame, 
                "Enter " + rateType + ":", String.valueOf(currentRate));
            if (input == null) {
                return -1;
            }
            if (input.trim().isEmpty()) {
                return -1;
            }
            try {
                rate = Double.parseDouble(input);
                if (rate <= 0) {
                    JOptionPane.showMessageDialog(frame, 
                        rateType + " must be greater than zero. Please enter a valid rate.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                return rate;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, 
                    "Invalid input. Please enter a valid " + rateType + ".", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return rate;
    }

    private void saveRatesToFile() {
        APUHostelManagement.Manager.saveRatesToFile(rateList);
    }

    private void filterRates() {
        String[] options = {"All", "Active", "Deleted", "Room Type"};
        String choice = (String) JOptionPane.showInputDialog(frame,
            "Select filter:", "Filter Rates",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (choice == null) return;
    
        if (choice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("Manage Rates - " + manager.getUsername());
            
            filteredRateList = new ArrayList<>(rateList); // Use existing rates instead of reloading
            
            if (currentSortCategory != null) {
                applySorting(filteredRateList); // Apply current sorting if active
            } else {
                updateTable(filteredRateList); // Just update table if no sorting
            }
            return;
        }

        if (!choice.equals("All")) {
            filterButton.setText("Filter: " + currentFilterValue);
        } else {
            filterButton.setText("Filter");
        }
    
        currentFilterChoice = choice;
        List<APUHostelManagement.FeeRate> filteredRates = new ArrayList<>(rateList);
    
        switch (choice) {
            case "Active":
                currentFilterValue = "Active";
                filteredRates = rateList.stream()
                    .filter(APUHostelManagement.FeeRate::isActive)
                    .collect(Collectors.toList());
                break;
            case "Deleted":
                currentFilterValue = "Deleted";
                filteredRates = rateList.stream()
                    .filter(rate -> !rate.isActive())
                    .collect(Collectors.toList());
                break;
            case "Room Type":
                String[] roomTypes = {"Standard", "Large", "Family"};
                String roomType = (String) JOptionPane.showInputDialog(frame,
                    "Select room type:", "Filter by Room Type",
                    JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
                if (roomType == null) return;
                
                currentFilterValue = roomType;
                filteredRates = rateList.stream()
                    .filter(rate -> rate.getRoomType().equalsIgnoreCase(roomType))
                    .collect(Collectors.toList());
                break;
        }
    
        filterButton.setText("Filter: " + currentFilterValue);
        
        if (!filteredRates.isEmpty()) {
            frame.setTitle("Manage Rates - " + manager.getUsername() + 
                " (Filtered: " + currentFilterValue + ", " + filteredRates.size() + " rates)");
        }
        
        if (currentSortCategory != null) {
            applySorting(filteredRates);
        } else {
            updateTable(filteredRates);
        }
    }
    
    private void sortRates() {
        String[] categories = {
            "Rate ID", 
            "Room Type",
            "Daily Rate",
            "Weekly Rate", 
            "Monthly Rate",
            "Yearly Rate"
        };
    
        String category = (String) JOptionPane.showInputDialog(frame,
            "Select category to sort by:",
            "Sort Rates",
            JOptionPane.QUESTION_MESSAGE,
            null,
            categories,
            categories[0]);
    
        if (category == null) return;


    
        String order = (String) JOptionPane.showInputDialog(frame,
            "Select sort order:",
            "Sort Order", 
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Ascending", "Descending"},
            "Ascending");
    
        if (order == null) return;
        currentSortCategory = category;
        currentSortOrder = order;

        applySorting(filteredRateList != null ? filteredRateList : rateList);
    }

    private void applySorting(List<APUHostelManagement.FeeRate> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<APUHostelManagement.FeeRate> sortedList = new ArrayList<>(listToSort);
        
        Comparator<APUHostelManagement.FeeRate> comparator = switch (currentSortCategory) {
            case "Rate ID" -> Comparator.comparing(APUHostelManagement.FeeRate::getFeeRateID);
            case "Room Type" -> Comparator.comparing(APUHostelManagement.FeeRate::getRoomType);
            case "Daily Rate" -> Comparator.comparing(APUHostelManagement.FeeRate::getDailyRate);
            case "Weekly Rate" -> Comparator.comparing(APUHostelManagement.FeeRate::getWeeklyRate);
            case "Monthly Rate" -> Comparator.comparing(APUHostelManagement.FeeRate::getMonthlyRate);  
            case "Yearly Rate" -> Comparator.comparing(APUHostelManagement.FeeRate::getYearlyRate);
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
    
    private void searchRates(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || searchQuery.equals("Search rates...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRates();
            }
            return;
        }
    
        String lowerQuery = searchQuery.toLowerCase();
        List<APUHostelManagement.FeeRate> searchList = filteredRateList != null ? 
            filteredRateList : new ArrayList<>(rateList);
    
        List<APUHostelManagement.FeeRate> searchResults = searchList.stream()
            .filter(rate -> 
                rate.getFeeRateID().toLowerCase().contains(lowerQuery) ||
                rate.getRoomType().toLowerCase().contains(lowerQuery) ||
                String.valueOf(rate.getDailyRate()).contains(lowerQuery) ||
                String.valueOf(rate.getWeeklyRate()).contains(lowerQuery) ||
                String.valueOf(rate.getMonthlyRate()).contains(lowerQuery) ||
                String.valueOf(rate.getYearlyRate()).contains(lowerQuery) ||
                String.valueOf(rate.isActive()).toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    
        if (!searchResults.isEmpty()) {
            frame.setTitle("Manage Rates - " + manager.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("Manage Rates - " + manager.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No rates found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRates();
            }
        }
    }
    
    private void updateTable(List<APUHostelManagement.FeeRate> rates) {
        tableModel.setRowCount(0);
        
        if (rates.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No rates found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadRates();
            }
            return;
        }
        
        filteredRateList = new ArrayList<>(rates);
        
        for (APUHostelManagement.FeeRate rate : rates) {
            tableModel.addRow(new Object[]{
                rate.getFeeRateID(),
                rate.getRoomType(),
                rate.getDailyRate(),
                rate.getWeeklyRate(),
                rate.getMonthlyRate(),
                rate.getYearlyRate(),
                rate.isActive()
            });
        }
    }
    
    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            List<APUHostelManagement.FeeRate> filteredRates = new ArrayList<>(rateList);
            
            switch (currentFilterChoice) {
                case "Active" -> filteredRates = filteredRates.stream()
                    .filter(APUHostelManagement.FeeRate::isActive)
                    .collect(Collectors.toList());
                case "Deleted" -> filteredRates = filteredRates.stream()
                    .filter(rate -> !rate.isActive())
                    .collect(Collectors.toList());
                case "Room Type" -> filteredRates = filteredRates.stream()
                    .filter(rate -> rate.getRoomType().equalsIgnoreCase(currentFilterValue))
                    .collect(Collectors.toList());
            }
            updateTable(filteredRates);
        } else {
            loadRates();
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
            button.setHorizontalAlignment(SwingConstants.LEFT);
            
            // Enhanced Material Design colors with better contrast
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Filter") || text.contains("Sort")) {
                button.setBackground(new Color(187, 222, 251));    // Light Blue
                button.setForeground(new Color(25, 118, 210));    // Dark Blue
            } else if (text.contains("Search") || text.contains("Update")) {
                button.setBackground(new Color(225, 190, 231));    // Light Purple
                button.setForeground(new Color(106, 27, 154));    // Dark Purple
            } else if (text.contains("Delete")) {
                button.setBackground(new Color(255, 205, 210));    // Light Red
                button.setForeground(new Color(198, 40, 40));     // Dark Red
            } else if (text.contains("Restore")) {
                button.setBackground(new Color(200, 230, 201));    // Light Green
                button.setForeground(new Color(46, 125, 50));     // Dark Green
            } else if (text.contains("Set Initial")) {
                button.setBackground(new Color(255, 236, 179));    // Light Amber
                button.setForeground(new Color(255, 111, 0));     // Dark Amber
            } else if (text.contains("Clear")) {
                button.setBackground(new Color(224, 224, 224));    // Light Gray
                button.setForeground(new Color(97, 97, 97));      // Dark Gray
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