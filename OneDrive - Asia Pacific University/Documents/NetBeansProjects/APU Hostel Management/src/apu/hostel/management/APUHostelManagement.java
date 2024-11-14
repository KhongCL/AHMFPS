package apu.hostel.management;
import java.io.*;

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
    }

    // Implement core functionality for user registration, login, and authentication
    public static User registerUser(String username, String password, String role) throws IOException {
        if (!role.equals("Manager")) {
            User user = null;
            if (role.equals("Staff")) {
                user = new Staff(username, password);
            } else if (role.equals("Resident")) {
                user = new Resident(username, password);
            }

            if (user != null) {
                user.saveToFile();
                System.out.println("Registration successful");
                return user;
            }
        } else {
            System.out.println("Managers cannot register");
        }
        return null;
    }

    public static User loginUser(String username, String password) throws IOException {
        User user = User.loadFromFile(username, password);
        if (user != null) {
            System.out.println("Login successful");
            return user;
        } else {
            System.out.println("Invalid username or password");
        }
        return null;
    }

    // Main method to test the backend functionality
    public static void main(String[] args) {
        try {
            // Register a new Staff user
            User staff = registerUser("staff1", "password1", "Staff");

            // Register a new Resident user
            User resident = registerUser("resident1", "password1", "Resident");

            // Attempt to login as the Staff user
            User loggedInUser = loginUser("staff1", "password1");
            if (loggedInUser != null) {
                loggedInUser.displayMenu();
            }

            // Attempt to login as the Resident user
            loggedInUser = loginUser("resident1", "password1");
            if (loggedInUser != null) {
                loggedInUser.displayMenu();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}