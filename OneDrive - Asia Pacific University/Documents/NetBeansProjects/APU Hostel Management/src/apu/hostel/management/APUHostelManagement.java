package apu.hostel.management;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class APUHostelManagement {
    // User abstract class
    public abstract static class User {
        protected String userID;
        protected String icPassportNumber;
        protected String username;
        protected String password;
        protected String contactNumber;
        protected String dateOfRegistration;
        protected String role;
        protected boolean approved;
        

        public User(String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role) {
            this.userID = userID;
            this.icPassportNumber = icPassportNumber;
            this.username = username;
            this.password = password;
            this.contactNumber = contactNumber;
            this.dateOfRegistration = dateOfRegistration;
            this.role = role;
            this.approved = false;
        }

        public String getUserID() {
            return userID;
        }

        public String getIcPassportNumber() {
            return icPassportNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public String getDateOfRegistration() {
            return dateOfRegistration;
        }

        public String getRole() {
            return role;
        }

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }

        public abstract void displayMenu();

        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(userID + "," + icPassportNumber + "," + username + "," + password + "," + contactNumber + "," + dateOfRegistration + "," + role + "," + approved);
                writer.newLine();
            }
        }

        public static List<User> readFromFile(String filename) throws IOException {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 8) {
                        User user = null;
                        switch (parts[6]) {
                            case "Manager":
                                user = new Manager(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                            case "Staff":
                                user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                            case "Resident":
                                user = new Resident(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                        }
                        if (user != null) {
                            user.setApproved(Boolean.parseBoolean(parts[7]));
                            users.add(user);
                        }
                    }
                }
            }
            return users;
        }

        public static User findUser(String username, String password, String filename) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 8 && parts[2].equals(username) && parts[3].equals(password)) {
                        User user = null;
                        switch (parts[6]) {
                            case "Manager":
                                user = new Manager(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                            case "Staff":
                                user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                            case "Resident":
                                user = new Resident(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                                break;
                        }
                        if (user != null) {
                            user.setApproved(Boolean.parseBoolean(parts[7]));
                        }
                        return user;
                    }
                }
            }
            return null;
        }

        public static List<User> loadAllUsers() throws IOException {
            return readFromFile("users.txt");
        }
    }

    // Manager class
    public static class Manager extends User {
        private String managerID;

        public Manager(String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration) {
            super(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "Manager");
            this.managerID = "M" + userID.substring(1);
        }

        public String getManagerID() {
            return managerID;
        }

        @Override
        public void displayMenu() {
            // Manager-specific menu implementation
           
            System.out.println("Manager Menu:");
            System.out.println("1. Approve User Registration");
            System.out.println("2. Search User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Fix/Update Rate");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Approve User Registration logic
                    approveUserRegistration();
                    break;
                case 2:
                    // Search User logic
                    break;
                case 3:
                    // Update User logic
                    break;
                case 4:
                    // Delete User logic
                    break;
                case 5:
                    // Fix/Update Rate logic
                    break;
                case 6:
                    System.out.println("Logging out...");
                    
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    displayMenu(); // Recursively call to retry
                    break;
            }
        }

        private void approveUserRegistration() {
            try {
                List<User> unapprovedStaffs = User.readFromFile("unapproved_staffs.txt");
                List<User> unapprovedResidents = User.readFromFile("unapproved_residents.txt");

                if (unapprovedStaffs.isEmpty() && unapprovedResidents.isEmpty()) {
                    System.out.println("No users to approve.");
                    return;
                }

                System.out.println("Unapproved Staffs:");
                for (int i = 0; i < unapprovedStaffs.size(); i++) {
                    User user = unapprovedStaffs.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + " (" + user.getRole() + ")");
                }

                System.out.println("Unapproved Residents:");
                for (int i = 0; i < unapprovedResidents.size(); i++) {
                    User user = unapprovedResidents.get(i);
                    System.out.println((i + 1 + unapprovedStaffs.size()) + ". " + user.getUsername() + " (" + user.getRole() + ")");
                }

                System.out.print("Enter the number of the user to approve: ");
                Scanner scanner = new Scanner(System.in);
                int userIndex = scanner.nextInt() - 1;
                scanner.nextLine(); // Consume newline

                if (userIndex >= 0 && userIndex < unapprovedStaffs.size()) {
                    User userToApprove = unapprovedStaffs.get(userIndex);
                    userToApprove.setApproved(true);
                    userToApprove.saveToFile("approved_staffs.txt");
                    unapprovedStaffs.remove(userIndex);
                    saveUnapprovedUsers(unapprovedStaffs, "unapproved_staffs.txt");
                    System.out.println("Staff approved successfully.");
                } else if (userIndex >= unapprovedStaffs.size() && userIndex < unapprovedStaffs.size() + unapprovedResidents.size()) {
                    User userToApprove = unapprovedResidents.get(userIndex - unapprovedStaffs.size());
                    userToApprove.setApproved(true);
                    userToApprove.saveToFile("approved_residents.txt");
                    unapprovedResidents.remove(userIndex - unapprovedStaffs.size());
                    saveUnapprovedUsers(unapprovedResidents, "unapproved_residents.txt");
                    System.out.println("Resident approved successfully.");
                } else {
                    System.out.println("Invalid user number.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while approving the user.");
                e.printStackTrace();
            }
        }

        private void saveUnapprovedUsers(List<User> users, String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    writer.write(user.getUserID() + "," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.isApproved());
                    writer.newLine();
                }
            }
        }
        

        public void searchUser(String username) {
            // Search user logic
        }

        public void updateUser(String username) {
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
        private String staffID;
        private String dateOfApproval;

        public Staff(String userID, String icPassportNumber, String username, String password, String contactNumber) {
            super(userID, icPassportNumber, username, password, contactNumber, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "Staff");
            this.staffID = "S" + userID.substring(1);
        }

        public String getStaffID() {
            return staffID;
        }

        public String getDateOfApproval() {
            return dateOfApproval;
        }

        public void setDateOfApproval(String dateOfApproval) {
            this.dateOfApproval = dateOfApproval;
        }

        @Override
        public void displayMenu() {
            System.out.println("Staff Menu:");
            System.out.println("1. Update Individual Login Account");
            System.out.println("2. Make Payment for Resident");
            System.out.println("3. Generate Receipt");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
    
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
    
            switch (choice) {
                case 1:
                    // Register Individual Login Account logic
                    break;
                case 2:
                    // Update Individual Login Account logic
                    break;
                case 3:
                    // Make Payment for Resident logic
                    break;
                case 4:
                    // Generate Receipt logic
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
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
        private String residentID;
        private String dateOfApproval;

        public Resident(String userID, String icPassportNumber, String username, String password, String contactNumber) {
            super(userID, icPassportNumber, username, password, contactNumber, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "Resident");
            this.residentID = "R" + userID.substring(1);
        }

        public String getResidentID() {
            return residentID;
        }

        public String getDateOfApproval() {
            return dateOfApproval;
        }

        public void setDateOfApproval(String dateOfApproval) {
            this.dateOfApproval = dateOfApproval;
        }

        @Override
        public void displayMenu() {
            // Resident-specific menu implementation
            System.out.println("Resident Menu:");
            System.out.println("1. Update Individual Login Account");
            System.out.println("2. View Payment Records");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Register Individual Login Account logic
                    break;
                case 2:
                    // Update Individual Login Account logic
                    break;
                case 3:
                    // View Payment Records logic
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            
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
        private String paymentID;
        private String residentID;
        private String staffID;
        private double amount;
        private String paymentDate;
        private String receiptNumber;
        private String roomNumber;
        private String paymentMethod;

        public Payment(String paymentID, String residentID, String staffID, double amount, String paymentDate, String receiptNumber, String roomNumber, String paymentMethod) {
            this.paymentID = paymentID;
            this.residentID = residentID;
            this.staffID = staffID;
            this.amount = amount;
            this.paymentDate = paymentDate;
            this.receiptNumber = receiptNumber;
            this.roomNumber = roomNumber;
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentID() {
            return paymentID;
        }

        public String getResidentID() {
            return residentID;
        }

        public String getStaffID() {
            return staffID;
        }

        public double getAmount() {
            return amount;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public String getReceiptNumber() {
            return receiptNumber;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(paymentID + "," + residentID + "," + staffID + "," + amount + "," + paymentDate + "," + receiptNumber + "," + roomNumber + "," + paymentMethod);
                writer.newLine();
            }
        }

        public static List<Payment> readFromFile(String filename) throws IOException {
            List<Payment> payments = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 8) {
                        Payment payment = new Payment(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4], parts[5], parts[6], parts[7]);
                        payments.add(payment);
                    }
                }
            }
            return payments;
        }
    }

    // FeeRate class
    public static class FeeRate {
        private String feeRateID;
        private String roomType;
        private double monthlyRate;
        private String effectiveDate;

        public FeeRate(String feeRateID, String roomType, double monthlyRate, String effectiveDate) {
            this.feeRateID = feeRateID;
            this.roomType = roomType;
            this.monthlyRate = monthlyRate;
            this.effectiveDate = effectiveDate;
        }

        public String getFeeRateID() {
            return feeRateID;
        }

        public String getRoomType() {
            return roomType;
        }

        public double getMonthlyRate() {
            return monthlyRate;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(feeRateID + "," + roomType + "," + monthlyRate + "," + effectiveDate);
                writer.newLine();
            }
        }

        public static List<FeeRate> readFromFile(String filename) throws IOException {
            List<FeeRate> feeRates = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        FeeRate feeRate = new FeeRate(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                        feeRates.add(feeRate);
                    }
                }
            }
            return feeRates;
        }
    }



    // Method to display the welcome page
    public static void displayWelcomePage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to APU Hostel Management Fees Payment System (AHMFPS)");
        System.out.println("Please choose your role:");
        System.out.println("1. Manager");
        System.out.println("2. Staff");
        System.out.println("3. Resident");
        System.out.print("Enter your choice (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.println("You have chosen Manager.");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int managerChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (managerChoice == 1) {
                    registerManager();
                } else if (managerChoice == 2) {
                    loginManager();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    displayWelcomePage(); // Recursively call to retry
                }
                break;
            case 2:
                System.out.println("You have chosen Staff.");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int staffChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (staffChoice == 1) {
                    registerStaff();
                } else if (staffChoice == 2) {
                    loginStaff();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    displayWelcomePage(); // Recursively call to retry
                }
                    break;
            case 3:
                System.out.println("You have chosen Resident.");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int residentChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (residentChoice == 1) {
                    registerResident();
                } else if (residentChoice == 2) {
                    loginResident();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    displayWelcomePage(); // Recursively call to retry
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                displayWelcomePage(); // Recursively call to retry
                break;
        }
            
    }

    // Method to handle Manager registration
    public static void registerManager() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IC/Passport Number: ");
        String icPassportNumber = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter date of registration: ");
        String dateOfRegistration = scanner.nextLine();

        try {
            String userID = generateUserID("U");
            User manager = new Manager(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration);
            manager.saveToFile("managers.txt");
            System.out.println("Manager registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Manager login
    public static void loginManager() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = User.findUser(username, password, "managers.txt");
            if (user != null && user.getRole().equals("Manager")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                loginManager(); // Retry login
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Staff registration
    public static void registerStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IC/Passport Number: ");
        String icPassportNumber = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter date of registration: ");
        String dateOfRegistration = scanner.nextLine();

        try {
            String userID = generateUserID("U");
            User staff = new Staff(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration);
            staff.saveToFile("unapproved_staffs.txt");
            System.out.println("Staff registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Staff login
    public static void loginStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = User.findUser(username, password, "approved_staffs.txt");
            if (user != null && user.getRole().equals("Staff")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                loginStaff(); // Retry login
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Resident registration
    public static void registerResident() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IC/Passport Number: ");
        String icPassportNumber = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        

        try {
            String userID = generateUserID("U");
            String residentID = generateUserID("R");
            Resident resident = new Resident(userID, icPassportNumber, username, password, contactNumber);
            resident.saveToFile("unapproved_residents.txt");
            System.out.println("Resident registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Resident login
    public static void loginResident() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = User.findUser(username, password, "approved_residents.txt");
            if (user != null && user.getRole().equals("Resident")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                loginResident(); // Retry login
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to generate unique IDs with a prefix
    private static String generateUserID(String prefix) {
        int id = 1;
        String filename = prefix.equals("R") ? "unapproved_residents.txt" : "users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].startsWith(prefix)) {
                    int currentId = Integer.parseInt(parts[0].substring(1));
                    if (currentId >= id) {
                        id = currentId + 1;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prefix + String.format("%02d", id);
    }

        // Main method to launch the application
    public static void main(String[] args) {
        displayWelcomePage();
    }
}
