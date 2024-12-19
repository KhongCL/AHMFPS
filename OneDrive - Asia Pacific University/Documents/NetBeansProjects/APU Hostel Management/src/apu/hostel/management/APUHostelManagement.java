package apu.hostel.management;
import java.io.*;
import java.util.*;

import apu.hostel.management.APUHostelManagement.Manager;
import apu.hostel.management.APUHostelManagement.Resident;
import apu.hostel.management.APUHostelManagement.Staff;
import apu.hostel.management.APUHostelManagement.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
        protected boolean isActive;
        

        public User(String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role, boolean isActive) {
            this.userID = userID;
            this.icPassportNumber = icPassportNumber;
            this.username = username;
            this.password = password;
            this.contactNumber = contactNumber;
            this.dateOfRegistration = dateOfRegistration;
            this.role = role;
            this.isActive = isActive;
        }

        public void setUserID(String userID) {
            this.userID = userID;
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

        public boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(boolean isActive) {
            this.isActive = isActive;
        }

        public abstract void displayMenu();

        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(userID + "," + icPassportNumber + "," + username + "," + password + "," + contactNumber + "," + dateOfRegistration + "," + role + "," + isActive);
                writer.newLine();
            }
        }

        // Method to read users from file
        public static List<User> readFromFile(String filename) throws IOException {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    User user = null;
                    if (parts.length == 8) {
                        switch (parts[7]) {
                            case "Manager":
                                user = new Manager(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]));
                                break;
                            case "Staff":
                                user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7],Boolean.parseBoolean(parts[8]), parts[9]);
                                break;
                            case "Resident":
                                user = new Resident(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]), parts[9]);
                                break;
                        }
                    } else if (parts.length == 9 && "Manager".equals(parts[7])) {
                        user = new Manager(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]));
                    } else if (parts.length == 10) {
                        if ("Staff".equals(parts[7])) {
                            user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]), parts[9]);
                        } else if ("Resident".equals(parts[7])) {
                            user = new Resident(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]), parts[9]);
                        }
                    }
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
            return users;
        }

        // Method to find user by username and password
        public static User findUser(String username, String password, String filename) throws IOException {
            List<User> users = User.readFromFile(filename);
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
            return null;
        }

        // Method to check if IC/Passport Number, Username, or Contact Number is unique
        public static boolean isUnique(String icPassportNumber, String username, String contactNumber) throws IOException {
            List<User> users = new ArrayList<>();
            users.addAll(User.readFromFile("users.txt"));
            users.addAll(User.readFromFile("unapproved_residents.txt"));
            users.addAll(User.readFromFile("approved_residents.txt"));
            users.addAll(User.readFromFile("unapproved_staffs.txt"));
            users.addAll(User.readFromFile("approved_staffs.txt"));
            users.addAll(User.readFromFile("managers.txt"));
        
            for (User user : users) {
                if ((icPassportNumber != null && !icPassportNumber.isEmpty() && user.getIcPassportNumber().equals(icPassportNumber)) ||
                    (username != null && !username.isEmpty() && user.getUsername().equals(username)) ||
                    (contactNumber != null && !contactNumber.isEmpty() && user.getContactNumber().equals(contactNumber))) {
                    return false;
                }
            }
            return true;
        }
    }

    // Manager class
    public static class Manager extends User {
        private String managerID;

        public Manager(String managerID, String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role, boolean isActive) {
            super(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, role, isActive);
            this.managerID = managerID;
        }

        public String getManagerID() {
            return managerID;
        }

        public void setManagerID(String managerID) {
            this.managerID = managerID;
        }

        
        public void saveToManagerFile() throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("managers.txt", true))) {
                writer.write(managerID + "," + userID + "," + icPassportNumber + "," + username + "," + password + "," + contactNumber + "," + dateOfRegistration + "," + role + "," + isActive);
                writer.newLine();
            }
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
                    displayWelcomePage();
                    
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
                    displayMenu();
                    return;
                }
        
                System.out.println("Unapproved Staffs:");
                for (int i = 0; i < unapprovedStaffs.size(); i++) {
                    User user = unapprovedStaffs.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + " (" + user.getIcPassportNumber() + ")");
                }
        
                System.out.println("Unapproved Residents:");
                for (int i = 0; i < unapprovedResidents.size(); i++) {
                    User user = unapprovedResidents.get(i);
                    System.out.println((i + 1 + unapprovedStaffs.size()) + ". " + user.getUsername() + " (" + user.getIcPassportNumber() + ")");
                }
        
                System.out.print("Enter the number of the user to approve: ");
                Scanner scanner = new Scanner(System.in);
                int userIndex = scanner.nextInt() - 1;
                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                scanner.nextLine(); // Consume newline
        
                if (userIndex >= 0 && userIndex < unapprovedStaffs.size()) {
                    Staff staffToApprove = (Staff) unapprovedStaffs.get(userIndex);
                    String userID = generateUserID("U");
                    String staffID = generateUserID("S");
                    staffToApprove.setUserID(userID);
                    staffToApprove.setStaffID(staffID);
                    staffToApprove.setDateOfApproval(currentDate);
                    staffToApprove.setIsActive(true);
                    staffToApprove.saveToFile("users.txt");
                    staffToApprove.saveToStaffFile(staffID, userID, "approved_staffs.txt");
                    unapprovedStaffs.remove(userIndex);
                    saveUnapprovedUsers(unapprovedStaffs, "unapproved_staffs.txt");
                    System.out.println("Staff approved successfully.");
                } else if (userIndex >= unapprovedStaffs.size() && userIndex < unapprovedStaffs.size() + unapprovedResidents.size()) {
                    Resident residentToApprove = (Resident) unapprovedResidents.get(userIndex - unapprovedStaffs.size());
                    String userID = generateUserID("U");
                    String residentID = generateUserID("R");
                    residentToApprove.setUserID(userID);
                    residentToApprove.setResidentID(residentID);
                    residentToApprove.setDateOfApproval(currentDate);
                    residentToApprove.setIsActive(true);
                    residentToApprove.saveToFile("users.txt");
                    residentToApprove.saveToResidentFile(residentID, userID, "approved_residents.txt");
                    unapprovedResidents.remove(userIndex - unapprovedStaffs.size());
                    saveUnapprovedUsers(unapprovedResidents, "unapproved_residents.txt");
                    System.out.println("Resident approved successfully.");
                } else {
                    System.out.println("Invalid user number.");
                }
                displayMenu();
            } catch (IOException e) {
                System.out.println("An error occurred while approving the user.");
                e.printStackTrace();
            }
        }

        // Method to save unapproved users to file
        private void saveUnapprovedUsers(List<User> users, String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    if (user instanceof Staff) {
                        Staff staff = (Staff) user;
                        staff.setIsActive(true);
                        writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + null);
                    } else if (user instanceof Resident) {
                        Resident resident = (Resident) user;
                        resident.setIsActive(true);
                        writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + null);
                    }
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

        public Staff(String staffID, String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role, boolean isActive, String dateOfApproval) {
            super(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, role, isActive);
            this.staffID = staffID;
            this.dateOfApproval = dateOfApproval;
        }

        public String getStaffID() {
            return staffID;
        }

        public void setStaffID(String staffID) {
            this.staffID = staffID;
        }

        public String getDateOfApproval() {
            return dateOfApproval;
        }

        public void setDateOfApproval(String dateOfApproval) {
            this.dateOfApproval = dateOfApproval;
        }

        public void saveToStaffFile(String staffID, String userID, String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(staffID + "," + userID + "," + icPassportNumber + "," + username + "," + password + "," + contactNumber + "," + dateOfRegistration + "," + role + "," + isActive + "," + dateOfApproval);
                writer.newLine();
            }
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
                    updatePersonalInformation();
                    
                    break;
                case 2:
                    // Make Payment for Resident logic
                    break;
                case 3:
                    // Generate Receipt logic
                    break;
                case 4:
                    System.out.println("Logging out...");
                    System.out.println("You have been logged out successfully.");
                    displayWelcomePage();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        public void updatePersonalInformation() {
            Scanner scanner = new Scanner(System.in);
            int choice;
    
            do {
                System.out.println("Update Personal Information:");
                System.out.println("1. Update Username");
                System.out.println("2. Update Password");
                System.out.println("3. Update Contact Number");
                System.out.println("0. Go Back to Staff Menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        System.out.print("Enter new username: ");
                        String newUsername = scanner.nextLine();
                        if (newUsername.isEmpty()) {
                            System.out.println("Username cannot be empty. Please try again.");
                            break;
                        }
                        try {
                            if (!User.isUnique("", newUsername, "")) {
                                System.out.println("Error: Username already exists.");
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        this.username = newUsername;
                        System.out.println("Username updated successfully.");
                        break;
                    case 2:
                        System.out.print("Enter new password: ");
                        String newPassword = scanner.nextLine();
                        if (newPassword.isEmpty()) {
                            System.out.println("Password cannot be empty. Please try again.");
                            break;
                        }
                        this.password = newPassword;
                        System.out.println("Password updated successfully.");
                        break;
                    case 3:
                        System.out.print("Enter new contact number: ");
                        String newContactNumber = scanner.nextLine();
                        if (newContactNumber.isEmpty()) {
                            System.out.println("Contact number cannot be empty. Please try again.");
                            break;
                        }
                        try {
                            if (!User.isUnique("", "", newContactNumber)) {
                                System.out.println("Error: Contact Number already exists.");
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        this.contactNumber = newContactNumber;
                        System.out.println("Contact number updated successfully.");
                        break;
                    case 0:
                        System.out.println("Returning to Staff Menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
    
                try {
                    updateFile("approved_staffs.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
            displayMenu(); 
        }
    
        private void updateFile(String filename) throws IOException {
            List<User> users = User.readFromFile(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    if (user.getUserID().equals(this.userID)) {
                        
                        if (user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + resident.getDateOfApproval());
                        }
                    } else {
                        if (user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + resident.getDateOfApproval());
                        }
                    }
                    writer.newLine();
                }
            }
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

        public Resident(String residentID, String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role, boolean isActive, String dateOfApproval) {
            super(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, role, isActive);
            this.residentID = residentID;
            this.dateOfApproval = dateOfApproval;
        }

        public String getResidentID() {
            return residentID;
        }

        public void setResidentID(String residentID) {
            this.residentID = residentID;
        }

        public String getDateOfApproval() {
            return dateOfApproval;
        }

        public void setDateOfApproval(String dateOfApproval) {
            this.dateOfApproval = dateOfApproval;
        }

        public void saveToResidentFile(String residentID, String userID, String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(residentID + "," + userID + "," + icPassportNumber + "," + username + "," + password + "," + contactNumber + "," + dateOfRegistration + "," + role + "," + isActive + "," + dateOfApproval);
                writer.newLine();
            }
        }

        @Override
        public void displayMenu() {
            // Resident-specific menu implementation
            System.out.println("Resident Menu:");
            System.out.println("1. Update Individual Information");
            System.out.println("2. View Payment Records");
            System.out.println("3. Manage Bookings");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    updatePersonalInformation();
                    break;
                case 2:
                    viewPaymentRecords();
                    break;
                case 3:
                    manageBookings();
                    break;
                case 4:
                    residentLogout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    displayMenu(); // Recursively call to retry
                    break;
            }
        }

        public void updatePersonalInformation() {
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("Update Personal Information:");
                System.out.println("1. Update Username");
                System.out.println("2. Update Password");
                System.out.println("3. Update Contact Number");
                System.out.println("0. Go Back to Resident Menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter new username: ");
                        String newUsername = scanner.nextLine();
                        if (newUsername.isEmpty()) {
                            System.out.println("Username cannot be empty. Please try again.");
                            break;
                        }
                        try {
                            if (!User.isUnique("", newUsername, "")) {
                                System.out.println("Error: Username already exists.");
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        this.username = newUsername;
                        System.out.println("Username updated successfully.");
                        break;
                    case 2:
                        System.out.print("Enter new password: ");
                        String newPassword = scanner.nextLine();
                        if (newPassword.isEmpty()) {
                            System.out.println("Password cannot be empty. Please try again.");
                            break;
                        }
                        this.password = newPassword;
                        System.out.println("Password updated successfully.");
                        break;
                    case 3:
                        System.out.print("Enter new contact number: ");
                        String newContactNumber = scanner.nextLine();
                        if (newContactNumber.isEmpty()) {
                            System.out.println("Contact number cannot be empty. Please try again.");
                            break;
                        }
                        try {
                            if (!User.isUnique("", "", newContactNumber)) {
                                System.out.println("Error: Contact Number already exists.");
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        this.contactNumber = newContactNumber;
                        System.out.println("Contact number updated successfully.");
                        break;
                    case 0:
                        System.out.println("Returning to Resident Menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                try {
                    updateFile("approved_residents.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
            displayMenu();
        }

        private void updateFile(String filename) throws IOException {
            List<User> users = User.readFromFile(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    if (user.getUserID().equals(this.userID)) {
                        if (user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + resident.getDateOfApproval());
                        }
                    } else {
                        if (user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + resident.getDateOfApproval());
                        }
                    }
                    writer.newLine();
                }
            }
        }
    

        public void viewPaymentRecords() {
            System.out.println("Payment Records:");
            String userID = this.getUserID(); // Assuming there's a method to get the current user's ID

            // Read room data from rooms.txt and store it in a map
            Map<String, String> roomMap = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        roomMap.put(parts[0], parts[1]); // Assuming parts[0] is RoomID and parts[1] is RoomNumber
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
                e.printStackTrace();
            }

            try (BufferedReader br = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                boolean hasRecords = false;
                while ((line = br.readLine()) != null) {
                    String[] details = line.split(",");
                    if (details[1].equals(userID)) { // Assuming the second element is the residentID
                        hasRecords = true;
                        String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room"); // Assuming the sixth element is RoomID
                        System.out.println("Payment ID: " + details[0]);
                        System.out.println("Resident ID: " + details[1]);
                        System.out.println("Staff ID: " + (details[2].equals("NULL") ? "N/A" : details[2]));
                        System.out.println("Payment Amount: " + details[3]);
                        System.out.println("Booking Date: " + details[4]);
                        System.out.println("Room Number: " + roomNumber);
                        System.out.println("Payment Method: " + (details[6].equals("NULL") ? "N/A" : details[6]));
                        System.out.println("-----------------------------");
                    }
                }
                if (!hasRecords) {
                    System.out.println("No payment records found for your account.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the payment records.");
                e.printStackTrace();
            }
        }

        public void manageBookings() {
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("Manage Bookings:");
                System.out.println("1. Make Booking");
                System.out.println("2. Make Payment for Booking");
                System.out.println("3. Cancel Booking");
                System.out.println("0. Go Back to Resident Menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        makeBooking();
                        break;
                    case 2:
                        makePaymentForBooking();
                        break;
                    case 3:
                        cancelBooking();
                        break;
                    case 0:
                        System.out.println("Returning to Resident Menu...");
                        displayMenu();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);
        }

        public void makePaymentForBooking() {
            // Logic for making payment for a booking
            System.out.println("Make Payment for Booking functionality is not yet implemented.");
        }

        public void cancelBooking() {
            // Logic for canceling a booking
            System.out.println("Cancel Booking functionality is not yet implemented.");
        }


        public void makeBooking() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select Room Type:");
            System.out.println("1. Standard");
            System.out.println("2. Deluxe");
            System.out.print("Enter your choice: ");
            int roomTypeChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String roomType = null;
            String feeRateID = null;
            switch (roomTypeChoice) {
                case 1:
                    roomType = "Standard";
                    feeRateID = "FR01";
                    break;
                case 2:
                    roomType = "Deluxe";
                    feeRateID = "FR02";
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }

            System.out.print("Enter start date of your stay (yyyy-MM-dd): ");
            String startDate = scanner.nextLine();
            System.out.print("Enter end date of your stay (yyyy-MM-dd): ");
            String endDate = scanner.nextLine();

            // Generate a new PaymentID
            String paymentID = generatePaymentID();

            // Get the ResidentID of the logged-in user
            String residentID = this.getUserID();

            // Select an available room based on room type
            String roomID = selectAvailableRoom(roomType);
            if (roomID == null) {
                System.out.println("No available rooms of the selected type.");
                return;
            }

            // Calculate the payment amount
            double paymentAmount = calculatePaymentAmount(startDate, endDate, feeRateID);

            // Get the current date and time for BookingDateTime
            String bookingDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Add a new line to payments.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt", true))) {
                writer.write(paymentID + "," + residentID + ",NULL," + startDate + "," + endDate + "," + roomID + "," + paymentAmount + ",Unpaid," + bookingDateTime + ",NULL,Active");
                writer.newLine();
                System.out.println("Booking successful.");
            } catch (IOException e) {
                System.out.println("An error occurred while saving the booking.");
                e.printStackTrace();
            }
        }

        private String generatePaymentID() {
            int id = 1;
            String filename = "payments.txt";
            File file = new File(filename);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].startsWith("P")) {
                        int currentId = Integer.parseInt(parts[0].substring(1));
                        if (currentId >= id) {
                            id = currentId + 1;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "P" + String.format("%02d", id);
        }

        private String selectAvailableRoom(String roomType) {
            Map<String, String> roomMap = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[1].equals(roomType)) {
                        roomMap.put(parts[0], parts[2]); // Assuming parts[0] is RoomID and parts[1] is RoomType
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
                e.printStackTrace();
            }

            for (String roomID : roomMap.keySet()) {
                return roomID; // Return the first available room
            }
            return null;
        }

        private double calculatePaymentAmount(String startDate, String endDate, String feeRateID) {
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.parse(startDate), LocalDate.parse(endDate)) + 1;
            double ratePerDay = 0;
            double ratePerWeek = 0;
            double ratePerMonth = 0;

            try (BufferedReader rateReader = new BufferedReader(new FileReader("fee_rates.txt"))) {
                String line;
                while ((line = rateReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(feeRateID)) {
                        ratePerDay = Double.parseDouble(parts[1]);
                        ratePerWeek = Double.parseDouble(parts[2]);
                        ratePerMonth = Double.parseDouble(parts[3]);
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the fee rate data.");
                e.printStackTrace();
            }

            if (daysBetween <= 7 && daysBetween > 0) {
                return daysBetween * ratePerDay;
            } else if (daysBetween <= 30 && daysBetween > 7) {
                return (daysBetween / 7) * ratePerWeek + (daysBetween % 7) * ratePerDay;
            } else {
                return (daysBetween / 30) * ratePerMonth + ((daysBetween % 30) / 7) * ratePerWeek + (daysBetween % 7) * ratePerDay;
            }
        }



        

        
        public void residentLogout() {
            System.out.println("Logging out...");
            // Perform any necessary cleanup, such as closing resources or saving state
            // For example:
            // closeDatabaseConnection();
            // saveUserSession();

            System.out.println("You have been logged out successfully.");
            // Route back to the main menu
            displayWelcomePage();
        }
    }

    // Payment class
    public static class Payment {
        private String paymentID;
        private String residentID;
        private String staffID;
        private double amount;
        private String bookingDate;
        private String roomNumber;
        private String paymentMethod;

        public Payment(String paymentID, String residentID, String staffID, double amount, String bookingDate, String roomNumber, String paymentMethod) {
            this.paymentID = paymentID;
            this.residentID = residentID;
            this.staffID = staffID;
            this.amount = amount;
            this.bookingDate = bookingDate;
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

        public String getBookingDate() {
            return bookingDate;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(paymentID + "," + residentID + "," + (staffID != null ? staffID : "NULL") + "," + amount + "," + bookingDate + "," + roomNumber + "," + (paymentMethod != null ? paymentMethod : "NULL"));
                writer.newLine();
            }
        }

        public static List<Payment> readFromFile(String filename) throws IOException {
            List<Payment> payments = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        Payment payment = new Payment(parts[0], parts[1], parts[2].equals("NULL") ? null : parts[2], Double.parseDouble(parts[3]), parts[4], parts[5], parts[6].equals("NULL") ? null : parts[6]);
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
        String icPassportNumber, username, password, contactNumber;

        while (true) {
            System.out.print("Enter IC/Passport Number: ");
            icPassportNumber = scanner.nextLine();
            if (icPassportNumber.isEmpty()) {
                System.out.println("IC/Passport Number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique(icPassportNumber, "", "")) {
                    System.out.println("Error: IC/Passport Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", username, "")) {
                    System.out.println("Error: Username already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (contactNumber.isEmpty()) {
                System.out.println("Contact number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", "", contactNumber)) {
                    System.out.println("Error: Contact Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String managerID = generateUserID("M");
            Manager manager = new Manager(managerID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "Manager", true);
            manager.saveToFile("users.txt");
            manager.saveToManagerFile();
            System.out.println("Manager registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Manager login
    public static void loginManager() {
        Scanner scanner = new Scanner(System.in);
        String username, password;

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        try {
            User user = User.findUser(username, password, "managers.txt");
            if (user != null && user.getRole().equals("Manager")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                System.out.print("Do you want to retry? (yes/no): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("yes")) {
                    loginManager(); // Retry login
                } else {
                    System.out.println("Exiting login process.");
                    displayWelcomePage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Staff registration
    public static void registerStaff() {
        Scanner scanner = new Scanner(System.in);
        String icPassportNumber, username, password, contactNumber;

        while (true) {
            System.out.print("Enter IC/Passport Number: ");
            icPassportNumber = scanner.nextLine();
            if (icPassportNumber.isEmpty()) {
                System.out.println("IC/Passport Number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique(icPassportNumber, "", "")) {
                    System.out.println("Error: IC/Passport Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", username, "")) {
                    System.out.println("Error: Username already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (contactNumber.isEmpty()) {
                System.out.println("Contact number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", "", contactNumber)) {
                    System.out.println("Error: Contact Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String staffID = generateUserID("S");
            Staff staff = new Staff(staffID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "Staff", true, null);
            staff.saveToStaffFile(null, null, "unapproved_staffs.txt");
            System.out.println("Staff registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Staff login
    public static void loginStaff() {
        Scanner scanner = new Scanner(System.in);
        String username, password;

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        try {
            User user = User.findUser(username, password, "approved_staffs.txt");
            if (user != null && user.getRole().equals("Staff")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                System.out.print("Do you want to retry? (yes/no): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("yes")) {
                    loginStaff(); // Retry login
                } else {
                    System.out.println("Exiting login process.");
                    displayWelcomePage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Resident registration
    public static void registerResident() {
        Scanner scanner = new Scanner(System.in);
        String icPassportNumber, username, password, contactNumber;

        while (true) {
            System.out.print("Enter IC/Passport Number: ");
            icPassportNumber = scanner.nextLine();
            if (icPassportNumber.isEmpty()) {
                System.out.println("IC/Passport Number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique(icPassportNumber, "", "")) {
                    System.out.println("Error: IC/Passport Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", username, "")) {
                    System.out.println("Error: Username already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (contactNumber.isEmpty()) {
                System.out.println("Contact number cannot be empty. Please try again.");
                continue;
            }
            try {
                if (!User.isUnique("", "", contactNumber)) {
                    System.out.println("Error: Contact Number already exists.");
                    System.out.print("Do you want to try again? (yes/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                        displayWelcomePage();
                        return;
                    }
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String residentID = generateUserID("R");
            Resident resident = new Resident(residentID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "Resident", true, null);
            resident.saveToResidentFile(null, null, "unapproved_residents.txt");
            System.out.println("Resident registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Resident login
    public static void loginResident() {
        Scanner scanner = new Scanner(System.in);
        String username, password;

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }
            break;
        }

        try {
            User user = User.findUser(username, password, "approved_residents.txt");
            if (user != null && user.getRole().equals("Resident")) {
                System.out.println("Login successful.");
                user.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
                System.out.print("Do you want to retry? (yes/no): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("yes")) {
                    loginResident(); // Retry login
                } else {
                    System.out.println("Exiting login process.");
                    displayWelcomePage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to generate unique IDs with a prefix
    private static String generateUserID(String prefix) {
        int id = 1;
        String filename = null;
        if (prefix.equals("U")) {
            filename = "users.txt";
        } else if (prefix.equals("M")) {
            filename = "managers.txt";
        } else if (prefix.equals("S")) {
            filename = "approved_staffs.txt";
        } else if (prefix.equals("R")) {
            filename = "approved_residents.txt";
        }
        
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
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

//test netbean pull push

//test again