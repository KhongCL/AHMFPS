// KHONG CHEE LEONG TP075846
// JUSTIN NG KEN HONG TP073469

package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerManageRoomsGUI {
    private JFrame frame;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.Room> roomList;
    private APUHostelManagement.Manager manager; 
    private List<APUHostelManagement.Room> filteredRoomList;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private JButton filterButton;
    private JButton sortButton;
    private String currentSortCategory = null;
    private String currentSortOrder = null;

    
    public ManagerManageRoomsGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        this.roomList = new ArrayList<>();
        this.filteredRoomList = new ArrayList<>(); 
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manage Rooms");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Manage Rooms - " + manager.getUsername());
        frame.setLocationRelativeTo(null);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 

        
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager); 
                frame.dispose();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Manage Rooms", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        // Create a panel to hold the table and approve button
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

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
                    frame.setTitle("Manage Rooms - " + manager.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(roomList);
                    } else {
                        loadRooms();
                    }
                }
            } else {
                filterRooms();
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
                        loadRooms();
                    }
                }
            } else {
                sortRooms();
            }
        });

        JTextField searchField = new JTextField(20);
        searchField.setText("Search rooms...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search rooms...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search rooms...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");
        searchButton.addActionListener(e -> searchRooms(searchField.getText()));

        JButton clearButton = createButton("Clear", "clear_icon.png");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRooms();
            }
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        topPanel.add(backButton, BorderLayout.WEST);
        contentPanel.add(filterPanel, BorderLayout.NORTH);

        frame.add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new Object[]{"RoomID", "FeeRateID", "RoomType", "RoomNumber", "RoomStatus", "RoomCapacity", "IsActive"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setReorderingAllowed(false);
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        roomTable.setFont(new Font("Arial", Font.PLAIN, 12));
        roomTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        roomTable.setSelectionBackground(new Color(230, 240, 250));
        roomTable.setSelectionForeground(Color.BLACK);
        roomTable.setGridColor(Color.LIGHT_GRAY);
        roomTable.setIntercellSpacing(new Dimension(5, 5));
        roomTable.setShowGrid(true);
        roomTable.setFillsViewportHeight(true);

        roomTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        roomTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(100); 
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(80);  
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(80);  
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(70);  

        JScrollPane scrollPane = new JScrollPane(roomTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        
        loadRooms();

        
        JPanel actionPanel = new JPanel(new BorderLayout(5, 5));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        
        JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton addButton = createButton("Add Room", "add_icon.png");
        JButton updateStatusButton = createButton("Update Room Status", "update_icon.png");
        JButton updateFeeRateButton = createButton("Update Fee Rate for Room Type", "update_icon.png");
        JButton deleteButton = createButton("Delete Room", "delete_icon.png");

        
        JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton restoreButton = createButton("Restore Room", "restore_icon.png");
        JButton deleteAllButton = createButton("Delete All Room", "delete_all_icon.png");
        JButton restoreAllButton = createButton("Restore All Room", "restore_all_icon.png");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRoom();
            }
        });

        updateStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRoomStatus();
            }
        });

        updateFeeRateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRoomType();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRoom();
            }
        });

        restoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreRoom();
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllRooms();
            }
        });

        restoreAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreAllRooms();
            }
        });

        roomTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateRoomStatus(); 
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        firstRowPanel.add(addButton);
        firstRowPanel.add(updateStatusButton);
        firstRowPanel.add(updateFeeRateButton);
        firstRowPanel.add(deleteButton);
        
        secondRowPanel.add(restoreButton);
        secondRowPanel.add(deleteAllButton);
        secondRowPanel.add(restoreAllButton);

        actionPanel.add(firstRowPanel, BorderLayout.NORTH);
        actionPanel.add(secondRowPanel, BorderLayout.CENTER);

        Dimension buttonSize = new Dimension(175, 40);
        addButton.setPreferredSize(buttonSize);
        updateStatusButton.setPreferredSize(new Dimension(225, 40));
        updateFeeRateButton.setPreferredSize(new Dimension(300, 40));
        deleteButton.setPreferredSize(buttonSize);
        restoreButton.setPreferredSize(new Dimension(200,40));
        deleteAllButton.setPreferredSize(new Dimension(200,40));
        restoreAllButton.setPreferredSize(new Dimension(200,40));

        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        backButton.setMnemonic(KeyEvent.VK_B);
        filterButton.setMnemonic(KeyEvent.VK_F);  
        sortButton.setMnemonic(KeyEvent.VK_S);    
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        addButton.setMnemonic(KeyEvent.VK_A);
        updateStatusButton.setMnemonic(KeyEvent.VK_U);
        updateFeeRateButton.setMnemonic(KeyEvent.VK_F);
        deleteButton.setMnemonic(KeyEvent.VK_D);
        restoreButton.setMnemonic(KeyEvent.VK_R);
        deleteAllButton.setMnemonic(KeyEvent.VK_L);
        restoreAllButton.setMnemonic(KeyEvent.VK_T);

        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter rooms (Alt+F)");
        sortButton.setToolTipText("Sort rooms (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        addButton.setToolTipText("Add new room (Alt+A)");
        updateStatusButton.setToolTipText("Update room status (Alt+U)");
        updateFeeRateButton.setToolTipText("Update fee rate (Alt+F)");
        deleteButton.setToolTipText("Delete selected room (Alt+D)");
        restoreButton.setToolTipText("Restore selected room (Alt+R)");
        deleteAllButton.setToolTipText("Delete all available rooms (Alt+L)");
        restoreAllButton.setToolTipText("Restore all rooms (Alt+T)");

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(addButton);
        addButtonHoverEffect(updateStatusButton);
        addButtonHoverEffect(updateFeeRateButton);
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

    private void loadRooms() {
        roomList = APUHostelManagement.Manager.readRoomsFromFile("rooms.txt");
        updateTable(roomList);
    }

    private void addRoom() {
        List<APUHostelManagement.FeeRate> feeRates;
        try {
            feeRates = APUHostelManagement.Manager.readRatesFromFile("fee_rates.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading fee rates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String roomId = "RM" + String.format("%02d", roomList.size() + 1);
        int roomNumber = 101 + roomList.size();
    
        String[] roomTypes = feeRates.stream()
                .map(APUHostelManagement.FeeRate::getRoomType)
                .distinct()
                .toArray(String[]::new);
    
        String selectedRoomType = (String) JOptionPane.showInputDialog(frame, "Select Room Type:", "Add Room", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
        if (selectedRoomType == null) return;
    
        APUHostelManagement.FeeRate selectedFeeRate = feeRates.stream()
                .filter(rate -> rate.getRoomType().equalsIgnoreCase(selectedRoomType))
                .findFirst()
                .orElse(null);
    
        if (selectedFeeRate == null) {
            JOptionPane.showMessageDialog(frame, "No fee rate found for the selected room type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int roomCapacity;
        switch (selectedRoomType.toLowerCase()) {
            case "standard" -> roomCapacity = 1;
            case "large" -> roomCapacity = 3;
            case "family" -> roomCapacity = 6;
            default -> {
                JOptionPane.showMessageDialog(frame, "Invalid room type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
        APUHostelManagement.Room newRoom = new APUHostelManagement.Room(roomId, selectedFeeRate.getFeeRateID(), selectedRoomType, roomNumber, "available", roomCapacity, true);
    
        
        String message = String.format("""
            Room Details:
            Room ID: %s
            Fee Rate ID: %s
            Room Type: %s
            Room Number: %d
            Room Status: %s
            Room Capacity: %d
            
            Do you want to add this room?""",
            newRoom.getRoomID(), newRoom.getFeeRateID(), newRoom.getRoomType(),
            newRoom.getRoomNumber(), newRoom.getRoomStatus(), newRoom.getRoomCapacity());
    
        int confirm = JOptionPane.showConfirmDialog(frame, message, "Confirm Addition", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(frame, "Room addition cancelled.");
            return;
        }
    
        roomList.add(newRoom);
        saveRoomsToFile();
        reapplyCurrentFilter();
        
        
        int addMore = JOptionPane.showConfirmDialog(frame, 
            "Do you want to add another room?",
            "Add Another Room", 
            JOptionPane.YES_NO_OPTION);
        if (addMore == JOptionPane.YES_OPTION) {
            addRoom(); 
        }
    }

    private void updateRoomStatus() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.Room roomToUpdate = filteredRoomList.get(selectedIndex); // Changed from roomList
    
        String newStatus = roomToUpdate.getRoomStatus().equals("available") ? "unavailable" : "available";
        String oldStatus = roomToUpdate.getRoomStatus();
        
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Do you want to update the selected room's status from " + oldStatus + " to " + newStatus + "?\n" + roomToUpdate, 
            "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        roomToUpdate.setRoomStatus(newStatus);
        saveRoomsToFile();
        reapplyCurrentFilter(); // Changed from loadRooms()
        JOptionPane.showMessageDialog(frame, "Room status updated successfully to " + newStatus + ".", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateRoomType() {
        List<APUHostelManagement.FeeRate> feeRates;
        try {
            feeRates = APUHostelManagement.Manager.readRatesFromFile("fee_rates.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading fee rates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] roomTypes = feeRates.stream()
                .map(APUHostelManagement.FeeRate::getRoomType)
                .distinct()
                .toArray(String[]::new);

        String selectedRoomType = (String) JOptionPane.showInputDialog(frame, "Select Room Type to update:", "Update Room Type", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
        if (selectedRoomType == null) return;

        String currentFeeRateID = roomList.stream()
                .filter(room -> room.getRoomType().equalsIgnoreCase(selectedRoomType))
                .map(APUHostelManagement.Room::getFeeRateID)
                .findFirst()
                .orElse(null);

        List<APUHostelManagement.FeeRate> selectedFeeRates = feeRates.stream()
                .filter(rate -> rate.getRoomType().equalsIgnoreCase(selectedRoomType) && !rate.getFeeRateID().equals(currentFeeRateID) && rate.getIsActive())
                .collect(Collectors.toList());

        if (selectedFeeRates.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No available fee rates for this room type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        APUHostelManagement.FeeRate selectedFeeRate = (APUHostelManagement.FeeRate) JOptionPane.showInputDialog(frame, "Select new Fee Rate ID:", "Update Room Type", JOptionPane.QUESTION_MESSAGE, null, selectedFeeRates.toArray(), selectedFeeRates.get(0));
        if (selectedFeeRate == null) return;

        for (APUHostelManagement.Room room : roomList) {
            if (room.getRoomType().equalsIgnoreCase(selectedRoomType)) {
                room.setFeeRateID(selectedFeeRate.getFeeRateID());
            }
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to save the changes?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        saveRoomsToFile();
        reapplyCurrentFilter();  
        JOptionPane.showMessageDialog(frame, "Rooms updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRoom() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.Room roomToDelete = filteredRoomList.get(selectedIndex); // Changed from roomList
    
        if (!roomToDelete.isActive()) {
            JOptionPane.showMessageDialog(frame, "The selected room is already deleted.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        if (!roomToDelete.getRoomStatus().equalsIgnoreCase("available")) {
            JOptionPane.showMessageDialog(frame, "Cannot delete a room with unavailable status.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this room?\n" + roomToDelete, "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        roomToDelete.setActive(false);
        saveRoomsToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "Room deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void restoreRoom() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.Room roomToRestore = filteredRoomList.get(selectedIndex); // Changed from roomList
    
        if (roomToRestore.isActive()) {
            JOptionPane.showMessageDialog(frame, "The selected room is already active.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore this room?\n" + roomToRestore, "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        roomToRestore.setActive(true);
        saveRoomsToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "Room restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAllRooms() {
        List<APUHostelManagement.Room> deletableRooms = roomList.stream()
                .filter(room -> room.isActive() && room.getRoomStatus().equalsIgnoreCase("available"))
                .collect(Collectors.toList());

        if (deletableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active and available rooms to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete all active and available rooms? This action cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.Room room : deletableRooms) {
            room.setActive(false);
        }

        saveRoomsToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "All active and available rooms deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreAllRooms() {
        List<APUHostelManagement.Room> inactiveRooms = roomList.stream()
                .filter(room -> !room.isActive())
                .collect(Collectors.toList());

        if (inactiveRooms.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No inactive rooms to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore all inactive rooms?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.Room room : inactiveRooms) {
            room.setActive(true);
        }

        saveRoomsToFile();
        reapplyCurrentFilter();
        JOptionPane.showMessageDialog(frame, "All inactive rooms restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveRoomsToFile() {
        APUHostelManagement.Manager.saveRoomsToFile(roomList);
    }

    private void filterRooms() {
        String[] filterOptions = {"All", "Room Type", "Room Status", "Active Status"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Select filter category:", "Filter Rooms",
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);

        if (filterChoice == null) return;

        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("Manage Rooms - " + manager.getUsername());
            
            filteredRoomList = new ArrayList<>(roomList); // Use existing rates
            
            if (currentSortCategory != null) {
                applySorting(filteredRoomList); // Apply current sorting if active
            } else {
                updateTable(filteredRoomList); // Just update table if no sorting
            }
            return;
        }

        String[] values = switch (filterChoice) {
            case "Room Type" -> new String[]{"Standard", "Large", "Family"};
            case "Room Status" -> new String[]{"Available", "Unavailable"};
            case "Active Status" -> new String[]{"Active", "Inactive"};
            default -> null;
        };

        if (values == null) return;

        String value = (String) JOptionPane.showInputDialog(frame,
            "Select " + filterChoice + ":", "Filter Rooms",
            JOptionPane.QUESTION_MESSAGE, null, values, values[0]);

        if (value == null) return;

        currentFilterChoice = filterChoice;
        currentFilterValue = value;

        List<APUHostelManagement.Room> filteredRooms = roomList.stream()
            .filter(room -> switch (filterChoice) {
                case "Room Type" -> room.getRoomType().equalsIgnoreCase(value);
                case "Room Status" -> room.getRoomStatus().equalsIgnoreCase(value);
                case "Active Status" -> room.isActive() == value.equalsIgnoreCase("Active");
                default -> true;
            })
            .collect(Collectors.toList());

        filterButton.setText("Filter: " + value);
        
        if (!filteredRooms.isEmpty()) {
            frame.setTitle("Manage Rooms - " + manager.getUsername() + 
                " (Filtered: " + value + ", " + filteredRooms.size() + " rooms)");
        }

        if (currentSortCategory != null) {
            applySorting(filteredRooms);
        } else {
            updateTable(filteredRooms);
        }
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            List<APUHostelManagement.Room> filteredRooms = roomList.stream()
                .filter(room -> switch (currentFilterChoice) {
                    case "Room Type" -> room.getRoomType().equalsIgnoreCase(currentFilterValue);
                    case "Room Status" -> room.getRoomStatus().equalsIgnoreCase(currentFilterValue);
                    case "Active Status" -> room.isActive() == currentFilterValue.equalsIgnoreCase("Active");
                    default -> true;
                })
                .collect(Collectors.toList());
            updateTable(filteredRooms);
        } else {
            loadRooms();
        }
    }

    private void searchRooms(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || 
            searchQuery.equals("Search rooms...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRooms();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
        List<APUHostelManagement.Room> searchList = filteredRoomList != null ? 
            filteredRoomList : new ArrayList<>(roomList);
    
        List<APUHostelManagement.Room> searchedRooms = searchList.stream()
            .filter(room -> room.getRoomID().toLowerCase().contains(lowerCaseQuery) ||
                        room.getFeeRateID().toLowerCase().contains(lowerCaseQuery) ||
                        room.getRoomType().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(room.getRoomNumber()).contains(lowerCaseQuery) ||
                        room.getRoomStatus().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(room.getRoomCapacity()).contains(lowerCaseQuery) ||
                        String.valueOf(room.isActive()).toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());
    
        if (!searchedRooms.isEmpty()) {
            frame.setTitle("Manage Rooms - " + manager.getUsername() + 
                " (Found " + searchedRooms.size() + " results)");
            updateTable(searchedRooms);
        } else {
            frame.setTitle("Manage Rooms - " + manager.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No rooms found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadRooms();
            }
        }
    }

    private void sortRooms() {
        String[] options = {"Room Number (Ascending)", "Room Number (Descending)", 
                        "Room Type A-Z", "Room Type Z-A"};
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Rooms", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null) return;

        currentSortCategory = choice.split(" ")[0] + " " + choice.split(" ")[1]; 
        currentSortOrder = choice.contains("Descending") || choice.contains("Z-A") ? 
                        "Descending" : "Ascending";

        applySorting(filteredRoomList != null ? filteredRoomList : roomList);
    }

    private void applySorting(List<APUHostelManagement.Room> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;

        List<APUHostelManagement.Room> sortedList = new ArrayList<>(listToSort);

        Comparator<APUHostelManagement.Room> comparator = switch (currentSortCategory) {
            case "Room Number" -> Comparator.comparing(APUHostelManagement.Room::getRoomNumber);
            case "Room Type" -> Comparator.comparing(APUHostelManagement.Room::getRoomType);
            default -> null;
        };

        if (comparator != null) {
            if (currentSortOrder.equals("Descending")) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            sortButton.setText("Sort: " + currentSortCategory);
            updateTable(sortedList);
        } else {
            sortButton.setText("Sort");
        }
    }

    private void updateTable(List<APUHostelManagement.Room> rooms) {
        tableModel.setRowCount(0);
        
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No rooms found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadRooms();
            }
            return;
        }
        
        
        filteredRoomList = new ArrayList<>(rooms);
        
        for (APUHostelManagement.Room room : rooms) {
            tableModel.addRow(new Object[]{
                room.getRoomID(),
                room.getFeeRateID(),
                room.getRoomType(),
                room.getRoomNumber(),
                room.getRoomStatus(),
                room.getRoomCapacity(),
                room.isActive()
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
            } else if (text.contains("Add")) {
                button.setBackground(new Color(255, 236, 179));    // Light Amber
                button.setForeground(new Color(255, 111, 0));     // Dark Amber
            } else if (text.contains("Clear")) {
                button.setBackground(new Color(255, 205, 210)); // Light Red
                button.setForeground(new Color(198, 40, 40));     // Dark Gray
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