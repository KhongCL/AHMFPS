package apu.hostel.management;

import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class APUHostelManagement {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        SwingUtilities.invokeLater(() -> {
            JFrame welcomeFrame = new JFrame("Welcome to APU Hostel Management");
            welcomeFrame.setSize(400, 200);
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            welcomeFrame.add(panel);
            placeWelcomeComponents(panel, welcomeFrame);

            welcomeFrame.setVisible(true);
        });
    }

    private static void placeWelcomeComponents(JPanel panel, JFrame welcomeFrame) {
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome to APU Hostel Management");
        welcomeLabel.setBounds(50, 20, 300, 25);
        panel.add(welcomeLabel);

        JButton managerButton = new JButton("Manager");
        managerButton.setBounds(50, 60, 100, 25);
        panel.add(managerButton);

        // Add Home button
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(50, 100, 150, 25);
        panel.add(homeButton);

        // Add action listener for Home button
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Define the action to be performed when Home button is clicked
                JOptionPane.showMessageDialog(welcomeFrame, "Home button clicked!");
            }

        });

        JButton residentButton = new JButton("Resident");
        residentButton.setBounds(150, 60, 100, 25);
        panel.add(residentButton);

        JButton staffButton = new JButton("Staff");
        staffButton.setBounds(250, 60, 100, 25);
        panel.add(staffButton);

        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeFrame.dispose();
                showLoginFrame("Manager");
            }

            
        });

        residentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeFrame.dispose();
                showLoginFrame("Resident");
            }
        });

        staffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeFrame.dispose();
                showLoginFrame("Staff");
            }
        });
    }

    private static void showLoginFrame(String userType) {
        JFrame loginFrame = new JFrame(userType + " Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        loginFrame.add(panel);
        placeLoginComponents(panel, loginFrame, userType);

        loginFrame.setVisible(true);
    }

    private static void placeLoginComponents(JPanel panel, JFrame loginFrame, String userType) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                User user = null;
                try {
                    user = User.loadFromFile(username, password);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(loginFrame, "Error reading user data: " + ex.getMessage());
                }

                if (user == null) {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid login credentials");
                } else {
                    loginFrame.dispose();
                    new ResidentMenu(user).setVisible(true);
                }
            }
        });
    }

    // User abstract class
    public abstract static class User {
        protected String username;
        protected String password;
        protected String role;

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public abstract void displayMenu();

        public void saveToFile() throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                writer.write(username + "," + password + "," + role);
                writer.newLine();
            }
        }

        public static User loadFromFile(String username, String password) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        switch (parts[2]) {
                            case "Manager":
                                return new Manager(parts[0], parts[1]);
                            case "Staff":
                                return new Staff(parts[0], parts[1]);
                            case "Resident":
                                return new Resident(parts[0], parts[1]);
                        }
                    }
                }
            }
            return null;
        }
    }
    
    //User Menu class
    public static class UserMenu extends JFrame {
        public UserMenu(User user) {
            setTitle("User Menu");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Add components to the UserMenu
            JLabel welcomeLabel = new JLabel("Welcome, " + user.username);
            add(welcomeLabel);
        }
    }

    // Manager class
    public static class Manager extends User {

        public Manager(String username, String password) {
            super(username, password, "Manager");
        }

        @Override
        public void displayMenu() {
            System.out.println("Manager Menu:");
            System.out.println("1. Approve User Registration");
            System.out.println("2. Search, Update, Delete User Accounts");
            System.out.println("3. Fix/Update Rate");
            // Add more options as needed
        }

        // Implement methods for manager functionalities
    }

    // Staff class
    public static class Staff extends User {

        public Staff(String username, String password) {
            super(username, password, "Staff");
        }

        @Override
        public void displayMenu() {
            System.out.println("Staff Menu:");
            System.out.println("1. Register Individual Login Account");
            System.out.println("2. Update Individual Login Account");
            System.out.println("3. Make Payment for Resident");
            System.out.println("4. Generate Receipt");
            // Add more options as needed
        }

        // Implement methods for staff functionalities
    }

    // Resident class
    public static class Resident extends User {

        public Resident(String username, String password) {
            super(username, password, "Resident");
        }

        @Override
        public void displayMenu() {
            System.out.println("Resident Menu:");
            System.out.println("1. Register Individual Login Account");
            System.out.println("2. Update Individual Login Account");
            System.out.println("3. View Payment Records");
            // Add more options as needed
        }

        // Implement methods for resident functionalities
    }

    // ManagerMenu class
    public static class ManagerMenu extends JFrame {

        private User user;

        public ManagerMenu(User user) {
            this.user = user;
            setTitle("Manager Menu");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            add(panel);
            JLabel welcomeLabel = new JLabel("Welcome, " + user.username);
            add(welcomeLabel);
            placeComponents(panel);
        }

        private void placeComponents(JPanel panel) {
            panel.setLayout(null);

            JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
            welcomeLabel.setBounds(10, 20, 300, 25);
            panel.add(welcomeLabel);

            JButton approveRegistrationButton = new JButton("Approve User Registration");
            approveRegistrationButton.setBounds(10, 60, 200, 25);
            panel.add(approveRegistrationButton);

            JButton manageAccountsButton = new JButton("Manage User Accounts");
            manageAccountsButton.setBounds(10, 100, 200, 25);
            panel.add(manageAccountsButton);

            JButton updateRateButton = new JButton("Fix/Update Rate");
            updateRateButton.setBounds(10, 140, 200, 25);
            panel.add(updateRateButton);

            // Add action listeners for buttons
            approveRegistrationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    approveUserRegistration();
                }
            });

            manageAccountsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manageUserAccounts();
                }
            });

            updateRateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fixOrUpdateRate();
                }
            });
        }

        private void approveUserRegistration() {
            // Implement the logic to approve user registration
            // This could involve reading pending registrations from a file and approving them
            JOptionPane.showMessageDialog(this, "Approve User Registration functionality not implemented yet.");
        }

        private void manageUserAccounts() {
            // Implement the logic to search, update, and delete user accounts
            // This could involve reading user data from a file and allowing the manager to modify it
            JOptionPane.showMessageDialog(this, "Manage User Accounts functionality not implemented yet.");
        }

        private void fixOrUpdateRate() {
            // Implement the logic to fix or update the rate
            // This could involve updating a rate value in a configuration file
            JOptionPane.showMessageDialog(this, "Fix/Update Rate functionality not implemented yet.");
        }
    }

    // StaffMenu class
    public static class StaffMenu extends JFrame {
        private User user; // Declare an instance variable for user

        public StaffMenu(User user) {
            this.user = user; // Initialize the instance variable
            setTitle("Staff Menu");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);
        }

        private void placeComponents(JPanel panel) {
            panel.setLayout(null);

            JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
            welcomeLabel.setBounds(10, 20, 300, 25);
            panel.add(welcomeLabel);

            JButton registerAccountButton = new JButton("Register Login Account");
            registerAccountButton.setBounds(10, 60, 200, 25);
            panel.add(registerAccountButton);

            JButton updateAccountButton = new JButton("Update Login Account");
            updateAccountButton.setBounds(10, 100, 200, 25);
            panel.add(updateAccountButton);

            JButton makePaymentButton = new JButton("Make Payment for Resident");
            makePaymentButton.setBounds(10, 140, 200, 25);
            panel.add(makePaymentButton);

            JButton generateReceiptButton = new JButton("Generate Receipt");
            generateReceiptButton.setBounds(10, 180, 200, 25);
            panel.add(generateReceiptButton);

            // Add action listeners for buttons
            registerAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement register account logic here
                }
            });

            updateAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement update account logic here
                }
            });

            makePaymentButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement make payment logic here
                }
            });

            generateReceiptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement generate receipt logic here
                }
            });
        }
    }

    // ResidentMenu class
    public static class ResidentMenu extends JFrame {
        private User user; // Declare an instance variable for user

        public ResidentMenu(User user) {
            this.user = user; // Initialize the instance variable
            setTitle("Resident Menu");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);
        }

        private void placeComponents(JPanel panel) {
            panel.setLayout(null);

            JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
            welcomeLabel.setBounds(10, 20, 300, 25);
            panel.add(welcomeLabel);

            JButton registerAccountButton = new JButton("Register Login Account");
            registerAccountButton.setBounds(10, 60, 200, 25);
            panel.add(registerAccountButton);

            JButton updateAccountButton = new JButton("Update Login Account");
            updateAccountButton.setBounds(10, 100, 200, 25);
            panel.add(updateAccountButton);

            JButton viewPaymentRecordsButton = new JButton("View Payment Records");
            viewPaymentRecordsButton.setBounds(10, 140, 200, 25);
            panel.add(viewPaymentRecordsButton);

            // Add action listeners for buttons
            registerAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement register account logic here
                }
            });

            updateAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement update account logic here
                }
            });

            viewPaymentRecordsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement view payment records logic here
                }
            });
        }
    }

    // LoginScreen class
    public static class LoginScreen extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleComboBox;
        private JButton loginButton;
        private JButton registerButton;
        private JButton homeButton;

        public LoginScreen() {
            setTitle("Login/Register");
            setSize(300, 250);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String role = (String) roleComboBox.getSelectedItem();

                    // Implement authentication logic here
                    User user = null;
                    if (role.equals("Manager")) {
                        user = new Manager(username, password);
                        new ManagerMenu(user).setVisible(true);
                    } else if (role.equals("Staff")) {
                        user = new Staff(username, password);
                        new UserMenu(user).setVisible(true);
                    } else if (role.equals("Resident")) {
                        user = new Resident(username, password);
                        new UserMenu(user).setVisible(true);
                    }

                    if (user != null) {
                        dispose();
                    }
                }
            });

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String role = (String) roleComboBox.getSelectedItem();

                    // Implement registration logic here
                    if (!role.equals("Manager")) {
                        // Staff and Resident registration logic
                        User user = null;
                        if (role.equals("Staff")) {
                            user = new Staff(username, password);
                        } else if (role.equals("Resident")) {
                            user = new Resident(username, password);
                        }

                        if (user != null) {
                            try {
                                user.saveToFile();
                                JOptionPane.showMessageDialog(LoginScreen.this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(LoginScreen.this, "Error saving user", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginScreen.this, "Managers cannot register", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });


            homeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    // Logic to show the home page
                }
            });
        }

        private void placeComponents(JPanel panel) {
            panel.setLayout(null);

            JLabel userLabel = new JLabel("User:");
            userLabel.setBounds(10, 20, 80, 25);
            panel.add(userLabel);

            usernameField = new JTextField(20);
            usernameField.setBounds(100, 20, 165, 25);
            panel.add(usernameField);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBounds(10, 50, 80, 25);
            panel.add(passwordLabel);

            passwordField = new JPasswordField(20);
            passwordField.setBounds(100, 50, 165, 25);
            panel.add(passwordField);

            JLabel roleLabel = new JLabel("Role:");
            roleLabel.setBounds(10, 80, 80, 25);
            panel.add(roleLabel);

            String[] roles = { "Manager", "Staff", "Resident" };
            roleComboBox = new JComboBox<>(roles);
            roleComboBox.setBounds(100, 80, 165, 25);
            panel.add(roleComboBox);

            loginButton = new JButton("Login");
            loginButton.setBounds(10, 110, 80, 25);
            panel.add(loginButton);

            registerButton = new JButton("Register");
            registerButton.setBounds(100, 110, 100, 25);
            panel.add(registerButton);

            homeButton = new JButton("Home");
            homeButton.setBounds(10, 140, 80, 25);
            panel.add(homeButton);
        }
    }
}
//gcgfdctfydtyttfc

//Kynax nigga