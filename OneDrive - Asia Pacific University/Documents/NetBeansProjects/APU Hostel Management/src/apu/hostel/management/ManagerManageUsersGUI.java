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

public class ManagerManageUsersGUI {
    private JFrame frame;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.User> userList;
    private APUHostelManagement.Manager manager; // Add manager field
    private List<APUHostelManagement.User> filteredUserList;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;

    // Add new constructor
    public ManagerManageUsersGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        this.userList = new ArrayList<>();
        this.filteredUserList = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        if (manager == null) {
            JOptionPane.showMessageDialog(null, "Invalid manager session.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        frame = new JFrame("Search, Update, Delete or Restore User");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Filter, Sort, and Search components
        JPanel filterSortSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton filterButton = createButton("Filter", "filter_icon.png");
        JButton sortButton = createButton("Sort", "sort_icon.png");
        JTextField searchField = new JTextField(20);
        JButton searchButton = createButton("Search", "search_icon.png");

        filterSortSearchPanel.add(filterButton);
        filterSortSearchPanel.add(sortButton);
        filterSortSearchPanel.add(searchField);
        filterSortSearchPanel.add(searchButton);

        topPanel.add(filterSortSearchPanel, BorderLayout.EAST);

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

        // User table
        tableModel = new DefaultTableModel(new Object[]{"UserID", "IC/Passport Number", "Username", "Password", "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Load users into the table
        loadUsers();

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = createButton("Update", "update_icon.png");
        JButton deleteButton = createButton("Delete", "delete_icon.png");
        JButton restoreButton = createButton("Restore", "restore_icon.png");
        JButton deleteAllButton = createButton("Delete All", "delete_all_icon.png");
        JButton restoreAllButton = createButton("Restore All", "restore_all_icon.png");

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

        searchField.addActionListener(e -> searchUsers(searchField.getText()));
        JButton clearButton = createButton("Clear", "clear_icon.png");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUsers();
            }
        });
        filterSortSearchPanel.add(clearButton);

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

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear the table
        try {
            userList = APUHostelManagement.User.readFromFile("users.txt");
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_managers.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_staffs.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_residents.txt"));
            
            // Initialize filteredUserList with all users initially
            filteredUserList = new ArrayList<>(userList);
            
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
            JOptionPane.showMessageDialog(frame, "An error occurred while loading users.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        String[] filterOptions = {"Approved/Unapproved", "Role", "IsActive", "No filter"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame, 
            "Select filter option:", "Filter Users", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);
    
        if (filterChoice == null) {
            return; // User cancelled
        }
    
        // Reset filter if "No filter" selected
        if (filterChoice.equals("No filter")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            loadUsers();
            return;
        }
    
        currentFilterChoice = filterChoice;
        List<APUHostelManagement.User> filteredUsers = new ArrayList<>(userList);
    
        switch (filterChoice) {
            case "Approved/Unapproved" -> {
                String[] approvalOptions = {"Approved", "Unapproved"};
                String approvalChoice = (String) JOptionPane.showInputDialog(frame, 
                    "Select approval status:", "Filter Users",
                    JOptionPane.QUESTION_MESSAGE, null, approvalOptions, approvalOptions[0]);
                if (approvalChoice == null) return;
                currentFilterValue = approvalChoice;
                try {
                    filteredUsers = approvalChoice.equals("Approved") 
                        ? APUHostelManagement.Manager.readApprovedUsers()
                        : APUHostelManagement.Manager.readUnapprovedUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            case "Role" -> {
                String[] roleOptions = {"Manager", "Staff", "Resident"};
                String roleChoice = (String) JOptionPane.showInputDialog(frame,
                    "Select role:", "Filter Users",
                    JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);
                if (roleChoice == null) return;
                currentFilterValue = roleChoice;
                filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getRole().equalsIgnoreCase(roleChoice))
                    .collect(Collectors.toList());
            }
            case "IsActive" -> {
                String[] activeOptions = {"Active", "Inactive"};
                String activeChoice = (String) JOptionPane.showInputDialog(frame,
                    "Select active status:", "Filter Users",
                    JOptionPane.QUESTION_MESSAGE, null, activeOptions, activeOptions[0]);
                if (activeChoice == null) return;
                currentFilterValue = activeChoice;
                boolean isActive = activeChoice.equals("Active");
                filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getIsActive() == isActive)
                    .collect(Collectors.toList());
            }
        }
    
        updateTable(filteredUsers);
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            List<APUHostelManagement.User> filteredUsers = new ArrayList<>(userList);
            
            try {
                switch (currentFilterChoice) {
                    case "Approved/Unapproved" -> {
                        if (currentFilterValue.equals("Approved")) {
                            filteredUsers = APUHostelManagement.Manager.readApprovedUsers();
                        } else {
                            filteredUsers = APUHostelManagement.Manager.readUnapprovedUsers();
                        }
                    }
                    case "Role" -> {
                        filteredUsers = filteredUsers.stream()
                            .filter(user -> user.getRole().equalsIgnoreCase(currentFilterValue))
                            .collect(Collectors.toList());
                    }
                    case "IsActive" -> {
                        boolean isActive = currentFilterValue.equals("Active");
                        filteredUsers = filteredUsers.stream()
                            .filter(user -> user.getIsActive() == isActive)
                            .collect(Collectors.toList());
                    }
                }
                updateTable(filteredUsers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadUsers();
        }
    }

    private void sortUsers() {
        String[] sortOptions = {"User ID Ascending", "User ID Descending", "Username Ascending", "Username Descending"};
        String sortChoice = (String) JOptionPane.showInputDialog(frame, "Select sort option:", "Sort Users", JOptionPane.QUESTION_MESSAGE, null, sortOptions, sortOptions[0]);

        if (sortChoice == null) {
            return; // User cancelled
        }

        List<APUHostelManagement.User> sortedUsers = new ArrayList<>(filteredUserList);

        switch (sortChoice) {
            case "User ID Ascending":
                sortedUsers.sort(Comparator.comparing(APUHostelManagement.User::getUserID));
                break;
            case "User ID Descending":
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
            // If search query is empty, reapply current filter or show all users
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUsers();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
    
        List<APUHostelManagement.User> searchedUsers = filteredUserList.stream()
                .filter(user -> user.getUserID().toLowerCase().contains(lowerCaseQuery) ||
                                user.getIcPassportNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                                user.getPassword().toLowerCase().contains(lowerCaseQuery) ||
                                user.getContactNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getDateOfRegistration().toLowerCase().contains(lowerCaseQuery) ||
                                user.getRole().toLowerCase().contains(lowerCaseQuery) ||
                                String.valueOf(user.getIsActive()).toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    
        updateTable(searchedUsers);
    }

    private void updateTable(List<APUHostelManagement.User> users) {
        tableModel.setRowCount(0); // Clear the table
        
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No users found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadUsers(); // Only reload if no filter is active
            }
            return;
        }
        
        // Update filtered list
        filteredUserList = new ArrayList<>(users);
        
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
    
        APUHostelManagement.User userToUpdate = filteredUserList.get(selectedIndex);
    
        String[] options = {"IC/Passport Number", "Username", "Password", "Contact Number"};
        String fieldToUpdate = (String) JOptionPane.showInputDialog(frame, 
            "Select field to update:", "Update User", 
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
        if (fieldToUpdate == null) return;
    
        String newValue = JOptionPane.showInputDialog(frame, 
            "Enter new " + fieldToUpdate + ":", 
            getCurrentValue(userToUpdate, fieldToUpdate));
    
        if (newValue == null || newValue.trim().isEmpty()) return;
    
        try {
            switch (fieldToUpdate) {
                case "IC/Passport Number" -> {
                    if (newValue.equals(userToUpdate.getIcPassportNumber())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateICPassport(newValue);
                    userToUpdate.setIcPassportNumber(newValue);
                }
                case "Username" -> {
                    if (newValue.equals(userToUpdate.getUsername())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateUsername(newValue);
                    userToUpdate.setUsername(newValue);
                }
                case "Password" -> {
                    if (newValue.equals(userToUpdate.getPassword())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validatePassword(newValue, userToUpdate.getUsername());
                    userToUpdate.setPassword(newValue);
                }
                case "Contact Number" -> {
                    if (newValue.equals(userToUpdate.getContactNumber())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateContactNumber(newValue);
                    userToUpdate.setContactNumber(newValue);
                }
            }
    
            updateAllUserFiles(userToUpdate);
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "User updated successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (Exception e) {
            handleUpdateError(e.getMessage(), fieldToUpdate, userToUpdate);
        }
    }
    
    private void showSameValueMessage(String fieldName) {
        JOptionPane.showMessageDialog(frame,
            "The " + fieldName + " is the same as the original value.",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleUpdateError(String errorMessage, String fieldToUpdate, APUHostelManagement.User userToUpdate) {
        int retry = JOptionPane.showConfirmDialog(frame,
            errorMessage + "\nDo you want to try again?",
            "Error", JOptionPane.YES_NO_OPTION);
            
        if (retry == JOptionPane.YES_OPTION) {
            String newValue = JOptionPane.showInputDialog(frame,
                "Enter new " + fieldToUpdate + ":",
                getCurrentValue(userToUpdate, fieldToUpdate));
            if (newValue != null && !newValue.trim().isEmpty()) {
                updateSelectedField(userToUpdate, fieldToUpdate, newValue);
            }
        }
    }
    
    private void updateAllUserFiles(APUHostelManagement.User userToUpdate) throws IOException {
        String[] files = {
            "users.txt", "unapproved_managers.txt", "unapproved_staffs.txt",
            "unapproved_residents.txt", "approved_managers.txt", 
            "approved_staffs.txt", "approved_residents.txt"
        };
        
        for (String file : files) {
            APUHostelManagement.Manager.updateFile(file, userToUpdate);
        }
    }

    private void updateSelectedField(APUHostelManagement.User userToUpdate, String fieldToUpdate, String newValue) {
        try {
            switch (fieldToUpdate) {
                case "IC/Passport Number" -> {
                    if (newValue.equals(userToUpdate.getIcPassportNumber())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateICPassport(newValue);
                    userToUpdate.setIcPassportNumber(newValue);
                }
                case "Username" -> {
                    if (newValue.equals(userToUpdate.getUsername())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateUsername(newValue);
                    userToUpdate.setUsername(newValue);
                }
                case "Password" -> {
                    if (newValue.equals(userToUpdate.getPassword())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validatePassword(newValue, userToUpdate.getUsername());
                    userToUpdate.setPassword(newValue);
                }
                case "Contact Number" -> {
                    if (newValue.equals(userToUpdate.getContactNumber())) {
                        showSameValueMessage(fieldToUpdate);
                        return;
                    }
                    APUHostelManagement.validateContactNumber(newValue);
                    userToUpdate.setContactNumber(newValue);
                }
            }
    
            updateAllUserFiles(userToUpdate);
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "User updated successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (Exception e) {
            handleUpdateError(e.getMessage(), fieldToUpdate, userToUpdate);
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
    
    private void deleteUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        APUHostelManagement.User userToDelete = filteredUserList.get(selectedIndex);
        if (userToDelete.getIsActive() == false) {
            JOptionPane.showMessageDialog(frame, "User is already deleted.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        userToDelete.setIsActive(false);
        try {
            updateAllUserFiles(userToDelete);
            reapplyCurrentFilter();


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

        APUHostelManagement.User userToRestore = filteredUserList.get(selectedIndex);
        if (userToRestore.getIsActive() == true) {
            JOptionPane.showMessageDialog(frame, "User is already active.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restore this user?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        userToRestore.setIsActive(true);
        try {
            updateAllUserFiles(userToRestore);
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "User restored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while restoring the user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAllUsers() {
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to delete all filtered users?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        // Only affect filtered users
        for (APUHostelManagement.User user : filteredUserList) {
            user.setIsActive(false);
        }
    
        try {
            for (APUHostelManagement.User user : filteredUserList) {
                updateAllUserFiles(user);
            }
            // Reload with same filter
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "All filtered users deleted successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "An error occurred while deleting users.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreAllUsers() {
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to restore all filtered users?", 
            "Confirm Restoration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        // Only affect filtered users
        for (APUHostelManagement.User user : filteredUserList) {
            user.setIsActive(true);
        }
    
        try {
            for (APUHostelManagement.User user : filteredUserList) {
                updateAllUserFiles(user);
            }
            // Reload with same filter
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "All filtered users restored successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "An error occurred while restoring users.", 
                "Error", JOptionPane.ERROR_MESSAGE);
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