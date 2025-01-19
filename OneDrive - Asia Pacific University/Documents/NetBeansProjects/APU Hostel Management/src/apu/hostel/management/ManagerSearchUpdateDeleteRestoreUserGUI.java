package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerSearchUpdateDeleteRestoreUserGUI {
    private JFrame frame;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.User> userList;

    public ManagerSearchUpdateDeleteRestoreUserGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Search, Update, Delete or Restore User");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Filter, Sort, and Search components
        JPanel filterSortSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton filterButton = new JButton("Filter");
        JButton sortButton = new JButton("Sort");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        filterSortSearchPanel.add(filterButton);
        filterSortSearchPanel.add(sortButton);
        filterSortSearchPanel.add(searchField);
        filterSortSearchPanel.add(searchButton);

        topPanel.add(filterSortSearchPanel, BorderLayout.EAST);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI();
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        // User table
        tableModel = new DefaultTableModel(new Object[]{"UserID", "IC/Passport Number", "Username", "Password", "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load users into the table
        loadUsers();

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton restoreButton = new JButton("Restore");
        JButton deleteAllButton = new JButton("Delete All");
        JButton restoreAllButton = new JButton("Restore All");

        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterUsers();
            }
        });
        
        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sortUsers();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUsers(searchField.getText());
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        restoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreUser();
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllUsers();
            }
        });

        restoreAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreAllUsers();
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

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear the table
        try {
            userList = APUHostelManagement.User.readFromFile("users.txt");
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_managers.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_staffs.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_residents.txt"));
            for (APUHostelManagement.User user : userList) {
                tableModel.addRow(new Object[]{
                    user.getUserID(),
                    user.getIcPassportNumber(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getContactNumber(),
                    user.getDateOfRegistration(),
                    user.getRole(),
                    user.getIsActive()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading users.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        String[] filterOptions = {"Approved/Unapproved", "Role", "IsActive", "No filter"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame, "Select filter option:", "Filter Users", JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);

        if (filterChoice == null) {
            return; // User cancelled
        }

        List<APUHostelManagement.User> filteredUsers = new ArrayList<>(userList);

        switch (filterChoice) {
            case "Approved/Unapproved":
                String[] approvalOptions = {"Approved", "Unapproved"};
                String approvalChoice = (String) JOptionPane.showInputDialog(frame, "Select approval status:", "Filter Users", JOptionPane.QUESTION_MESSAGE, null, approvalOptions, approvalOptions[0]);
                if (approvalChoice == null) return;
                try {
                    if (approvalChoice.equals("Approved")) {
                        filteredUsers = APUHostelManagement.Manager.readApprovedUsers();
                    } else {
                        filteredUsers = APUHostelManagement.Manager.readUnapprovedUsers();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case "Role":
                String[] roleOptions = {"Manager", "Staff", "Resident"};
                String roleChoice = (String) JOptionPane.showInputDialog(frame, "Select role:", "Filter Users", JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);
                if (roleChoice == null) return;
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getRole().equalsIgnoreCase(roleChoice))
                        .collect(Collectors.toList());
                break;
            case "IsActive":
                String[] activeOptions = {"Active", "Inactive"};
                String activeChoice = (String) JOptionPane.showInputDialog(frame, "Select active status:", "Filter Users", JOptionPane.QUESTION_MESSAGE, null, activeOptions, activeOptions[0]);
                if (activeChoice == null) return;
                boolean isActive = activeChoice.equals("Active");
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getIsActive() == isActive)
                        .collect(Collectors.toList());
                break;
            default:
                break;
        }

        updateTable(filteredUsers);
    }

    private void sortUsers() {
        String[] sortOptions = {"Primary Key Ascending", "Primary Key Descending", "Username Ascending", "Username Descending"};
        String sortChoice = (String) JOptionPane.showInputDialog(frame, "Select sort option:", "Sort Users", JOptionPane.QUESTION_MESSAGE, null, sortOptions, sortOptions[0]);

        if (sortChoice == null) {
            return; // User cancelled
        }

        List<APUHostelManagement.User> sortedUsers = new ArrayList<>(userList);

        switch (sortChoice) {
            case "Primary Key Ascending":
                sortedUsers.sort(Comparator.comparing(APUHostelManagement.User::getUserID));
                break;
            case "Primary Key Descending":
                sortedUsers.sort(Comparator.comparing(APUHostelManagement.User::getUserID).reversed());
                break;
            case "Username Ascending":
                sortedUsers.sort(Comparator.comparing(APUHostelManagement.User::getUsername));
                break;
            case "Username Descending":
                sortedUsers.sort(Comparator.comparing(APUHostelManagement.User::getUsername).reversed());
                break;
            default:
                break;
        }

        updateTable(sortedUsers);
    }

    private void searchUsers(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return; // No search input
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
    
        List<APUHostelManagement.User> searchedUsers = userList.stream()
                .filter(user -> user.getUserID().toLowerCase().contains(lowerCaseQuery) ||
                                user.getIcPassportNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                                user.getPassword().toLowerCase().contains(lowerCaseQuery) ||
                                user.getContactNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getRole().toLowerCase().contains(lowerCaseQuery) ||
                                String.valueOf(user.getIsActive()).toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    
        updateTable(searchedUsers);
    }

    private void updateTable(List<APUHostelManagement.User> users) {
        tableModel.setRowCount(0); // Clear the table
        for (APUHostelManagement.User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserID(),
                user.getIcPassportNumber(),
                user.getUsername(),
                user.getPassword(),
                user.getContactNumber(),
                user.getDateOfRegistration(),
                user.getRole(),
                user.getIsActive()
            });
        }
    }

    private void updateUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        APUHostelManagement.User userToUpdate = userList.get(selectedIndex);
    
        String[] options = {"IC/Passport Number", "Username", "Password", "Contact Number"};
        String fieldToUpdate = (String) JOptionPane.showInputDialog(frame, "Select field to update:", "Update User", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (fieldToUpdate == null) {
            return; // User cancelled
        }
    
        String newValue = JOptionPane.showInputDialog(frame, "Enter new " + fieldToUpdate + ":", getCurrentValue(userToUpdate, fieldToUpdate));
    
        if (newValue == null || newValue.trim().isEmpty()) {
            return; // User cancelled or entered empty value
        }
    
        switch (fieldToUpdate) {
            case "IC/Passport Number":
                while (true) {
                    if (!APUHostelManagement.isValidICPassport(newValue)) {
                        String errorMessage = getICPassportErrorMessage(newValue);
                        int retry = JOptionPane.showConfirmDialog(frame, errorMessage + " Do you want to try again?", "Error", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.NO_OPTION) {
                            return;
                        }
                        newValue = JOptionPane.showInputDialog(frame, "Enter new IC/Passport Number:", userToUpdate.getIcPassportNumber());
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return; // User cancelled or entered empty value
                        }
                    } else {
                        userToUpdate.setIcPassportNumber(newValue);
                        break;
                    }
                }
                break;
            case "Username":
                while (true) {
                    if (!APUHostelManagement.isValidUsername(newValue)) {
                        String errorMessage = getUsernameErrorMessage(newValue);
                        int retry = JOptionPane.showConfirmDialog(frame, errorMessage + " Do you want to try again?", "Error", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.NO_OPTION) {
                            return;
                        }
                        newValue = JOptionPane.showInputDialog(frame, "Enter new Username:", userToUpdate.getUsername());
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return; // User cancelled or entered empty value
                        }
                    } else {
                        userToUpdate.setUsername(newValue);
                        break;
                    }
                }
                break;
            case "Password":
                while (true) {
                    if (!APUHostelManagement.isValidPassword(newValue, userToUpdate.getUsername())) {
                        String errorMessage = getPasswordErrorMessage(newValue, userToUpdate.getUsername());
                        int retry = JOptionPane.showConfirmDialog(frame, errorMessage + " Do you want to try again?", "Error", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.NO_OPTION) {
                            return;
                        }
                        newValue = JOptionPane.showInputDialog(frame, "Enter new Password:", userToUpdate.getPassword());
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return; // User cancelled or entered empty value
                        }
                    } else {
                        userToUpdate.setPassword(newValue);
                        break;
                    }
                }
                break;
            case "Contact Number":
                while (true) {
                    if (!APUHostelManagement.isValidContactNumber(newValue)) {
                        String errorMessage = getContactNumberErrorMessage(newValue);
                        int retry = JOptionPane.showConfirmDialog(frame, errorMessage + " Do you want to try again?", "Error", JOptionPane.YES_NO_OPTION);
                        if (retry == JOptionPane.NO_OPTION) {
                            return;
                        }
                        newValue = JOptionPane.showInputDialog(frame, "Enter new Contact Number:", userToUpdate.getContactNumber());
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return; // User cancelled or entered empty value
                        }
                    } else {
                        userToUpdate.setContactNumber(newValue);
                        break;
                    }
                }
                break;
        }
    
        try {
            APUHostelManagement.Manager.updateFile("users.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("unapproved_managers.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("unapproved_staffs.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("unapproved_residents.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("approved_managers.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("approved_staffs.txt", userToUpdate);
            APUHostelManagement.Manager.updateFile("approved_residents.txt", userToUpdate);
            loadUsers(); // Refresh the table
            JOptionPane.showMessageDialog(frame, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while updating the user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getCurrentValue(APUHostelManagement.User user, String field) {
        switch (field) {
            case "IC/Passport Number":
                return user.getIcPassportNumber();
            case "Username":
                return user.getUsername();
            case "Password":
                return user.getPassword();
            case "Contact Number":
                return user.getContactNumber();
            default:
                return "";
        }
    }
    
    private String getICPassportErrorMessage(String icPassport) {
        if (icPassport.length() != 14 && icPassport.length() != 9) {
            return "Invalid IC/Passport format. IC format: xxxxxx-xx-xxxx, Passport format: one alphabet followed by 8 numbers.";
        }
        try {
            if (!APUHostelManagement.isUnique(icPassport, "", "")) {
                return "IC/Passport number already exists. Please use a different IC/Passport number.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the IC/Passport number.";
        }
        return "Invalid IC/Passport number.";
    }
    
    private String getUsernameErrorMessage(String username) {
        if (username.length() < 3 || username.length() > 12) {
            return "Username must be between 3 and 12 characters long.";
        }
        for (char c : username.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return "Username can only contain letters, numbers, and underscores.";
            }
        }
        if (!username.matches(".*[a-zA-Z]+.*")) {
            return "Username must contain at least one letter.";
        }
        try {
            if (!APUHostelManagement.isUnique("", username, "")) {
                return "Username already exists. Please choose a different username.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the username.";
        }
        return "Invalid username.";
    }
    
    private String getPasswordErrorMessage(String password, String username) {
        if (password.length() < 8 || password.length() > 12) {
            return "Password must be between 8 and 12 characters long.";
        }
        if (password.contains(username)) {
            return "Password cannot be similar to the username.";
        }
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");
        boolean hasInvalidChar = password.matches(".*[^a-zA-Z0-9!@#$%^&*()].*");
        if (!(hasNumber && hasSpecialChar && hasUppercase)) {
            return "Password must contain at least one number, one special character (!@#$%^&*()), and one uppercase letter.";
        }
        if (hasInvalidChar) {
            return "Password contains invalid characters. Only !@#$%^&*() are allowed as special characters.";
        }
        return "Invalid password.";
    }
    
    private String getContactNumberErrorMessage(String contactNumber) {
        if (contactNumber.length() != 12 || !contactNumber.startsWith("01") || contactNumber.charAt(3) != '-' || contactNumber.charAt(7) != '-' || !contactNumber.replace("-", "").matches("\\d+")) {
            return "Invalid contact number format. The correct format is 01X-XXX-XXXX.";
        }
        try {
            if (!APUHostelManagement.isUnique("", "", contactNumber)) {
                return "Contact number already exists. Please choose a different contact number.";
            }
        } catch (IOException e) {
            return "An error occurred while checking the contact number.";
        }
        return "Invalid contact number.";
    }

    private void deleteUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.User userToDelete = userList.get(selectedIndex);
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        userToDelete.setIsActive(false);
        try {
            APUHostelManagement.Manager.updateFile("users.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("unapproved_managers.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("unapproved_staffs.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("unapproved_residents.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("approved_managers.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("approved_staffs.txt", userToDelete);
            APUHostelManagement.Manager.updateFile("approved_residents.txt", userToDelete);
            loadUsers(); // Refresh the table
            JOptionPane.showMessageDialog(frame, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while deleting the user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to restore.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.User userToRestore = userList.get(selectedIndex);
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore this user?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        userToRestore.setIsActive(true);
        try {
            APUHostelManagement.Manager.updateFile("users.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("unapproved_managers.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("unapproved_staffs.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("unapproved_residents.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("approved_managers.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("approved_staffs.txt", userToRestore);
            APUHostelManagement.Manager.updateFile("approved_residents.txt", userToRestore);
            loadUsers(); // Refresh the table
            JOptionPane.showMessageDialog(frame, "User restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while restoring the user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAllUsers() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete all users?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.User user : userList) {
            user.setIsActive(false);
        }

        try {
            for (APUHostelManagement.User user : userList) {
                APUHostelManagement.Manager.updateFile("users.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_managers.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_staffs.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_residents.txt", user);
                APUHostelManagement.Manager.updateFile("approved_managers.txt", user);
                APUHostelManagement.Manager.updateFile("approved_staffs.txt", user);
                APUHostelManagement.Manager.updateFile("approved_residents.txt", user);
            }
            loadUsers(); // Refresh the table
            JOptionPane.showMessageDialog(frame, "All users deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while deleting all users.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreAllUsers() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore all users?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        for (APUHostelManagement.User user : userList) {
            user.setIsActive(true);
        }

        try {
            for (APUHostelManagement.User user : userList) {
                APUHostelManagement.Manager.updateFile("users.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_managers.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_staffs.txt", user);
                APUHostelManagement.Manager.updateFile("unapproved_residents.txt", user);
                APUHostelManagement.Manager.updateFile("approved_managers.txt", user);
                APUHostelManagement.Manager.updateFile("approved_staffs.txt", user);
                APUHostelManagement.Manager.updateFile("approved_residents.txt", user);
            }
            loadUsers(); // Refresh the table
            JOptionPane.showMessageDialog(frame, "All users restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while restoring all users.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ManagerSearchUpdateDeleteRestoreUserGUI window = new ManagerSearchUpdateDeleteRestoreUserGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}