package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagerApproveUserTableGUI {
    private JFrame frame;
    private JPanel userPanel;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private List<APUHostelManagement.User> selectedRoleList;
    private JLabel tableTitleLabel;
    private String role;

    public ManagerApproveUserTableGUI(String role, List<APUHostelManagement.User> selectedRoleList) {
        this.role = role;
        this.selectedRoleList = selectedRoleList;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Approve " + role);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout());

        // Top panel with back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Back button in table page clicked");
                new ManagerApproveUserRegistrationGUI();
                frame.dispose();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

        // User list panel
        userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        tableTitleLabel = new JLabel("Approve " + role, JLabel.CENTER);
        userPanel.add(tableTitleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Index", "IC/Passport Number", "Username", "Password", "Contact Number", "Date Of Registration", "Role", "Is Active"}, 0);
        userTable = new JTable(tableModel);
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // Set width for index column
        JScrollPane scrollPane = new JScrollPane(userTable);
        userPanel.add(scrollPane, BorderLayout.CENTER);

        int index = 1;
        for (APUHostelManagement.User user : selectedRoleList) {
            tableModel.addRow(new Object[]{
                index++,
                user.getIcPassportNumber(),
                user.getUsername(),
                user.getPassword(),
                user.getContactNumber(),
                user.getDateOfRegistration(),
                user.getRole(),
                user.getIsActive()
            });
        }

        // Approve button panel
        JPanel approveButtonPanel = new JPanel();
        approveButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add spacing above the button
        JButton approveButton = new JButton("Approve");
        approveButton.setPreferredSize(new Dimension(150, 40));
        approveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                approveSelectedUser();
            }
        });
        approveButtonPanel.add(approveButton);
        userPanel.add(approveButtonPanel, BorderLayout.SOUTH);

        frame.add(userPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void approveSelectedUser() {
        int selectedIndex = userTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to approve.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to approve this user?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        APUHostelManagement.User userToApprove = selectedRoleList.get(selectedIndex);
        String role = (String) userTable.getValueAt(selectedIndex, 6); // Get the role from the table
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
                selectedRoleList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(selectedRoleList, "unapproved_managers.txt");
                JOptionPane.showMessageDialog(frame, "Manager approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                selectedRoleList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(selectedRoleList, "unapproved_staffs.txt");
                JOptionPane.showMessageDialog(frame, "Staff approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                selectedRoleList.remove(selectedIndex);
                APUHostelManagement.Manager.saveUnapprovedUsers(selectedRoleList, "unapproved_residents.txt");
                JOptionPane.showMessageDialog(frame, "Resident approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid user role.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while approving the user.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        new ManagerApproveUserRegistrationGUI();
        frame.dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Example usage
                    List<APUHostelManagement.User> users = APUHostelManagement.User.readFromFile("unapproved_managers.txt");
                    new ManagerApproveUserTableGUI("Manager", users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}