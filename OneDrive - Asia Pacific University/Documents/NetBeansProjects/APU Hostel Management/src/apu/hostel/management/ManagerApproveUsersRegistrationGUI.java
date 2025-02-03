package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerApproveUsersRegistrationGUI {
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

    public ManagerApproveUsersRegistrationGUI(APUHostelManagement.Manager manager) {
        this.manager = manager;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Approve Users");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setTitle("Approve Users - " + manager.getUsername());
        frame.setLocationRelativeTo(null);

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new ManagerMainPageGUI(manager);
            frame.dispose();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        
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
                    frame.setTitle("Approve Users - " + manager.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(userList);
                    } else {
                        loadUnapprovedUsers();
                    }
                }
            } else {
                filterUsers();
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
                        loadUnapprovedUsers();
                    }
                }
            } else {
                sortUsers();
            }
        });

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
        searchButton.addActionListener(e -> searchUsers(searchField.getText()));

        JButton clearButton = createButton("Clear", "clear_icon.png");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUnapprovedUsers();
            }
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        topPanel.add(filterPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(
            new Object[]{"UserID", "IC/Passport Number", "Username", "Password", 
                        "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setRowHeight(25); 
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        userTable.setSelectionBackground(new Color(230, 240, 250));
        userTable.setSelectionForeground(Color.BLACK);
        userTable.setGridColor(Color.LIGHT_GRAY);
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

        
        JPanel approvePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton approveButton = createButton("Approve", "approve_icon.png");
        approveButton.setPreferredSize(new Dimension(150, 40));
        approveButton.addActionListener(e -> approveSelectedUser());
        approvePanel.add(approveButton);
        frame.add(approvePanel, BorderLayout.SOUTH);

        loadUnapprovedUsers();
        frame.setVisible(true);

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    approveSelectedUser();
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

        backButton.setMnemonic(KeyEvent.VK_B);  
        approveButton.setMnemonic(KeyEvent.VK_A); 
        filterButton.setMnemonic(KeyEvent.VK_F);  
        sortButton.setMnemonic(KeyEvent.VK_S);
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C); 

        backButton.setToolTipText("Go back (Alt+B)");
        approveButton.setToolTipText("Approve selected user (Alt+A)");
        filterButton.setToolTipText("Filter users (Alt+F)");
        sortButton.setToolTipText("Sort users (Alt+S)");
        searchButton.setToolTipText("Search by anything (case-insensitive) and press Enter");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        

        addButtonHoverEffect(backButton);
        addButtonHoverEffect(approveButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);

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

    private void loadUnapprovedUsers() {
        try {
            userList = new ArrayList<>();
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_managers.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_staffs.txt"));
            userList.addAll(APUHostelManagement.User.readFromFile("unapproved_residents.txt"));
    
            if (userList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "No users to approve.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            
            updateTable(userList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "Error loading unapproved users: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<APUHostelManagement.User> users) {
        tableModel.setRowCount(0);
        
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No users found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadUnapprovedUsers();
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

    private void approveSelectedUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to approve.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to approve this user?", 
            "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
    
        APUHostelManagement.User userToApprove = userList.get(selectedIndex);
        String role = (String) userTable.getValueAt(selectedIndex, 6);
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        try {
            if (role.equals("manager")) {
                APUHostelManagement.Manager managerToApprove = (APUHostelManagement.Manager) userToApprove;
                String userID = APUHostelManagement.generateUserID("U");
                String managerID = APUHostelManagement.generateUserID("M");
                managerToApprove.setUserID(userID);
                managerToApprove.setManagerID(managerID);
                managerToApprove.setDateOfApproval(currentDate);
                managerToApprove.setIsActive(true);
                managerToApprove.saveToFile("users.txt");
                managerToApprove.saveToManagerFile(managerID, userID, "approved_managers.txt");
                userList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(
                    userList.stream()
                        .filter(u -> u.getRole().equals("manager"))
                        .collect(Collectors.toList()), 
                    "unapproved_managers.txt"
                );
                JOptionPane.showMessageDialog(frame, "Manager approved successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
    
            } else if (role.equals("staff")) {
                APUHostelManagement.Staff staffToApprove = (APUHostelManagement.Staff) userToApprove;
                String userID = APUHostelManagement.generateUserID("U");
                String staffID = APUHostelManagement.generateUserID("S");
                staffToApprove.setUserID(userID);
                staffToApprove.setStaffID(staffID);
                staffToApprove.setDateOfApproval(currentDate);
                staffToApprove.setIsActive(true);
                staffToApprove.saveToFile("users.txt");
                staffToApprove.saveToStaffFile(staffID, userID, "approved_staffs.txt");
                userList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(
                    userList.stream()
                        .filter(u -> u.getRole().equals("staff"))
                        .collect(Collectors.toList()), 
                    "unapproved_staffs.txt"
                );
                JOptionPane.showMessageDialog(frame, "Staff approved successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
    
            } else if (role.equals("resident")) {
                APUHostelManagement.Resident residentToApprove = (APUHostelManagement.Resident) userToApprove;
                String userID = APUHostelManagement.generateUserID("U");
                String residentID = APUHostelManagement.generateUserID("R");
                residentToApprove.setUserID(userID);
                residentToApprove.setResidentID(residentID);
                residentToApprove.setDateOfApproval(currentDate);
                residentToApprove.setIsActive(true);
                residentToApprove.saveToFile("users.txt");
                residentToApprove.saveToResidentFile(residentID, userID, "approved_residents.txt");
                userList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(
                    userList.stream()
                        .filter(u -> u.getRole().equals("resident"))
                        .collect(Collectors.toList()), 
                    "unapproved_residents.txt"
                );
                JOptionPane.showMessageDialog(frame, "Resident approved successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
    
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid user role.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
    
            updateTable(userList); 
    
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "An error occurred while approving the user.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        String[] roleOptions = {"All", "Manager", "Staff", "Resident"};
        String roleChoice = (String) JOptionPane.showInputDialog(frame,
            "Select role to filter:", "Filter Users",
            JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);
    
        if (roleChoice == null) return;
    
        if (roleChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            loadUnapprovedUsers();
            return;
        }
    
        currentFilterChoice = "Role";
        currentFilterValue = roleChoice;
    
        List<APUHostelManagement.User> filteredUsers = userList;
        if (!roleChoice.equals("All")) {
            filteredUsers = userList.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(roleChoice))
                .collect(Collectors.toList());
        }

        if (!roleChoice.equals("All")) {
            filterButton.setText("Filter: " + roleChoice);
        } else {
            filterButton.setText("Filter");
        }

        if (!filteredUsers.isEmpty()) {
            frame.setTitle("Approve Users - " + manager.getUsername() + 
                " (Filtered: " + roleChoice + ", " + filteredUsers.size() + " users)");
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
            
            if (currentFilterChoice.equals("Role")) {
                if (!currentFilterValue.equals("All")) {
                    filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getRole().equalsIgnoreCase(currentFilterValue))
                        .collect(Collectors.toList());
                }
            }
            updateTable(filteredUsers);
        } else {
            loadUnapprovedUsers();
        }
    }

    private void searchUsers(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadUnapprovedUsers();
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
        
        if (!searchedUsers.isEmpty()) {
            frame.setTitle("Manage Users - " + manager.getUsername() + 
                " (Found " + searchedUsers.size() + " results)");
        }
    
        updateTable(searchedUsers);
    }

    private void sortUsers() {
        String[] options = {"Username A-Z", "Username Z-A", "Registration Date (Newest)", "Registration Date (Oldest)"};
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Users", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null) return;

        currentSortCategory = choice.split(" ")[0]; 
        currentSortOrder = choice.contains("Z-A") || choice.contains("Newest") ? "Descending" : "Ascending";

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
        } else {
            sortButton.setText("Sort");
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
}