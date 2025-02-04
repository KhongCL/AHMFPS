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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerManageUsersGUI {
    private JFrame frame;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.User> userList;
    private APUHostelManagement.Manager manager; 
    private List<APUHostelManagement.User> filteredUserList;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private JButton filterButton;
    private JButton sortButton;
    private String currentSortCategory = null;
    private String currentSortOrder = null;

    
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
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setTitle("Manage Users - " + manager.getUsername());
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        
        JPanel filterSortSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        sortButton = createButton("Sort", "sort_icon.png");

        JTextField searchField = new JTextField(20);
        searchField.setText("Search users...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search users...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search users...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        filterSortSearchPanel.add(filterButton);
        filterSortSearchPanel.add(sortButton);
        filterSortSearchPanel.add(searchField);
        filterSortSearchPanel.add(searchButton);

        topPanel.add(filterSortSearchPanel, BorderLayout.EAST);

        
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManagerMainPageGUI(manager); 
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new Object[]{"UserID", "IC/Passport Number", "Username", "Password", "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        userTable.setSelectionBackground(new Color(230, 240, 250));
        userTable.setSelectionForeground(Color.BLACK);
        userTable.setGridColor(Color.LIGHT_GRAY);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setIntercellSpacing(new Dimension(5, 5));
        userTable.setShowGrid(true);
        userTable.setFillsViewportHeight(true);

        
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        userTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120); 
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); 
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100); 
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        userTable.getColumnModel().getColumn(5).setPreferredWidth(150); 
        userTable.getColumnModel().getColumn(6).setPreferredWidth(80); 
        userTable.getColumnModel().getColumn(7).setPreferredWidth(70); 

        JScrollPane scrollPane = new JScrollPane(userTable);
        frame.add(scrollPane, BorderLayout.CENTER);



        
        loadUsers();

        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = createButton("Update", "update_icon.png");
        JButton deleteButton = createButton("Delete", "delete_icon.png");
        JButton restoreButton = createButton("Restore", "restore_icon.png");
        JButton deleteAllButton = createButton("Delete All", "delete_all_icon.png");
        JButton restoreAllButton = createButton("Restore All", "restore_all_icon.png");

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
                    frame.setTitle("Manage Users - " + manager.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(userList);
                    } else {
                        loadUsers();
                    }
                }
            } else {
                filterUsers();
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
                        loadUsers();
                    }
                }
            } else {
                sortUsers();
            }
        });
        
        searchButton.addActionListener(e -> searchUsers(searchField.getText()));

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

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateUser();
                }
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

        backButton.setMnemonic(KeyEvent.VK_B); 
        filterButton.setMnemonic(KeyEvent.VK_F); 
        sortButton.setMnemonic(KeyEvent.VK_S); 
        searchButton.setMnemonic(KeyEvent.VK_ENTER); 
        clearButton.setMnemonic(KeyEvent.VK_C); 
        updateButton.setMnemonic(KeyEvent.VK_U);  
        deleteButton.setMnemonic(KeyEvent.VK_D);  
        restoreButton.setMnemonic(KeyEvent.VK_R); 
        deleteAllButton.setMnemonic(KeyEvent.VK_L); 
        restoreAllButton.setMnemonic(KeyEvent.VK_T); 

        backButton.setToolTipText("Go back to main page (Alt+B)");
        filterButton.setToolTipText("Filter users (Alt+F)");
        sortButton.setToolTipText("Sort users (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter"); 
        clearButton.setToolTipText("Clear search query (Alt+C)");
        updateButton.setToolTipText("Update selected user (Alt+U)");
        deleteButton.setToolTipText("Delete selected user (Alt+D)");
        restoreButton.setToolTipText("Restore selected user (Alt+R)");
        deleteAllButton.setToolTipText("Delete all filtered users (Alt+L)");
        restoreAllButton.setToolTipText("Restore all filtered users (Alt+T)");


        addButtonHoverEffect(backButton);
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

    private void loadUsers() {
        tableModel.setRowCount(0); 
        try {
            userList = APUHostelManagement.User.readFromFile("users.txt");
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_managers.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_staffs.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_residents.txt"));
            
            
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
        String[] filterOptions = {"Approved/Unapproved", "Role", "IsActive", "All"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame, 
            "Select filter option:", "Filter Users", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);
    
        if (filterChoice == null) {
            return; 
        }
    
        
        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("Manage Users - " + manager.getUsername());
            
            filteredUserList = new ArrayList<>(userList); // Use existing users instead of reloading
            
            if (currentSortCategory != null) {
                applySorting(filteredUserList); // Apply current sorting if active
            } else {
                updateTable(filteredUserList); // Just update table if no sorting
            }
            return;
        }

        if (!filterChoice.equals("All")) {
            filterButton.setText("Filter: " + filterChoice);
        } else {
            filterButton.setText("Filter");
        }
        
    
        currentFilterChoice = filterChoice;
        List<APUHostelManagement.User> filteredUsers = new ArrayList<>(userList);
    
        switch (filterChoice) {
            case "Approved/Unapproved" -> {
                String[] approvalOptions = {"Approved", "Unapproved"};
                String approvalChoice = (String) JOptionPane.showInputDialog(frame, 
                    "Select approval status:", "Filter Users",
                    JOptionPane.QUESTION_MESSAGE, null, approvalOptions, approvalOptions[0]);
                
                if (!filteredUsers.isEmpty()) {
                    frame.setTitle("Manage Users - " + manager.getUsername() + 
                        " (Filtered: " + approvalChoice + ", " + filteredUsers.size() + " users)");
                }

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

                if (!filteredUsers.isEmpty()) {
                    frame.setTitle("Manage Users - " + manager.getUsername() + 
                        " (Filtered: " + roleChoice + ", " + filteredUsers.size() + " users)");
                }
    
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

                if (!filteredUsers.isEmpty()) {
                    frame.setTitle("Manage Users - " + manager.getUsername() + 
                        " (Filtered: " + activeChoice + ", " + filteredUsers.size() + " users)");
                }

                if (activeChoice == null) return;
                currentFilterValue = activeChoice;
                boolean isActive = activeChoice.equals("Active");
                filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getIsActive() == isActive)
                    .collect(Collectors.toList());
            }
        }
        if (currentSortCategory != null) {
            applySorting(filteredUsers);
        } else {
            updateTable(filteredUsers);
        }
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
        String[] sortOptions = {
            "Username A-Z", 
            "Username Z-A",
            "Registration Date (Newest)", 
            "Registration Date (Oldest)"
        };
        String sortChoice = (String) JOptionPane.showInputDialog(frame, "Select sort option:", "Sort Users", JOptionPane.QUESTION_MESSAGE, null, sortOptions, sortOptions[0]);

        if (sortChoice == null) return;
        

        if (sortChoice != null) {
            sortButton.setText("Sort: " + sortChoice.split(" ")[0]);
        }

        currentSortCategory = sortChoice.split(" ")[0]; 
        currentSortOrder = sortChoice.contains("Z-A") || sortChoice.contains("Newest") ? "Descending" : "Ascending";

        applySorting(filteredUserList != null ? filteredUserList : userList);
    }

    private void applySorting(List<APUHostelManagement.User> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<APUHostelManagement.User> sortedList = new ArrayList<>(listToSort);
        
        Comparator<APUHostelManagement.User> comparator = switch (currentSortCategory) {
            case "Username" -> Comparator.comparing(APUHostelManagement.User::getUsername);
            case "Registration" -> Comparator.comparing(APUHostelManagement.User::getDateOfRegistration);
            default -> null;
        };
    
        if (comparator != null) {
            if (currentSortOrder.equals("Descending")) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            sortButton.setText("Sort: " + currentSortCategory.split(" ")[0]);
            updateTable(sortedList);
        }
    }

    private void searchUsers(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUsers();
            }
            return;
        }
    
        String lowerCaseQuery = searchQuery.toLowerCase();
        
        List<APUHostelManagement.User> searchList = filteredUserList != null ? 
            filteredUserList : new ArrayList<>(userList);
    
        List<APUHostelManagement.User> searchedUsers = searchList.stream()
                .filter(user -> user.getUserID().toLowerCase().contains(lowerCaseQuery) ||
                                user.getIcPassportNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                                user.getPassword().toLowerCase().contains(lowerCaseQuery) ||
                                user.getContactNumber().toLowerCase().contains(lowerCaseQuery) ||
                                user.getDateOfRegistration().toLowerCase().contains(lowerCaseQuery) ||
                                user.getRole().toLowerCase().contains(lowerCaseQuery) ||
                                String.valueOf(user.getIsActive()).toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
        
        if (!searchedUsers.isEmpty()) {
            frame.setTitle("Manage Users - " + manager.getUsername() + 
                " (Found " + searchedUsers.size() + " results)");
            updateTable(searchedUsers);
        } else {
            frame.setTitle("Manage Users - " + manager.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No users found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUsers();
            }
        }
    }

    private void updateTable(List<APUHostelManagement.User> users) {
        tableModel.setRowCount(0); 
        
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No users found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadUsers(); 
            }
            return;
        }
        
        
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
    
        
        for (APUHostelManagement.User user : filteredUserList) {
            user.setIsActive(false);
        }
    
        try {
            for (APUHostelManagement.User user : filteredUserList) {
                updateAllUserFiles(user);
            }
            
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
    
        
        for (APUHostelManagement.User user : filteredUserList) {
            user.setIsActive(true);
        }
    
        try {
            for (APUHostelManagement.User user : filteredUserList) {
                updateAllUserFiles(user);
            }
            
            reapplyCurrentFilter();
            JOptionPane.showMessageDialog(frame, "All filtered users restored successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "An error occurred while restoring users.", 
                "Error", JOptionPane.ERROR_MESSAGE);
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
            } else if (text.contains("Clear")) {
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