package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class LoginPageGUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPageGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout(10, 10)); // Add spacing between components

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        frame.add(inputPanel, BorderLayout.CENTER);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {
            List<APUHostelManagement.Manager> managers = APUHostelManagement.Manager.readManagersFromFile("approved_managers.txt");
            for (APUHostelManagement.Manager manager : managers) {
                if (manager.getUsername().equals(username) && manager.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new ManagerMainPageGUI(manager);
                    frame.dispose();
                    return;
                }
            }
            JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while logging in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginPageGUI window = new LoginPageGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}