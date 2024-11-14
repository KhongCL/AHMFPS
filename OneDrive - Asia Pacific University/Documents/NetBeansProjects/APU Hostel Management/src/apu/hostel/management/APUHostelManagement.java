package apu.hostel.management;
import java.io.*;
import java.util.*;

public class APUHostelManagement {
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

    // Manager class
    public static class Manager extends User {
        public Manager(String username, String password) {
            super(username, password, "Manager");
        }

        @Override
        public void displayMenu() {
            // Manager-specific menu implementation
        }

        public void approveUserRegistration(User user) {
            // Approve user registration logic
        }

        public void searchUser(String username) {
            // Search user logic
        }

        public void updateUser(User user) {
            // Update user logic
        }

        public void deleteUser(String username) {
            // Delete user logic
        }

        public void fixOrUpdateRate(double rate) {
            // Fix or update rate logic
        }
    }

    // Staff class
    public static class Staff extends User {
        public Staff(String username, String password) {
            super(username, password, "Staff");
        }

        @Override
        public void displayMenu() {
            // Staff-specific menu implementation
        }

        public void registerUser(User user) {
            // Register user logic
        }

        public void updateUser(User user) {
            // Update user logic
        }

        public void makePayment(Resident resident, double amount) {
            // Make payment logic
        }

        public void generateReceipt(Resident resident, double amount) {
            // Generate receipt logic
        }
    }

    // Resident class
    public static class Resident extends User {
        public Resident(String username, String password) {
            super(username, password, "Resident");
        }

        @Override
        public void displayMenu() {
            // Resident-specific menu implementation
        }

        public void updateDetails() {
            // Update details logic
        }

        public void viewPaymentRecords() {
            // View payment records logic
        }
    }

    // Payment class
    public static class Payment {
        private String username;
        private double amount;
        private Date date;

        public Payment(String username, double amount, Date date) {
            this.username = username;
            this.amount = amount;
            this.date = date;
        }

        public void saveToFile() throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt", true))) {
                writer.write(username + "," + amount + "," + date);
                writer.newLine();
            }
        }

        public static List<Payment> loadFromFile(String username) throws IOException {
            List<Payment> payments = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(username)) {
                        payments.add(new Payment(parts[0], Double.parseDouble(parts[1]), new Date(parts[2])));
                    }
                }
            }
            return payments;
        }
    }

    // Main method to test the backend functionality
    public static void main(String[] args) {
        try {
            // Register a new Staff user
            User staff = new Staff("staff1", "password1");
            staff.saveToFile();

            // Register a new Resident user
            User resident = new Resident("resident1", "password1");
            resident.saveToFile();

            // Attempt to login as the Staff user
            User loggedInUser = User.loadFromFile("staff1", "password1");
            if (loggedInUser != null) {
                loggedInUser.displayMenu();
            }

            // Attempt to login as the Resident user
            loggedInUser = User.loadFromFile("resident1", "password1");
            if (loggedInUser != null) {
                loggedInUser.displayMenu();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}