package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ManagerFixUpdateDeleteRestoreRateGUI {
    private JFrame frame;
    private JTable rateTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.FeeRate> rateList;

    public ManagerFixUpdateDeleteRestoreRateGUI() {
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
                new ManagerMainPageGUI();
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

        double dailyRate = getValidatedRate("Daily Rate");
        double weeklyRate = getValidatedRate("Weekly Rate");
        double monthlyRate = getValidatedRate("Monthly Rate");
        double yearlyRate = getValidatedRate("Yearly Rate");

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

        switch (attributeToUpdate) {
            case "Room Type":
                String[] roomTypes = {"Standard", "Large", "Family"};
                String newRoomType = (String) JOptionPane.showInputDialog(frame, "Select new Room Type:", "Update Room Type", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
                if (newRoomType == null) return;
                rateToUpdate.setRoomType(newRoomType.toLowerCase());
                break;
            case "Daily Rate":
                rateToUpdate.setDailyRate(getValidatedRate("Daily Rate"));
                break;
            case "Weekly Rate":
                rateToUpdate.setWeeklyRate(getValidatedRate("Weekly Rate"));
                break;
            case "Monthly Rate":
                rateToUpdate.setMonthlyRate(getValidatedRate("Monthly Rate"));
                break;
            case "Yearly Rate":
                rateToUpdate.setYearlyRate(getValidatedRate("Yearly Rate"));
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
        rateToRestore.setActive(true);

        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "Rate restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAllRates() {
        for (APUHostelManagement.FeeRate rate : rateList) {
            rate.setActive(false);
        }

        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "All rates deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restoreAllRates() {
        for (APUHostelManagement.FeeRate rate : rateList) {
            rate.setActive(true);
        }

        saveRatesToFile();
        loadRates(); // Refresh the table
        JOptionPane.showMessageDialog(frame, "All rates restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private double getValidatedRate(String rateType) {
        double rate = -1;
        while (rate <= 0) {
            String input = JOptionPane.showInputDialog(frame, "Enter " + rateType + ":");
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
                    ManagerFixUpdateDeleteRestoreRateGUI window = new ManagerFixUpdateDeleteRestoreRateGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}