package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerApproveUsersRegistrationGUI {
        private JFrame frame;
        private JTable userTable;
        private DefaultTableModel tableModel;
        private List<APUHostelManagement.User> userList;
        private APUHostelManagement.Manager manager;

        public ManagerApproveUsersRegistrationGUI(APUHostelManagement.Manager manager) {
            this.manager = manager;
            initialize();
        }

        private void initialize() {
            frame = new JFrame("Approve Users");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.setLayout(new BorderLayout(10, 10));

            // Top panel
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Back button
            JButton backButton = createButton("Back", "back_icon.png");
            backButton.setPreferredSize(new Dimension(100, 40));
            backButton.addActionListener(e -> {
                new ManagerMainPageGUI(manager);
                frame.dispose();
            });
            topPanel.add(backButton, BorderLayout.WEST);

            // Filter panel
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton filterButton = createButton("Filter", "filter_icon.png");
            filterButton.addActionListener(e -> filterUsers());
            filterPanel.add(filterButton);
            topPanel.add(filterPanel, BorderLayout.EAST);

            frame.add(topPanel, BorderLayout.NORTH);

            // Table setup
            tableModel = new DefaultTableModel(
                new Object[]{"UserID", "IC/Passport Number", "Username", "Password", 
                            "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0);
            userTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(userTable);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Approve button panel
            JPanel approvePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton approveButton = createButton("Approve", "approve_icon.png");
            approveButton.setPreferredSize(new Dimension(150, 40));
            approveButton.addActionListener(e -> approveSelectedUser());
            approvePanel.add(approveButton);
            frame.add(approvePanel, BorderLayout.SOUTH);

            loadUnapprovedUsers();
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

        private void filterUsers() {
            String[] roleOptions = {"All", "Manager", "Staff", "Resident"};
            String roleChoice = (String) JOptionPane.showInputDialog(frame,
                "Select role to filter:", "Filter Users",
                JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);

            if (roleChoice == null) return;

            List<APUHostelManagement.User> filteredUsers = userList;
            if (!roleChoice.equals("All")) {
                filteredUsers = userList.stream()
                    .filter(user -> user.getRole().equalsIgnoreCase(roleChoice))
                    .collect(Collectors.toList());
            }
            
            updateTable(filteredUsers);
        }

        private void updateTable(List<APUHostelManagement.User> users) {
            tableModel.setRowCount(0);
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
        
                updateTable(userList); // Refresh the table with updated data
        
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, 
                    "An error occurred while approving the user.", 
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
}