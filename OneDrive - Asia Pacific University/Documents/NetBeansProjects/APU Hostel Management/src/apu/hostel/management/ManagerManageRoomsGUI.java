package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerManageRoomsGUI {
    private JFrame frame;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.Room> roomList;
    private APUHostelManagement.Manager manager; // Add manager field

    // Add new constructor
    public ManagerManageRoomsGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    public ManagerManageRoomsGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manage Rooms");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Back button
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager); // Pass manager back
                frame.dispose();
            }
        });

        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Room table
        tableModel = new DefaultTableModel(new Object[]{"RoomID", "FeeRateID", "RoomType", "RoomNumber", "RoomStatus", "RoomCapacity", "IsActive"}, 0);
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load rooms into the table
        loadRooms();

        // Action buttons
        JPanel actionPanel = new JPanel(new BorderLayout(5, 5));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // First row panel
        JPanel firstRowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        JButton addButton = createButton("Add Room", "add_icon.png");
        JButton updateStatusButton = createButton("Update Room Status", "update_icon.png");
        JButton updateFeeRateButton = createButton("Update Fee Rate for Room Type", "update_icon.png");
        JButton deleteButton = createButton("Delete Room", "delete_icon.png");

        // Second row panel
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

        firstRowPanel.add(addButton);
        firstRowPanel.add(updateStatusButton);
        firstRowPanel.add(updateFeeRateButton);
        firstRowPanel.add(deleteButton);
        
        secondRowPanel.add(restoreButton);
        secondRowPanel.add(deleteAllButton);
        secondRowPanel.add(restoreAllButton);

        actionPanel.add(firstRowPanel, BorderLayout.NORTH);
        actionPanel.add(secondRowPanel, BorderLayout.CENTER);

        Dimension buttonSize = new Dimension(150, 40);
        addButton.setPreferredSize(buttonSize);
        updateStatusButton.setPreferredSize(buttonSize);
        updateFeeRateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        restoreButton.setPreferredSize(buttonSize);
        deleteAllButton.setPreferredSize(new Dimension(200,40));
        restoreAllButton.setPreferredSize(new Dimension(200,40));

        frame.add(actionPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        
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

    private void loadRooms() {
        tableModel.setRowCount(0); // Clear the table
        roomList = APUHostelManagement.Manager.readRoomsFromFile("rooms.txt");
        for (APUHostelManagement.Room room : roomList) {
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
    
        // Show room details and confirm
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
        loadRooms();
        
        // Ask if user wants to add another room
        int addMore = JOptionPane.showConfirmDialog(frame, 
            "Do you want to add another room?",
            "Add Another Room", 
            JOptionPane.YES_NO_OPTION);
        if (addMore == JOptionPane.YES_OPTION) {
            addRoom(); // Recursive call to add another room
        }
    }

    private void updateRoomStatus() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.Room roomToUpdate = roomList.get(selectedIndex);

        String newStatus = roomToUpdate.getRoomStatus().equals("available") ? "unavailable" : "available";
        roomToUpdate.setRoomStatus(newStatus);

        int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to save the changes?\n" + roomToUpdate, "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        saveRoomsToFile();
        loadRooms(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Room status updated successfully to " + newStatus + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        loadRooms(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Rooms updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRoom() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.Room roomToDelete = roomList.get(selectedIndex);
    
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
        loadRooms(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Room deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreRoom() {
        int selectedIndex = roomTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.Room roomToRestore = roomList.get(selectedIndex);

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
        loadRooms(); // Refresh the table
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
        loadRooms(); // Refresh the table
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
        loadRooms(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "All inactive rooms restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveRoomsToFile() {
        APUHostelManagement.Manager.saveRoomsToFile(roomList);
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
}