package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagerManageRatesGUI {
    private JFrame frame;
    private JTable rateTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.FeeRate> rateList;
    private APUHostelManagement.Manager manager; // Add manager field

    // Add new constructor
    public ManagerManageRatesGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    public ManagerManageRatesGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Fix, Update, Delete or Restore Rate");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add button
        JButton addButton = new JButton("Set Initial Rates");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInitialRates();
            }
        });

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager); // Pass manager back
                frame.dispose();
            }
        });

        topPanel.add(addButton, BorderLayout.EAST);
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Rate table
        tableModel = new DefaultTableModel(new Object[]{"FeeRateID", "RoomType", "DailyRate", "WeeklyRate", "MonthlyRate", "YearlyRate", "IsActive"}, 0);
        rateTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rateTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load rates into the table
        loadRates();

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton restoreButton = new JButton("Restore");
        JButton deleteAllButton = new JButton("Delete All");
        JButton restoreAllButton = new JButton("Restore All");

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

        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(restoreButton);
        actionPanel.add(deleteAllButton);
        actionPanel.add(restoreAllButton);

        frame.add(actionPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void loadRates() {
        tableModel.setRowCount(0); // Clear the table
        try {
            rateList = APUHostelManagement.FeeRate.readFromFile("fee_rates.txt");
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
        String feeRateID = "FR" + String.format("%02d", rateList.size() + 1);
    
        String[] roomTypes = {"Standard", "Large", "Family"};
        String roomType = (String) JOptionPane.showInputDialog(frame, "Select Room Type:", "Set Initial Rates", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
        if (roomType == null) return;
    
        double dailyRate = getValidatedRate("Daily Rate", 0);
        double weeklyRate = getValidatedRate("Weekly Rate", 0);
        double monthlyRate = getValidatedRate("Monthly Rate", 0);
        double yearlyRate = getValidatedRate("Yearly Rate", 0);
    
        rateList.add(new APUHostelManagement.FeeRate(feeRateID, roomType.toLowerCase(), dailyRate, weeklyRate, monthlyRate, yearlyRate, true));
        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Rate added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateRate() {
        int selectedIndex = rateTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.FeeRate rateToUpdate = rateList.get(selectedIndex);
    
        String[] options = {"Room Type", "Daily Rate", "Weekly Rate", "Monthly Rate", "Yearly Rate"};
        String attributeToUpdate = (String) JOptionPane.showInputDialog(frame, "Select attribute to update:", "Update Rate", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (attributeToUpdate == null) {
            return; // User cancelled
        }
    
        List<String> restrictedFeeRateIDs = APUHostelManagement.Manager.getRestrictedFeeRateIDs();
    
        switch (attributeToUpdate) {
            case "Room Type":
                if (restrictedFeeRateIDs.contains(rateToUpdate.getFeeRateID())) {
                    JOptionPane.showMessageDialog(frame, "Cannot update room type for fee rate ID: " + rateToUpdate.getFeeRateID() + " as it exists in rooms.txt.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String[] roomTypes = {"Standard", "Large", "Family"};
                String newRoomType = (String) JOptionPane.showInputDialog(frame, "Select new Room Type:", "Update Room Type", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
                if (newRoomType == null) return;
                rateToUpdate.setRoomType(newRoomType.toLowerCase());
                break;
            case "Daily Rate":
                double newDailyRate = getValidatedRate("Daily Rate", rateToUpdate.getDailyRate());
                if (newDailyRate != -1) {
                    rateToUpdate.setDailyRate(newDailyRate);
                }
                break;
            case "Weekly Rate":
                double newWeeklyRate = getValidatedRate("Weekly Rate", rateToUpdate.getWeeklyRate());
                if (newWeeklyRate != -1) {
                    rateToUpdate.setWeeklyRate(newWeeklyRate);
                }
                break;
            case "Monthly Rate":
                double newMonthlyRate = getValidatedRate("Monthly Rate", rateToUpdate.getMonthlyRate());
                if (newMonthlyRate != -1) {
                    rateToUpdate.setMonthlyRate(newMonthlyRate);
                }
                break;
            case "Yearly Rate":
                double newYearlyRate = getValidatedRate("Yearly Rate", rateToUpdate.getYearlyRate());
                if (newYearlyRate != -1) {
                    rateToUpdate.setYearlyRate(newYearlyRate);
                }
                break;
        }
    
        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Rate updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRate() {
        int selectedIndex = rateTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.FeeRate rateToDelete = rateList.get(selectedIndex);

        if (!rateToDelete.isActive()) {
            JOptionPane.showMessageDialog(frame, "The selected rate is already deleted.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> restrictedFeeRateIDs = APUHostelManagement.Manager.getRestrictedFeeRateIDs();
        if (restrictedFeeRateIDs.contains(rateToDelete.getFeeRateID())) {
            JOptionPane.showMessageDialog(frame, "Cannot delete fee rate ID: " + rateToDelete.getFeeRateID() + " as it exists in rooms.txt.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this rate?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        rateToDelete.setActive(false);

        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Rate deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreRate() {
        int selectedIndex = rateTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a rate to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.FeeRate rateToRestore = rateList.get(selectedIndex);

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
        loadRates(); // Refresh the table
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
        loadRates(); // Refresh the table
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
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "All rates restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private double getValidatedRate(String rateType, double currentRate) {
        double rate = -1;
        while (rate <= 0) {
            String input = JOptionPane.showInputDialog(frame, "Enter " + rateType + ":", currentRate);
            if (input == null) {
                return -1; // User cancelled
            }
            try {
                rate = Double.parseDouble(input);
                if (rate <= 0) {
                    JOptionPane.showMessageDialog(frame, rateType + " must be greater than zero. Please enter a valid rate.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid " + rateType + ".", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return rate;
    }

    private void saveRatesToFile() {
        APUHostelManagement.Manager.saveRatesToFile(rateList);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ManagerManageRatesGUI window = new ManagerManageRatesGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}