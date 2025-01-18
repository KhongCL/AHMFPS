package apu.hostel.management;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;

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

        @Override
        public String toString() {
            return "UserID: " + userID + ", IC/Passport Number: " + icPassportNumber + ", Username: " + username + ", Contact Number: " + contactNumber + ", Date of Registration: " + dateOfRegistration + ", Role: " + role + ", IsActive: " + isActive;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserID() {
            return userID;
        }

        public void setIcPassportNumber(String icPassportNumber) {
            this.icPassportNumber = icPassportNumber;
        }

        public String getIcPassportNumber() {
            return icPassportNumber;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setPassword(String password) {
            this.password = password;
        }    

        public String getPassword() {
            return password;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public String getDateOfRegistration() {
            return dateOfRegistration;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setIsActive(boolean isActive) {
            this.isActive = isActive;
        }

        public boolean getIsActive() {
            return isActive;
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
                        switch (parts[6]) {
                            case "manager" -> user = new Manager(parts);
                            case "staff" -> user = new Staff(parts);
                            case "resident" -> user = new Resident(parts);
                        }
                    } else if (parts.length == 9 && "manager".equals(parts[7])) {
                        user = new Manager(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]));
                    } else if (parts.length == 10) {
                        if ("staff".equals(parts[7])) {
                            user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Boolean.parseBoolean(parts[8]), parts[9]);
                        } else if ("resident".equals(parts[7])) {
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

        public static void writeToFile(List<User> users, String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    writer.write(user.userID + "," + user.icPassportNumber + "," + user.username + "," + user.password + "," + user.contactNumber + "," + user.dateOfRegistration + "," + user.role + "," + user.isActive);
                    writer.newLine();
                }
            }
        }

        public static List<User> readFromFileForSearch(String filename) throws IOException {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    User user = null;
                    if (parts.length == 8) {
                        switch (parts[6]) {
                            case "manager" -> user = new Manager(parts);
                            case "staff" -> user = new Staff(parts);
                            case "resident" -> user = new Resident(parts);
                        }
                    } else if (parts.length == 10) {
                        if ("staff".equalsIgnoreCase(parts[7])) {
                            user = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7],Boolean.parseBoolean(parts[8]), parts[9]);
                        } else if ("resident".equalsIgnoreCase(parts[7])) {
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

        public List<User> readUsersForSearch() throws IOException {
            List<User> users = new ArrayList<>();
            users.addAll(User.readFromFileForSearch("users.txt"));
            users.addAll(User.readFromFileForSearch("unapproved_staffs.txt"));
            users.addAll(User.readFromFileForSearch("unapproved_residents.txt"));
            return users;
        }


    }

    // Manager class
    public static class Manager extends User {
        private String managerID;

        public Manager(String managerID, String userID, String icPassportNumber, String username, String password, String contactNumber, String dateOfRegistration, String role, boolean isActive) {
            super(userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, role, isActive);
            this.managerID = managerID;
        }

        public Manager(String[] parts) {
            super(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Boolean.parseBoolean(parts[7]));
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

        // Define a single Scanner instance at the class level
        private static final Scanner scanner = new Scanner(System.in);

        @Override
        public void displayMenu() {
            while (true) {
                // Manager-specific menu implementation
                System.out.println("Manager Menu:");
                System.out.println("1. Approve User Registration");
                System.out.println("2. Search, Update, Delete or Restore User");
                System.out.println("3. Fix, Update, Delete or Restore Rate");
                System.out.println("4. Manage Rooms");
                System.out.println("5. Update Personal Information");
                System.out.println("6. Logout");
                System.out.print("Enter your choice: ");
        
                int choice = getValidatedChoice(scanner, 1, 6);
        
                switch (choice) {
                    case 1 -> approveUserRegistration();
                    case 2 -> searchUsers();
                    case 3 -> fixOrUpdateRate();
                    case 4 -> manageRooms();
                    case 5 -> updatePersonalInformation();
                    case 6 -> {
                        System.out.println("Logging out...");
                        displayWelcomePage();
                        return; // Exit the loop after logging out
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        
        
        public void updatePersonalInformation() {
            int choice;
        
            do {
                System.out.println("Update Personal Information:");
                System.out.println("1. Update IC Passport Number");
                System.out.println("2. Update Username");
                System.out.println("3. Update Password");
                System.out.println("4. Update Contact Number");
                System.out.println("0. Go Back to Manager Menu");
                System.out.print("Enter your choice: ");
                choice = getValidatedChoice(scanner, 0, 4);
        
                switch (choice) {
                    case 1 -> {
                        System.out.println("Current IC Passport Number: " + this.icPassportNumber);
                        while (true) {
                            System.out.print("Enter new IC Passport Number: ");
                            String newIcPassportNumber = scanner.nextLine();
                            if (!isValidICPassport(newIcPassportNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.icPassportNumber = newIcPassportNumber;
                            System.out.println("IC Passport Number updated successfully.");
                            break;
                        }
                    }
                    case 2 -> {
                        System.out.println("Current Username: " + this.username);
                        while (true) {
                            System.out.print("Enter new username: ");
                            String newUsername = scanner.nextLine();
                            if (!isValidUsername(newUsername)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.username = newUsername;
                            System.out.println("Username updated successfully.");
                            break;
                        }
                    }
                    case 3 -> {
                        System.out.println("Current Password: " + this.password);
                        while (true) {
                            System.out.print("Enter new password: ");
                            String newPassword = scanner.nextLine();
                            if (!isValidPassword(newPassword, this.username)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.password = newPassword;
                            System.out.println("Password updated successfully.");
                            break;
                        }
                    }
                    case 4 -> {
                        System.out.println("Current Contact Number: " + this.contactNumber);
                        while (true) {
                            System.out.print("Enter new contact number: ");
                            String newContactNumber = scanner.nextLine();
                            if (!isValidContactNumber(newContactNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.contactNumber = newContactNumber;
                            System.out.println("Contact number updated successfully.");
                            break;
                        }
                    }
                    case 0 -> {
                        System.out.println("Returning to Manager Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        
                try {
                    updateFile("managers.txt", this);
                    updateFile("users.txt", this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
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
                    System.out.println((i + 1) + ". " + user.getUsername() + " (" + user.getIcPassportNumber() + ")");
                }
        
                System.out.println("Unapproved Residents:");
                for (int i = 0; i < unapprovedResidents.size(); i++) {
                    User user = unapprovedResidents.get(i);
                    System.out.println((i + 1 + unapprovedStaffs.size()) + ". " + user.getUsername() + " (" + user.getIcPassportNumber() + ")");
                }
        
                System.out.print("Enter the number of the user to approve: ");
                int userIndex = getValidatedChoice(scanner, 1, unapprovedStaffs.size() + unapprovedResidents.size()) - 1;
        
                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
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
            } catch (IOException e) {
                System.out.println("An error occurred while approving the user.");
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
        
        // Method to search users
        public void searchUsers() {
            List<User> users = new ArrayList<>();
        
            // Read users for search
            try {
                users = readUsersForSearch();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            while (true) {
                // Filter options
                System.out.println("Filter options:");
                System.out.println("1. Approved/Unapproved");
                System.out.println("2. Role");
                System.out.println("3. IsActive");
                System.out.println("4. No filter");
                System.out.print("Enter your choice (1-4): ");
        
                int filterChoice = getValidatedChoice(scanner, 1, 4);
        
                List<User> filteredUsers = new ArrayList<>(users);
        
                switch (filterChoice) {
                    case 1 -> {
                        System.out.println("1. Approved");
                        System.out.println("2. Unapproved");
                        System.out.print("Enter your choice (1-2): ");
                        int approvalChoice = getValidatedChoice(scanner, 1, 2);
                        try {
                            if (approvalChoice == 1) {
                                // Filter approved users
                                filteredUsers = readApprovedUsers();
                            } else {
                                // Filter unapproved users
                                filteredUsers = readUnapprovedUsers();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    case 2 -> {
                        System.out.println("1. Manager");
                        System.out.println("2. Staff");
                        System.out.println("3. Resident");
                        System.out.print("Enter your choice (1-3): ");
                        int roleChoice = getValidatedChoice(scanner, 1, 3);
                        final String[] role = {""};
                        switch (roleChoice) {
                            case 1 -> role[0] = "manager";
                            case 2 -> role[0] = "staff";
                            case 3 -> role[0] = "resident";
                        }
                        filteredUsers = filteredUsers.stream()
                                .filter(user -> user.getRole().equalsIgnoreCase(role[0]))
                                .collect(Collectors.toList());
                    }
                    case 3 -> {
                        System.out.println("1. Active");
                        System.out.println("2. Inactive");
                        System.out.print("Enter your choice (1-2): ");
                        int activeChoice = getValidatedChoice(scanner, 1, 2);
                        boolean isActive = (activeChoice == 1);
                        filteredUsers = filteredUsers.stream()
                                .filter(user -> user.getIsActive() == isActive)
                                .collect(Collectors.toList());
                    }
                    default -> {
                    }
                }
        
                // Sort options
                System.out.println("Sort options:");
                System.out.println("1. Primary Key Ascending");
                System.out.println("2. Primary Key Descending");
                System.out.println("3. Username Ascending");
                System.out.println("4. Username Descending");
                System.out.print("Enter your choice (1-4): ");
        
                int sortChoice = getValidatedChoice(scanner, 1, 4);
        
                switch (sortChoice) {
                    case 1 -> filteredUsers.sort(Comparator.comparing(User::getUserID));
                    case 2 -> filteredUsers.sort(Comparator.comparing(User::getUserID).reversed());
                    case 3 -> filteredUsers.sort(Comparator.comparing(User::getUsername));
                    case 4 -> filteredUsers.sort(Comparator.comparing(User::getUsername).reversed());
                    default -> {
                    }
                }
        
                // Search by username
                System.out.print("Enter username to search (or press Enter to skip): ");
                String usernameSearch = scanner.nextLine();
                if (!usernameSearch.isEmpty()) {
                    filteredUsers = filteredUsers.stream()
                            .filter(user -> user.getUsername().toLowerCase().contains(usernameSearch.toLowerCase()))
                            .collect(Collectors.toList());
                }
        
                // Display filtered and sorted users with index numbers
                System.out.println("Filtered and Sorted Users:");
                int index = 1;
                for (User user : filteredUsers) {
                    System.out.println(index + ". " + user);
                    index++;
                }
                System.out.println("Total users: " + filteredUsers.size());
                System.out.println("Search completed.");
        
                if (filteredUsers.isEmpty()) {
                    System.out.print("No users found. Do you want to search again? (yes/no): ");
                    String retryChoice = scanner.nextLine();
                    if (retryChoice.equalsIgnoreCase("yes")) {
                        continue; // Loop back to search again
                    } else {
                        return; // Go back to manager main menu
                    }
                }
        
                updateDeleteOrRestoreUser(filteredUsers);
                break; // Exit the loop after processing
            }
        }
        
        
        public List<User> readApprovedUsers() throws IOException {
            return User.readFromFileForSearch("users.txt");
        }
        
        public List<User> readUnapprovedUsers() throws IOException {
            List<User> unapprovedUsers = new ArrayList<>();
            unapprovedUsers.addAll(User.readFromFileForSearch("unapproved_staffs.txt"));
            unapprovedUsers.addAll(User.readFromFileForSearch("unapproved_residents.txt"));
            return unapprovedUsers;
        }

        public void updateDeleteOrRestoreUser(List<User> users) {
            OUTER:
            while (true) {
                System.out.println("User Management:");
                System.out.println("1. Choose user to update or delete");
                System.out.println("2. Delete all users");
                System.out.println("3. Restore all users");
                System.out.println("4. Return to main menu");
                System.out.print("Enter your choice (1-4): ");
                int choice = getValidatedChoice(scanner, 1, 4);
                switch (choice) {
                    case 1 -> {
                        // Select user to update or delete
                        System.out.print("Enter the number of the user to update or delete (or 0 to cancel): ");
                        int userChoice = getValidatedChoice(scanner, 0, users.size());
                        if (userChoice == 0) {
                            System.out.println("Operation cancelled.");
                            continue;
                        }
                        User userToUpdate = users.get(userChoice - 1);
                        // Choose to update, delete, or restore
                        System.out.println("1. Update User");
                        System.out.println("2. Delete User");
                        System.out.println("3. Restore User");
                        System.out.println("4. Return to main menu");
                        System.out.print("Enter your choice: ");
                        int actionChoice = getValidatedChoice(scanner, 1, 4);
                        switch (actionChoice) {
                            case 1 -> updateUser(userToUpdate);
                            case 2 -> deleteUser(userToUpdate);
                            case 3 -> restoreUser(userToUpdate);
                            default -> {
                                System.out.println("Returning to main menu...");
                                break OUTER;
                            }
                        }
                    }
                    case 2 -> deleteAllUsers(users);
                    case 3 -> restoreAllUsers(users);
                    default -> {
                        System.out.println("Returning to main menu...");
                        return;
                    }
                }
            }
        }
        
        private void deleteAllUsers(List<User> users) {
            System.out.println("Users that will be deleted:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i));
            }
        
            System.out.print("Are you sure you want to delete all users? This action cannot be undone. You can restore all users on the menu. (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                for (User user : users) {
                    user.setIsActive(false);
                }
                System.out.println("All users deleted successfully.");
                try {
                    for (String filename : getAllFilenames()) {
                        for (User user : users) {
                            updateFile(filename, user);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Delete all users cancelled.");
            }
        }
        
        private void restoreAllUsers(List<User> users) {
            System.out.println("Users that will be restored:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i));
            }
        
            System.out.print("Are you sure you want to restore all users? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                for (User user : users) {
                    user.setIsActive(true);
                }
                System.out.println("All users restored successfully.");
                try {
                    for (String filename : getAllFilenames()) {
                        for (User user : users) {
                            updateFile(filename, user);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Restore all users cancelled.");
            }
        }
        
        private List<String> getAllFilenames() {
            return Arrays.asList("approved_staffs.txt", "approved_residents.txt", "users.txt", "managers.txt", "unapproved_staffs.txt", "unapproved_residents.txt");
        }
        
        public void updateUser(User userToUpdate) {
            int choice;
            do {
                System.out.println("Update User Information:");
                System.out.println("1. Update IC Passport Number");
                System.out.println("2. Update Username");
                System.out.println("3. Update Password");
                System.out.println("4. Update Contact Number");
                System.out.println("0. Go Back");
                System.out.print("Enter your choice: ");
                choice = getValidatedChoice(scanner, 0, 4);
        
                switch (choice) {
                    case 1 -> {
                        System.out.println("Current IC Passport Number: " + userToUpdate.getIcPassportNumber());
                        while (true) {
                            System.out.print("Enter new IC Passport Number: ");
                            String newIcPassportNumber = scanner.nextLine();
                            if (!isValidICPassport(newIcPassportNumber)) {
                                System.out.print("Invalid IC Passport Number. Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                            } else {
                                userToUpdate.setIcPassportNumber(newIcPassportNumber);
                                System.out.println("IC Passport Number updated successfully.");
                                break;
                            }
                        }
                    }
                    case 2 -> {
                        System.out.println("Current Username: " + userToUpdate.getUsername());
                        while (true) {
                            System.out.print("Enter new username: ");
                            String newUsername = scanner.nextLine();
                            if (!isValidUsername(newUsername)) {
                                System.out.print("Invalid Username. Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                            } else {
                                userToUpdate.setUsername(newUsername);
                                System.out.println("Username updated successfully.");
                                break;
                            }
                        }
                    }
                    case 3 -> {
                        System.out.println("Current Password: " + userToUpdate.getPassword());
                        while (true) {
                            System.out.print("Enter new password: ");
                            String newPassword = scanner.nextLine();
                            if (!isValidPassword(newPassword, userToUpdate.getUsername())) {
                                System.out.print("Invalid Password. Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                            } else {
                                userToUpdate.setPassword(newPassword);
                                System.out.println("Password updated successfully.");
                                break;
                            }
                        }
                    }
                    case 4 -> {
                        System.out.println("Current Contact Number: " + userToUpdate.getContactNumber());
                        while (true) {
                            System.out.print("Enter new contact number: ");
                            String newContactNumber = scanner.nextLine();
                            if (!isValidContactNumber(newContactNumber)) {
                                System.out.print("Invalid Contact Number. Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                            } else {
                                userToUpdate.setContactNumber(newContactNumber);
                                System.out.println("Contact number updated successfully.");
                                break;
                            }
                        }
                    }
                    case 0 -> {
                        System.out.println("Returning to main menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        
                try {
                    updateFile("approved_staffs.txt", userToUpdate);
                    updateFile("approved_residents.txt", userToUpdate);
                    updateFile("users.txt", userToUpdate);
                    updateFile("managers.txt", userToUpdate);
                    updateFile("unapproved_staffs.txt", userToUpdate);
                    updateFile("unapproved_residents.txt", userToUpdate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
        }
        
        public void deleteUser(User userToUpdate) {
            if (!userToUpdate.getIsActive()) {
                System.out.println("This user is already deactivated.");
            } else {
                System.out.println("User Details:");
                System.out.println("IC Passport Number: " + userToUpdate.getIcPassportNumber());
                System.out.println("Username: " + userToUpdate.getUsername());
                System.out.println("Password: " + userToUpdate.getPassword());
                System.out.println("Contact Number: " + userToUpdate.getContactNumber());
                System.out.print("Are you sure you want to delete this user? (yes/no): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();
        
                if (confirmation.equals("yes")) {
                    // Soft delete user
                    userToUpdate.setIsActive(false);
                    System.out.println("User deactivated successfully.");
        
                    try {
                        updateFile("approved_staffs.txt", userToUpdate);
                        updateFile("approved_residents.txt", userToUpdate);
                        updateFile("users.txt", userToUpdate);
                        updateFile("managers.txt", userToUpdate);
                        updateFile("unapproved_staffs.txt", userToUpdate);
                        updateFile("unapproved_residents.txt", userToUpdate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("User deletion cancelled.");
                }
            }
        }
        
        public void restoreUser(User userToRestore) {
            if (userToRestore.getIsActive()) {
                System.out.println("This user is already active.");
                return;
            }
        
            // Confirm restoration
            System.out.println("User Details:");
            System.out.println("IC Passport Number: " + userToRestore.getIcPassportNumber());
            System.out.println("Username: " + userToRestore.getUsername());
            System.out.println("Password: " + userToRestore.getPassword());
            System.out.println("Contact Number: " + userToRestore.getContactNumber());
            System.out.print("Are you sure you want to restore this user? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
        
            if (confirmation.equals("yes")) {
                // Restore user
                userToRestore.setIsActive(true);
                System.out.println("User restored successfully.");
        
                try {
                    updateFile("approved_staffs.txt", userToRestore);
                    updateFile("approved_residents.txt", userToRestore);
                    updateFile("users.txt", userToRestore);
                    updateFile("managers.txt", userToRestore);
                    updateFile("unapproved_staffs.txt", userToRestore);
                    updateFile("unapproved_residents.txt", userToRestore);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User restoration cancelled.");
            }
        }
        
        private void updateFile(String filename, User updatedUser) throws IOException {
            List<User> users = User.readFromFile(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    boolean isMatch = user.getUserID().equals(updatedUser.getUserID());
                    if (filename.equals("unapproved_staffs.txt") || filename.equals("unapproved_residents.txt")) {
                        isMatch = user.getIcPassportNumber().equals(updatedUser.getIcPassportNumber()) ||
                                  user.getUsername().equals(updatedUser.getUsername()) ||
                                  user.getContactNumber().equals(updatedUser.getContactNumber());
                    }
                    
                    if (isMatch) {
                        if (filename.equals("users.txt")) {
                            writer.write(updatedUser.getUserID() + "," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive());
                        } else if (filename.equals("managers.txt") && user instanceof Manager) {
                            Manager manager = (Manager) user;
                            writer.write(manager.getManagerID() + "," + updatedUser.getUserID() + "," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive());
                        } else if (filename.equals("approved_staffs.txt") && user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + updatedUser.getUserID() + "," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (filename.equals("approved_residents.txt") && user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + updatedUser.getUserID() + "," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive() + "," + resident.getDateOfApproval());
                        } else if (filename.equals("unapproved_staffs.txt") && user instanceof Staff) {
                            writer.write("null,null," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive() + ",null");
                        } else if (filename.equals("unapproved_residents.txt") && user instanceof Resident) {
                            writer.write("null,null," + updatedUser.getIcPassportNumber() + "," + updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getContactNumber() + "," + updatedUser.getDateOfRegistration() + "," + updatedUser.getRole() + "," + updatedUser.getIsActive() + ",null");
                        }
                    } else {
                        if (filename.equals("users.txt")) {
                            writer.write(user.getUserID() + "," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive());
                        } else if (filename.equals("managers.txt") && user instanceof Manager) {
                            Manager manager = (Manager) user;
                            writer.write(manager.getManagerID() + "," + user.getUserID() + "," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive());
                        } else if (filename.equals("approved_staffs.txt") && user instanceof Staff) {
                            Staff staff = (Staff) user;
                            writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (filename.equals("approved_residents.txt") && user instanceof Resident) {
                            Resident resident = (Resident) user;
                            writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + resident.getDateOfApproval());
                        } else if (filename.equals("unapproved_staffs.txt") && user instanceof Staff) {
                            writer.write("null,null," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive() + ",null");
                        } else if (filename.equals("unapproved_residents.txt") && user instanceof Resident) {
                            writer.write("null,null," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive() + ",null");
                        }
                    }
                    writer.newLine();
                }
            }
        }

        public static List<FeeRate> readRatesFromFile(String filename) throws IOException {
            List<FeeRate> feeRates = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        FeeRate feeRate = new FeeRate(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Boolean.parseBoolean(parts[6]));
                        feeRates.add(feeRate);
                    }
                }
            }
            return feeRates;
        }
        
        public void fixOrUpdateRate() {
            List<FeeRate> rates = new ArrayList<>();
        
            // Load existing rates from file
            try {
                rates = readRatesFromFile("fee_rates.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            OUTER:
            while (true) {
                System.out.println("Fix or Update Rates:");
                System.out.println("1. Set Initial Rates");
                System.out.println("2. Update Existing Rates");
                System.out.println("3. Delete Rate");
                System.out.println("4. Restore Deleted Rate");
                System.out.println("5. Delete All Rates");
                System.out.println("6. Restore All Rates");
                System.out.println("7. Exit");
                System.out.print("Enter your choice (1-7): ");
                int choice = getValidatedChoice(scanner, 1, 7);
                switch (choice) {
                    case 1 -> {
                        setInitialRates(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 2 -> {
                        updateExistingRates(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 3 -> {
                        deleteRate(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 4 -> {
                        restoreDeletedRate(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 5 -> {
                        deleteAllRates(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 6 -> {
                        restoreAllRates(scanner, rates);
                        saveRatesToFile(rates);
                    }
                    case 7 -> {
                        break OUTER;
                    }
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
            displayMenu();
        }
        
        private void setInitialRates(Scanner scanner, List<FeeRate> rates) {
            while (true) {
                String feeRateID = "FR" + String.format("%02d", rates.size() + 1);
        
                System.out.println("Available Room Types:");
                System.out.println("1. Standard");
                System.out.println("2. Large");
                System.out.println("3. Family");
                System.out.print("Enter your choice (1-3): ");
                int roomTypeChoice = getValidatedChoice(scanner, 1, 3);
                String roomType;
                switch (roomTypeChoice) {
                    case 1 -> roomType = "standard";
                    case 2 -> roomType = "large";
                    case 3 -> roomType = "family";
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                    }
                }
        
                double dailyRate = getValidatedRate(scanner, "Daily Rate");
                double weeklyRate = getValidatedRate(scanner, "Weekly Rate");
                double monthlyRate = getValidatedRate(scanner, "Monthly Rate");
                double yearlyRate = getValidatedRate(scanner, "Yearly Rate");
        
                System.out.println("Fee Rate Details:");
                System.out.println("Fee Rate ID: " + feeRateID);
                System.out.println("Room Type: " + roomType);
                System.out.println("Daily Rate: " + dailyRate);
                System.out.println("Weekly Rate: " + weeklyRate);
                System.out.println("Monthly Rate: " + monthlyRate);
                System.out.println("Yearly Rate: " + yearlyRate);
                System.out.print("Are you sure you want to add this rate? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes")) {
                    rates.add(new FeeRate(feeRateID, roomType, dailyRate, weeklyRate, monthlyRate, yearlyRate, true));
                    System.out.println("Rate added successfully.");
                } else {
                    System.out.println("Rate addition cancelled.");
                }
        
                System.out.print("Do you want to add another rate? (yes/no): ");
                String addMore = scanner.nextLine().trim().toLowerCase();
                if (addMore.equalsIgnoreCase("no")) {
                    break;
                }
            }
        }
        
        
        private void updateExistingRates(Scanner scanner, List<FeeRate> rates) {
            if (rates.isEmpty()) {
                System.out.println("No existing rates to update.");
                return;
            }
        
            System.out.println("Existing Fee Rates:");
            for (int i = 0; i < rates.size(); i++) {
                System.out.println((i + 1) + ". " + rates.get(i));
            }
        
            System.out.print("Enter the number of the fee rate to update: ");
            int rateChoice = getValidatedChoice(scanner, 1, rates.size());
            FeeRate rateToUpdate = rates.get(rateChoice - 1);
        
            System.out.println("Current Rates:");
            System.out.println("Room Type: " + rateToUpdate.getRoomType());
            System.out.println("Daily Rate: " + rateToUpdate.getDailyRate());
            System.out.println("Weekly Rate: " + rateToUpdate.getWeeklyRate());
            System.out.println("Monthly Rate: " + rateToUpdate.getMonthlyRate());
            System.out.println("Yearly Rate: " + rateToUpdate.getYearlyRate());
        
            System.out.println("Which attribute do you want to update?");
            System.out.println("1. Room Type");
            System.out.println("2. Daily Rate");
            System.out.println("3. Weekly Rate");
            System.out.println("4. Monthly Rate");
            System.out.println("5. Yearly Rate");
            System.out.print("Enter your choice (1-5): ");
            int attributeChoice = getValidatedChoice(scanner, 1, 5);
        
            // Extract fee rate IDs from rooms.txt
            List<String> restrictedFeeRateIDs = new ArrayList<>();
            List<Room> rooms = readRoomsFromFile("rooms.txt");
            for (Room room : rooms) {
                if (!restrictedFeeRateIDs.contains(room.getFeeRateID())) {
                    restrictedFeeRateIDs.add(room.getFeeRateID());
                }
            }
        
            if (attributeChoice == 1 && restrictedFeeRateIDs.contains(rateToUpdate.getFeeRateID())) {
                System.out.println("Cannot update room type for fee rate ID: " + rateToUpdate.getFeeRateID() + " as it exists in rooms.txt.");
                return;
            }
        
            if (attributeChoice == 1) {
                System.out.println("Available Room Types:");
                System.out.println("1. Standard");
                System.out.println("2. Large");
                System.out.println("3. Family");
                System.out.print("Enter your choice (1-3): ");
                int roomTypeChoice = getValidatedChoice(scanner, 1, 3);
                String roomType;
                int roomCapacity = 0;
                switch (roomTypeChoice) {
                    case 1 -> {
                        roomType = "standard";
                        roomCapacity = 1;
                    }
                    case 2 -> {
                        roomType = "large";
                        roomCapacity = 3;
                    }
                    case 3 -> {
                        roomType = "family";
                        roomCapacity = 6;
                    }
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                        return;
                    }
                }
                if (roomType.equals(rateToUpdate.getRoomType())) {
                    System.out.println("The selected room type is the same as the current room type.");
                    return;
                }
                System.out.println("Current Room Type: " + rateToUpdate.getRoomType());
                System.out.println("New Room Type: " + roomType);
                System.out.print("Are you sure you want to update the room type? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes")) {
                    rateToUpdate.setRoomType(roomType);
        
                    // Update room type in rooms.txt
                    for (Room room : rooms) {
                        if (room.getFeeRateID().equals(rateToUpdate.getFeeRateID())) {
                            room.setRoomType(roomType);
                            room.setRoomCapacity(roomCapacity);
                        }
                    }
                    saveRoomsToFile(rooms);
        
                    System.out.println("Room Type updated successfully.");
                } else {
                    System.out.println("Room type update cancelled.");
                }
            } else {
                double newRate = getValidatedRate(scanner, "new rate");
                if (newRate == getCurrentRate(rateToUpdate, attributeChoice)) {
                    System.out.println("The new rate is the same as the current rate.");
                    return;
                }
                System.out.println("Current Rate: " + getCurrentRate(rateToUpdate, attributeChoice));
                System.out.println("New Rate: " + newRate);
                System.out.print("Are you sure you want to update the rate? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes")) {
                    switch (attributeChoice) {
                        case 2 -> rateToUpdate.setDailyRate(newRate);
                        case 3 -> rateToUpdate.setWeeklyRate(newRate);
                        case 4 -> rateToUpdate.setMonthlyRate(newRate);
                        case 5 -> rateToUpdate.setYearlyRate(newRate);
                    }
                    System.out.println("Rate updated successfully.");
                } else {
                    System.out.println("Rate update cancelled.");
                }
            }
        }
        
        private double getCurrentRate(FeeRate rate, int attributeChoice) {
            return switch (attributeChoice) {
                case 2 -> rate.getDailyRate();
                case 3 -> rate.getWeeklyRate();
                case 4 -> rate.getMonthlyRate();
                case 5 -> rate.getYearlyRate();
                default -> -1;
            };
        }
        
        private void deleteRate(Scanner scanner, List<FeeRate> rates) {
            if (rates.isEmpty()) {
                System.out.println("No existing rates to delete.");
                return;
            }
        
            // Extract fee rate IDs from rooms.txt
            List<String> usedFeeRateIDs = new ArrayList<>();
            List<Room> rooms = readRoomsFromFile("rooms.txt");
            for (Room room : rooms) {
                if (!usedFeeRateIDs.contains(room.getFeeRateID())) {
                    usedFeeRateIDs.add(room.getFeeRateID());
                }
            }
        
            System.out.println("Existing Fee Rates:");
            List<FeeRate> deletableRates = new ArrayList<>();
            for (int i = 0; i < rates.size(); i++) {
                if (rates.get(i).isActive() && !usedFeeRateIDs.contains(rates.get(i).getFeeRateID())) {
                    deletableRates.add(rates.get(i));
                    System.out.println((deletableRates.size()) + ". " + rates.get(i));
                }
            }
        
            if (deletableRates.isEmpty()) {
                System.out.println("No deletable rates available.");
                return;
            }
        
            System.out.print("Enter the number of the fee rate to delete: ");
            int rateChoice = getValidatedChoice(scanner, 1, deletableRates.size());
            FeeRate rateToDelete = deletableRates.get(rateChoice - 1);
        
            System.out.println("Rate Details:");
            System.out.println(rateToDelete);
        
            System.out.print("Are you sure you want to delete this rate? (yes/no): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("yes")) {
                rateToDelete.setActive(false);
                System.out.println("Rate deleted successfully.");
            } else {
                System.out.println("Rate deletion cancelled.");
            }
        }
        
        private void restoreDeletedRate(Scanner scanner, List<FeeRate> rates) {
            List<FeeRate> deletedRates = new ArrayList<>();
            for (FeeRate rate : rates) {
                if (!rate.isActive()) {
                    deletedRates.add(rate);
                }
            }
        
            if (deletedRates.isEmpty()) {
                System.out.println("No deleted rates to restore.");
                return;
            }
        
            System.out.println("Deleted Fee Rates:");
            for (int i = 0; i < deletedRates.size(); i++) {
                System.out.println((i + 1) + ". " + deletedRates.get(i));
            }
        
            System.out.print("Enter the number of the fee rate to restore: ");
            int rateChoice = getValidatedChoice(scanner, 1, deletedRates.size());
            FeeRate rateToRestore = deletedRates.get(rateChoice - 1);
        
            System.out.println("Rate Details:");
            System.out.println(rateToRestore);
        
            System.out.print("Are you sure you want to restore this rate? (yes/no): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("yes")) {
                rateToRestore.setActive(true);
                System.out.println("Rate restored successfully.");
            } else {
                System.out.println("Rate restoration cancelled.");
            }
        }
        
        private void deleteAllRates(Scanner scanner, List<FeeRate> rates) {
            if (rates.isEmpty()) {
                System.out.println("No existing rates to delete.");
                return;
            }
        
            // Extract fee rate IDs from rooms.txt
            List<String> usedFeeRateIDs = new ArrayList<>();
            List<Room> rooms = readRoomsFromFile("rooms.txt");
            for (Room room : rooms) {
                if (!usedFeeRateIDs.contains(room.getFeeRateID())) {
                    usedFeeRateIDs.add(room.getFeeRateID());
                }
            }
        
            List<FeeRate> deletableRates = new ArrayList<>();
            for (FeeRate rate : rates) {
                if (!usedFeeRateIDs.contains(rate.getFeeRateID())) {
                    deletableRates.add(rate);
                }
            }
        
            if (deletableRates.isEmpty()) {
                System.out.println("No deletable rates available.");
                return;
            }
        
            System.out.println("Rates that will be deleted:");
            for (int i = 0; i < deletableRates.size(); i++) {
                System.out.println((i + 1) + ". " + deletableRates.get(i));
            }
        
            System.out.print("Are you sure you want to delete all these rates? This action cannot be undone. You can restore all rates on the menu. (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                for (FeeRate rate : deletableRates) {
                    rate.setActive(false);
                }
                saveRatesToFile(rates);
                System.out.println("All deletable rates deleted successfully.");
            } else {
                System.out.println("Delete all rates cancelled.");
            }
        }
        
        private void restoreAllRates(Scanner scanner, List<FeeRate> rates) {
            List<FeeRate> deletedRates = new ArrayList<>();
            for (FeeRate rate : rates) {
                if (!rate.isActive()) {
                    deletedRates.add(rate);
                }
            }
        
            if (deletedRates.isEmpty()) {
                System.out.println("No deleted rates to restore.");
                return;
            }
        
            System.out.println("Rates that will be restored:");
            for (int i = 0; i < deletedRates.size(); i++) {
                System.out.println((i + 1) + ". " + deletedRates.get(i));
            }
        
            System.out.print("Are you sure you want to restore all these rates? (yes/no): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("yes")) {
                for (FeeRate rate : deletedRates) {
                    rate.setActive(true);
                }
                saveRatesToFile(rates);
                System.out.println("All rates restored successfully.");
            } else {
                System.out.println("Restore all rates cancelled.");
            }
        }


        private double getValidatedRate(Scanner scanner, String rateType) {
            double rate = -1;
            while (rate <= 0) {
                System.out.print("Enter " + rateType + ": ");
                if (scanner.hasNextDouble()) {
                    rate = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    if (rate <= 0) {
                        System.out.println(rateType + " must be greater than zero. Please enter a valid rate.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid " + rateType + ".");
                    scanner.nextLine(); // Consume invalid input
                }
            }
            return rate;
        }
        
        private void saveRatesToFile(List<FeeRate> rates) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("fee_rates.txt"))) {
                for (FeeRate rate : rates) {
                    writer.write(rate.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
            }
        }

        private void manageRooms() {
            OUTER:
            while (true) {
                System.out.println("Manage Rooms:");
                System.out.println("1. Add Room");
                System.out.println("2. Update Room Status");
                System.out.println("3. Update Fee Rate for Room Type");
                System.out.println("4. Delete Room");
                System.out.println("5. Restore Room");
                System.out.println("6. Delete All Rooms");
                System.out.println("7. Restore All Rooms");
                System.out.println("8. Return to main menu");
                System.out.print("Enter your choice (1-8): ");
                int choice = getValidatedChoice(scanner, 1, 8);
                switch (choice) {
                    case 1 -> addRoom(scanner);
                    case 2 -> updateRoomStatus(scanner);
                    case 3 -> updateRoomType(scanner);
                    case 4 -> deleteRoom(scanner);
                    case 5 -> restoreRoom(scanner);
                    case 6 -> deleteAllRooms(scanner);
                    case 7 -> restoreAllRooms(scanner);
                    case 8 -> {
                        break OUTER;
                    }
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
            displayMenu();
        }
        
        private void addRoom(Scanner scanner) {
            List<FeeRate> feeRates = new ArrayList<>();
            List<Room> rooms = new ArrayList<>();
            try {
                feeRates = readRatesFromFile("fee_rates.txt");
                rooms = readRoomsFromFile("rooms.txt");
            } catch (IOException e) {
                return;
            }
        
            String roomId = "RM" + String.format("%02d", rooms.size() + 1);
            int roomNumber = 101 + rooms.size();
        
            System.out.println("Available Room Types:");
            List<String> roomTypes = feeRates.stream()
                    .map(FeeRate::getRoomType)
                    .distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + roomTypes.get(i));
            }
        
            System.out.print("Enter the number of the room type to use: ");
            int roomTypeChoice = getValidatedChoice(scanner, 1, roomTypes.size());
            String selectedRoomType = roomTypes.get(roomTypeChoice - 1);
        
            FeeRate selectedFeeRate = feeRates.stream()
                    .filter(rate -> rate.getRoomType().equalsIgnoreCase(selectedRoomType))
                    .findFirst()
                    .orElse(null);
        
            if (selectedFeeRate == null) {
                System.out.println("No fee rate found for the selected room type.");
                return;
            }
        
            int roomCapacity;
            switch (selectedRoomType.toLowerCase()) {
                case "standard" -> roomCapacity = 1;
                case "large" -> roomCapacity = 3;
                case "family" -> roomCapacity = 6;
                default -> {
                    System.out.println("Invalid room type.");
                    return;
                }
            }
        
            Room newRoom = new Room(roomId, selectedFeeRate.getFeeRateID(), selectedRoomType, roomNumber, "available", roomCapacity, true);
        
            // Confirmation before adding the room
            System.out.println("Room Details:");
            System.out.println("Room ID: " + newRoom.getRoomID());
            System.out.println("Fee Rate ID: " + newRoom.getFeeRateID());
            System.out.println("Room Type: " + newRoom.getRoomType());
            System.out.println("Room Number: " + newRoom.getRoomNumber());
            System.out.println("Room Status: " + newRoom.getRoomStatus());
            System.out.println("Room Capacity: " + newRoom.getRoomCapacity());
            System.out.print("Do you want to add this room? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
        
            if (confirmation.equals("yes")) {
                rooms.add(newRoom);
                saveRoomsToFile(rooms);
                System.out.println("Room added successfully.");
            } else {
                System.out.println("Room addition cancelled.");
            }
        
            System.out.print("Do you want to add another room? (yes/no): ");
            String addMore = scanner.nextLine();
            if (addMore.equalsIgnoreCase("yes")) {
                addRoom(scanner);
            }
        }
        
        private void updateRoomStatus(Scanner scanner) {
            List<Room> rooms = readRoomsFromFile("rooms.txt");
        
            System.out.println("Existing Rooms:");
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println((i + 1) + ". " + rooms.get(i));
            }
            System.out.print("Enter the number of the room to update: ");
            int roomChoice = getValidatedChoice(scanner, 1, rooms.size());
            Room roomToUpdate = rooms.get(roomChoice - 1);
        
            System.out.println("Current Room Details:");
            System.out.println("Room ID: " + roomToUpdate.getRoomID());
            System.out.println("Fee Rate ID: " + roomToUpdate.getFeeRateID());
            System.out.println("Room Type: " + roomToUpdate.getRoomType());
            System.out.println("Room Number: " + roomToUpdate.getRoomNumber());
            System.out.println("Room Status: " + roomToUpdate.getRoomStatus());
            System.out.println("Room Capacity: " + roomToUpdate.getRoomCapacity());
        
            String newStatus = roomToUpdate.getRoomStatus().equals("available") ? "unavailable" : "available";
            roomToUpdate.setRoomStatus(newStatus);
            System.out.println("Room status updated successfully to " + newStatus + ".");
        
            // Confirmation before saving the updated room
            System.out.print("Do you want to save the changes? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
        
            if (confirmation.equals("yes")) {
                saveRoomsToFile(rooms);
                System.out.println("Room updated successfully.");
            } else {
                System.out.println("Room update cancelled.");
            }
        }
        
        private void updateRoomType(Scanner scanner) {
            List<FeeRate> feeRates = new ArrayList<>();
            List<Room> rooms = new ArrayList<>();
            try {
                feeRates = readRatesFromFile("fee_rates.txt");
                rooms = readRoomsFromFile("rooms.txt");
            } catch (IOException e) {
                return;
            }
        
            System.out.println("Available Room Types:");
            List<String> roomTypes = feeRates.stream()
                    .map(FeeRate::getRoomType)
                    .distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + roomTypes.get(i));
            }
        
            System.out.print("Enter the number of the room type to update: ");
            int roomTypeChoice = getValidatedChoice(scanner, 1, roomTypes.size());
            String selectedRoomType = roomTypes.get(roomTypeChoice - 1);
        
            // Find the current fee rate ID used by the selected room type
            String currentFeeRateID = rooms.stream()
                    .filter(room -> room.getRoomType().equalsIgnoreCase(selectedRoomType))
                    .map(Room::getFeeRateID)
                    .findFirst()
                    .orElse(null);
        
            System.out.println("Current Fee Rate ID for " + selectedRoomType + ": " + currentFeeRateID);
        
            System.out.println("Available Fee Rates for " + selectedRoomType + ":");
            List<FeeRate> selectedFeeRates = feeRates.stream()
                    .filter(rate -> rate.getRoomType().equalsIgnoreCase(selectedRoomType) && !rate.getFeeRateID().equals(currentFeeRateID) && rate.getIsActive())
                    .collect(Collectors.toList());
            for (int i = 0; i < selectedFeeRates.size(); i++) {
                System.out.println((i + 1) + ". " + selectedFeeRates.get(i));
            }
        
            if (selectedFeeRates.size() < 1) {
                System.out.println("Not enough fee rates available for this room type. You can add fee rates in another section of the main menu.");
                return;
            }
        
            System.out.print("Enter the number of the fee rate to use: ");
            int feeRateChoice = getValidatedChoice(scanner, 1, selectedFeeRates.size());
            FeeRate selectedFeeRate = selectedFeeRates.get(feeRateChoice - 1);
        
            for (Room room : rooms) {
                if (room.getRoomType().equalsIgnoreCase(selectedRoomType)) {
                    room.setFeeRateID(selectedFeeRate.getFeeRateID());
                }
            }
        
            // Confirmation before saving the updated rooms
            System.out.print("Do you want to save the changes? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
        
            if (confirmation.equals("yes")) {
                saveRoomsToFile(rooms);
                System.out.println("Rooms updated successfully.");
            } else {
                System.out.println("Room type update cancelled.");
            }
        }
        
        
        private void deleteRoom(Scanner scanner) {
            List<Room> rooms = readRoomsFromFile("rooms.txt");
        
            System.out.println("Existing Active and Available Rooms:");
            List<Room> deletableRooms = new ArrayList<>();
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).isActive() && rooms.get(i).getRoomStatus().equalsIgnoreCase("available")) {
                    deletableRooms.add(rooms.get(i));
                    System.out.println((deletableRooms.size()) + ". " + rooms.get(i));
                }
            }
        
            if (deletableRooms.isEmpty()) {
                System.out.println("No active and available rooms to delete.");
                return;
            }
        
            System.out.print("Enter the number of the room to delete: ");
            int roomChoice = getValidatedChoice(scanner, 1, deletableRooms.size());
            Room roomToDelete = deletableRooms.get(roomChoice - 1);
        
            System.out.print("Are you sure you want to delete this room? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                roomToDelete.setActive(false);
                saveRoomsToFile(rooms);
                System.out.println("Room deleted successfully.");
            } else {
                System.out.println("Room deletion cancelled.");
            }
        }
        
        private void restoreRoom(Scanner scanner) {
            List<Room> rooms = readRoomsFromFile("rooms.txt");
        
            System.out.println("Existing Inactive Rooms:");
            List<Room> inactiveRooms = new ArrayList<>();
            for (int i = 0; i < rooms.size(); i++) {
                if (!rooms.get(i).isActive()) {
                    inactiveRooms.add(rooms.get(i));
                    System.out.println((inactiveRooms.size()) + ". " + rooms.get(i));
                }
            }
        
            if (inactiveRooms.isEmpty()) {
                System.out.println("No inactive rooms available.");
                return;
            }
        
            System.out.print("Enter the number of the room to restore: ");
            int roomChoice = getValidatedChoice(scanner, 1, inactiveRooms.size());
            Room roomToRestore = inactiveRooms.get(roomChoice - 1);
        
            System.out.print("Are you sure you want to restore this room? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                roomToRestore.setActive(true);
                saveRoomsToFile(rooms);
                System.out.println("Room restored successfully.");
            } else {
                System.out.println("Room restoration cancelled.");
            }
        }
        
        private void deleteAllRooms(Scanner scanner) {
            List<Room> rooms = readRoomsFromFile("rooms.txt");
        
            System.out.println("Existing Active and Available Rooms:");
            List<Room> deletableRooms = new ArrayList<>();
            for (Room room : rooms) {
                if (room.isActive() && room.getRoomStatus().equalsIgnoreCase("available")) {
                    deletableRooms.add(room);
                    System.out.println(deletableRooms.size() + ". " + room);
                }
            }
        
            if (deletableRooms.isEmpty()) {
                System.out.println("No active and available rooms to delete.");
                return;
            }
        
            System.out.print("Are you sure you want to delete all active and available rooms? This action cannot be undone. You can restore all rooms from the menu. (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                for (Room room : deletableRooms) {
                    room.setActive(false);
                }
                saveRoomsToFile(rooms);
                System.out.println("All active and available rooms deleted successfully.");
            } else {
                System.out.println("Delete all rooms cancelled.");
            }
        }
        
        private void restoreAllRooms(Scanner scanner) {
            List<Room> rooms = readRoomsFromFile("rooms.txt");
        
            System.out.println("Existing Inactive Rooms:");
            List<Room> inactiveRooms = new ArrayList<>();
            for (Room room : rooms) {
                if (!room.isActive()) {
                    inactiveRooms.add(room);
                    System.out.println(inactiveRooms.size() + ". " + room);
                }
            }
        
            if (inactiveRooms.isEmpty()) {
                System.out.println("No inactive rooms available.");
                return;
            }
        
            System.out.print("Are you sure you want to restore all inactive rooms? This action will restore the following rooms: ");
            System.out.print("(yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                for (Room room : inactiveRooms) {
                    room.setActive(true);
                }
                saveRoomsToFile(rooms);
                System.out.println("All inactive rooms restored successfully.");
            } else {
                System.out.println("Restore all rooms cancelled.");
            }
        }

        private List<Room> readRoomsFromFile(String filename) {
            List<Room> rooms = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        Room room = new Room(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]), Boolean.parseBoolean(parts[6]));
                        rooms.add(room);
                    }
                }
            } catch (IOException e) {
            }
            return rooms;
        }

        private void saveRoomsToFile(List<Room> rooms) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("rooms.txt"))) {
                for (Room room : rooms) {
                    writer.write(room.toString());
                    writer.newLine();
                }
                System.out.println("Rooms updated successfully.");
            } catch (IOException e) {
            }
        }

        private int getValidatedChoice(Scanner scanner, int min, int max) {
            int choice = -1;
            while (choice < min || choice > max) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (choice < min || choice > max) {
                        System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    scanner.nextLine(); // Consume invalid input
                }
            }
            return choice;
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

        public Staff(String[] parts) {
            super(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Boolean.parseBoolean(parts[7]));
            this.dateOfApproval = parts[5];
        }

        @Override
        public String toString() {
            return "UserID: " + getUserID() + ", IC/Passport Number: " + getIcPassportNumber() + ", Username: " + getUsername() + ", Contact Number: " + getContactNumber() + ", Date of Registration: " + getDateOfRegistration() + ", Role: " + getRole() + ", IsActive: " + getIsActive() + ", Date of Approval: " + dateOfApproval;
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

        // Define a single Scanner instance at the Staff class level
        private static final Scanner scanner = new Scanner(System.in);

        @Override
        public void displayMenu() {
            System.out.println("Staff Menu:");
            System.out.println("1. Update Personal Information");
            System.out.println("2. Make Payment for Resident");
            System.out.println("3. Generate Receipt");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
        
            int choice = getValidatedChoice(scanner, 1, 4);
        
            switch (choice) {
                case 1 -> updatePersonalInformation();
                case 2 -> makePayment(); // Make Payment for Resident logic
                case 3 -> generateReceipt(); // Generate Receipt logic
                case 4 -> {
                    System.out.println("Logging out...");
                    System.out.println("You have been logged out successfully.");
                    displayWelcomePage();
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
            displayMenu();
        }

        public void updatePersonalInformation() {
            int choice;
        
            do {
                System.out.println("Update Personal Information:");
                System.out.println("1. Update IC Passport Number");
                System.out.println("2. Update Username");
                System.out.println("3. Update Password");
                System.out.println("4. Update Contact Number");
                System.out.println("0. Go Back to Staff Menu");
                System.out.print("Enter your choice: ");
                choice = getValidatedChoice(scanner, 0, 4);
        
                switch (choice) {
                    case 1 -> {
                        System.out.println("Current IC Passport Number: " + this.icPassportNumber);
                        while (true) {
                            System.out.print("Enter new IC Passport Number: ");
                            String newIcPassportNumber = scanner.nextLine();
                            if (!isValidICPassport(newIcPassportNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.icPassportNumber = newIcPassportNumber;
                            System.out.println("IC Passport Number updated successfully.");
                            break;
                        }
                    }
                    case 2 -> {
                        System.out.println("Current Username: " + this.username);
                        while (true) {
                            System.out.print("Enter new username: ");
                            String newUsername = scanner.nextLine();
                            if (!isValidUsername(newUsername)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.username = newUsername;
                            System.out.println("Username updated successfully.");
                            break;
                        }
                    }
                    case 3 -> {
                        System.out.println("Current Password: " + this.password);
                        while (true) {
                            System.out.print("Enter new password: ");
                            String newPassword = scanner.nextLine();
                            if (!isValidPassword(newPassword, this.username)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.password = newPassword;
                            System.out.println("Password updated successfully.");
                            break;
                        }
                    }
                    case 4 -> {
                        System.out.println("Current Contact Number: " + this.contactNumber);
                        while (true) {
                            System.out.print("Enter new contact number: ");
                            String newContactNumber = scanner.nextLine();
                            if (!isValidContactNumber(newContactNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.contactNumber = newContactNumber;
                            System.out.println("Contact number updated successfully.");
                            break;
                        }
                    }
                    case 0 -> {
                        System.out.println("Returning to Staff Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        
                try {
                    updateFile("approved_staffs.txt");
                    updateFile("users.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
        }
        
        
        private void updateFile(String filename) throws IOException {
            List<User> users = User.readFromFile(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    if (user.getUserID().equals(this.userID)) {
                        if (filename.equals("users.txt")) {
                            writer.write(this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive);
                        } else if (user instanceof Staff staff) {
                            writer.write(staff.getStaffID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident resident) {
                            writer.write(resident.getResidentID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + resident.getDateOfApproval());
                        }
                    } else {
                        if (filename.equals("users.txt")) {
                            writer.write(user.getUserID() + "," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive());
                        } else if (user instanceof Staff staff) {
                            writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident resident) {
                            writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + resident.getDateOfApproval());
                        }
                    }
                    writer.newLine();
                }
            }
        }

        public void makePayment() {
            List<String[]> payments = new ArrayList<>();
        
            // Read payments from file
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    payments.add(line.split(","));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Display pending payments in brief
            System.out.println("Pending Payments:");
            List<String[]> pendingPayments = new ArrayList<>();
            for (String[] payment : payments) {
                if (payment[7].equalsIgnoreCase("pending")) {
                    pendingPayments.add(payment);
                    System.out.println(pendingPayments.size() + ". Payment ID: " + payment[0] + ", Resident ID: " + payment[1] + ", Amount: " + payment[6]);
                }
            }
        
            if (pendingPayments.isEmpty()) {
                System.out.println("No pending payments found.");
                return;
            }
        
            // Select payment to update
            System.out.print("Enter the number of the payment to update: ");
            int paymentIndex = getValidatedChoice(scanner, 1, pendingPayments.size()) - 1;
        
            // Show selected payment in detail
            String[] selectedPayment = pendingPayments.get(paymentIndex);
            System.out.println("Selected Payment Details:");
            System.out.println("Payment ID: " + selectedPayment[0]);
            System.out.println("Resident ID: " + selectedPayment[1]);
            System.out.println("Staff ID: " + selectedPayment[2]);
            System.out.println("Start Date: " + selectedPayment[3]);
            System.out.println("End Date: " + selectedPayment[4]);
            System.out.println("Room ID: " + selectedPayment[5]);
            System.out.println("Payment Amount: " + selectedPayment[6]);
            System.out.println("Payment Status: " + selectedPayment[7]);
            System.out.println("Booking DateTime: " + selectedPayment[8]);
            System.out.println("Payment Method: " + selectedPayment[9]);
            System.out.println("Booking Status: " + selectedPayment[10]);
        
            // Confirm update
            String confirmation = "";
            while (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                System.out.print("Do you want to update this payment? (yes/no): ");
                confirmation = scanner.nextLine();
                if (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        
            if (confirmation.equalsIgnoreCase("no")) {
                System.out.println("Payment update cancelled.");
                return;
            }
        
            // Update payment status and booking status
            selectedPayment[2] = this.staffID; // Update staffID
            selectedPayment[7] = "paid"; // Update payment status
        
            // Update the corresponding room's status
            List<Room> rooms = readRoomsFromFile("rooms.txt");
            for (Room room : rooms) {
                if (room.getRoomID().equals(selectedPayment[5])) {
                    room.setRoomStatus("available");
                    break;
                }
            }
            saveRoomsToFile(rooms);
        
            // Write updated payments back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt"))) {
                for (String[] payment : payments) {
                    writer.write(String.join(",", payment));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            System.out.println("Payment updated successfully.");
        }

        private List<Room> readRoomsFromFile(String filename) {
            List<Room> rooms = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        Room room = new Room(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]), Boolean.parseBoolean(parts[6]));
                        rooms.add(room);
                    }
                }
            } catch (IOException e) {
            }
            return rooms;
        }

        private void saveRoomsToFile(List<Room> rooms) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("rooms.txt"))) {
                for (Room room : rooms) {
                    writer.write(room.toString());
                    writer.newLine();
                }
                System.out.println("Rooms updated successfully.");
            } catch (IOException e) {
            }
        }

        public void generateReceipt() {
            List<String[]> payments = new ArrayList<>();
            List<String[]> receipts = new ArrayList<>();
        
            // Read payments from file
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    payments.add(line.split(","));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Read receipts from file
            try (BufferedReader reader = new BufferedReader(new FileReader("receipts.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    receipts.add(line.split(","));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Filter payments
            List<String[]> eligiblePayments = new ArrayList<>();
            for (String[] payment : payments) {
                if (payment[2] != null && !payment[2].isEmpty() && payment[7].equalsIgnoreCase("paid") && payment[10].equalsIgnoreCase("active")) {
                    eligiblePayments.add(payment);
                }
            }
        
            if (eligiblePayments.isEmpty()) {
                System.out.println("No eligible payments found.");
                return;
            }
        
            // Display eligible payments in brief
            System.out.println("Eligible Payments:");
            for (int i = 0; i < eligiblePayments.size(); i++) {
                String[] payment = eligiblePayments.get(i);
                System.out.println((i + 1) + ". Payment ID: " + payment[0] + ", Resident ID: " + payment[1] + ", Amount: " + payment[6]);
            }
        
            // Select payment to generate receipt
            System.out.print("Enter the number of the payment to generate receipt: ");
            int paymentIndex = getValidatedChoice(scanner, 1, eligiblePayments.size()) - 1;
        
            // Show selected payment in detail
            String[] selectedPayment = eligiblePayments.get(paymentIndex);
            System.out.println("Selected Payment Details:");
            System.out.println("Payment ID: " + selectedPayment[0]);
            System.out.println("Resident ID: " + selectedPayment[1]);
            System.out.println("Staff ID: " + selectedPayment[2]);
            System.out.println("Start Date: " + selectedPayment[3]);
            System.out.println("End Date: " + selectedPayment[4]);
            System.out.println("Room ID: " + selectedPayment[5]);
            System.out.println("Payment Amount: " + selectedPayment[6]);
            System.out.println("Payment Status: " + selectedPayment[7]);
            System.out.println("Booking DateTime: " + selectedPayment[8]);
            System.out.println("Payment Method: " + selectedPayment[9]);
            System.out.println("Booking Status: " + selectedPayment[10]);
        
            // Confirm receipt generation
            String confirmation = "";
            while (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                System.out.print("Do you want to generate a receipt for this payment? (yes/no): ");
                confirmation = scanner.nextLine();
                if (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        
            if (confirmation.equalsIgnoreCase("no")) {
                System.out.println("Receipt generation cancelled.");
                return;
            }
        
            // Generate receipt
            String receiptID = "RC" + String.format("%02d", receipts.size() + 1);
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String[] newReceipt = {receiptID, selectedPayment[0], this.staffID, currentDateTime};
            receipts.add(newReceipt);
        
            // Update booking status to completed
            selectedPayment[10] = "completed";
        
            // Write updated receipts back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipts.txt"))) {
                for (String[] receipt : receipts) {
                    writer.write(String.join(",", receipt));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            // Write updated payments back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt"))) {
                for (String[] payment : payments) {
                    writer.write(String.join(",", payment));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            System.out.println("Receipt generated successfully.");
        }

        private int getValidatedChoice(Scanner scanner, int min, int max) {
            int choice = -1;
            while (choice < min || choice > max) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (choice < min || choice > max) {
                        System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    scanner.nextLine(); // Consume invalid input
                }
            }
            return choice;
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

        public Resident(String[] parts) {
            super(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Boolean.parseBoolean(parts[7]));
            this.dateOfApproval = parts[5];
        }

        @Override
        public String toString() {
            return "UserID: " + getUserID() + ", IC/Passport Number: " + getIcPassportNumber() + ", Username: " + getUsername() + ", Contact Number: " + getContactNumber() + ", Date of Registration: " + getDateOfRegistration() + ", Role: " + getRole() + ", IsActive: " + getIsActive() + ", Date of Approval: " + dateOfApproval;
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

        // Define a single Scanner instance at the Resident class level
        private static final Scanner scanner = new Scanner(System.in);

        @Override
        public void displayMenu() {
            // Resident-specific menu implementation
            System.out.println("Resident Menu:");
            System.out.println("1. Update Personal Information");
            System.out.println("2. View Payment Records");
            System.out.println("3. Manage Bookings");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
        
            int choice = getValidatedChoice(scanner, 1, 4);
        
            switch (choice) {
                case 1 -> updatePersonalInformation();
                case 2 -> viewPaymentRecords();
                case 3 -> manageBookings();
                case 4 -> residentLogout();
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    displayMenu(); // Recursively call to retry
                }
            }
            displayMenu();
        }

        public void updatePersonalInformation() {
            int choice;
        
            do {
                System.out.println("Update Personal Information:");
                System.out.println("1. Update IC Passport Number");
                System.out.println("2. Update Username");
                System.out.println("3. Update Password");
                System.out.println("4. Update Contact Number");
                System.out.println("0. Go Back to Resident Menu");
                System.out.print("Enter your choice: ");
                choice = getValidatedChoice(scanner, 0, 4);
        
                switch (choice) {
                    case 1 -> {
                        System.out.println("Current IC Passport Number: " + this.icPassportNumber);
                        while (true) {
                            System.out.print("Enter new IC Passport Number: ");
                            String newIcPassportNumber = scanner.nextLine();
                            if (!isValidICPassport(newIcPassportNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.icPassportNumber = newIcPassportNumber;
                            System.out.println("IC Passport Number updated successfully.");
                            break;
                        }
                    }
                    case 2 -> {
                        System.out.println("Current Username: " + this.username);
                        while (true) {
                            System.out.print("Enter new username: ");
                            String newUsername = scanner.nextLine();
                            if (!isValidUsername(newUsername)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.username = newUsername;
                            System.out.println("Username updated successfully.");
                            break;
                        }
                    }
                    case 3 -> {
                        System.out.println("Current Password: " + this.password);
                        while (true) {
                            System.out.print("Enter new password: ");
                            String newPassword = scanner.nextLine();
                            if (!isValidPassword(newPassword, this.username)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.password = newPassword;
                            System.out.println("Password updated successfully.");
                            break;
                        }
                    }
                    case 4 -> {
                        System.out.println("Current Contact Number: " + this.contactNumber);
                        while (true) {
                            System.out.print("Enter new contact number: ");
                            String newContactNumber = scanner.nextLine();
                            if (!isValidContactNumber(newContactNumber)) {
                                System.out.print("Do you want to try again? (yes/no): ");
                                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                                    return;
                                }
                                continue;
                            }
                            this.contactNumber = newContactNumber;
                            System.out.println("Contact number updated successfully.");
                            break;
                        }
                    }
                    case 0 -> {
                        System.out.println("Returning to Resident Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        
                try {
                    updateFile("approved_residents.txt");
                    updateFile("users.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (choice != 0);
        }
        
        private void updateFile(String filename) throws IOException {
            List<User> users = User.readFromFile(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (User user : users) {
                    if (user.getUserID().equals(this.userID)) {
                        if (filename.equals("users.txt")) {
                            writer.write(this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive);
                        } else if (user instanceof Staff staff) {
                            writer.write(staff.getStaffID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident resident) {
                            writer.write(resident.getResidentID() + "," + this.userID + "," + this.icPassportNumber + "," + this.username + "," + this.password + "," + this.contactNumber + "," + this.dateOfRegistration + "," + this.role + "," + this.isActive + "," + resident.getDateOfApproval());
                        }
                    } else {
                        if (filename.equals("users.txt")) {
                            writer.write(user.getUserID() + "," + user.getIcPassportNumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getContactNumber() + "," + user.getDateOfRegistration() + "," + user.getRole() + "," + user.getIsActive());
                        } else if (user instanceof Staff staff) {
                            writer.write(staff.getStaffID() + "," + staff.getUserID() + "," + staff.getIcPassportNumber() + "," + staff.getUsername() + "," + staff.getPassword() + "," + staff.getContactNumber() + "," + staff.getDateOfRegistration() + "," + staff.getRole() + "," + staff.getIsActive() + "," + staff.getDateOfApproval());
                        } else if (user instanceof Resident resident) {
                            writer.write(resident.getResidentID() + "," + resident.getUserID() + "," + resident.getIcPassportNumber() + "," + resident.getUsername() + "," + resident.getPassword() + "," + resident.getContactNumber() + "," + resident.getDateOfRegistration() + "," + resident.getRole() + "," + resident.getIsActive() + "," + resident.getDateOfApproval());
                        }
                    }
                    writer.newLine();
                }
            }
        }
    

        public void viewPaymentRecords() {
            System.out.println("Payment Records:");
            String residentID = this.getResidentID(); 
        
            // Read room data from rooms.txt and store it in a map
            Map<String, String> roomMap = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        roomMap.put(parts[0], parts[3]); // Assuming parts[0] is RoomID and parts[3] is RoomNumber
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }
        
            List<String[]> relevantPayments = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] details = line.split(",");
                    if (details[1].equals(residentID) && !details[7].equals("unpaid") && !details[10].equals("null")) { // Assuming the second element is the residentID, eighth is PaymentStatus, and eleventh is PaymentMethod
                        relevantPayments.add(details);
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the payment records.");
            }
        
            if (relevantPayments.isEmpty()) {
                System.out.println("No payment records found for your account.");
                return;
            }
        
            // Display the list of relevant payment records
            System.out.println("Choose which Payment Record to view:");
            for (int i = 0; i < relevantPayments.size(); i++) {
                String[] details = relevantPayments.get(i);
                System.out.printf("%d. Payment ID: %s, Payment Amount: RM %s, Booking Date: %s%n", i + 1, details[0], details[6], details[8]);
            }
        
            // Get the user's choice
            System.out.printf("Enter your choice (1-%d): ", relevantPayments.size());
            int choice = getValidatedChoice(scanner, 1, relevantPayments.size());
        
            // Display the selected payment record in detail
            String[] selectedDetails = relevantPayments.get(choice - 1);
            String roomNumber = roomMap.getOrDefault(selectedDetails[5], "Unknown Room"); // Assuming the sixth element is RoomID
            LocalDate startDate = LocalDate.parse(selectedDetails[3]); // Assuming the fourth element is StartDate
            LocalDate endDate = LocalDate.parse(selectedDetails[4]); // Assuming the fifth element is EndDate
            long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);
            System.out.println("Payment ID: " + selectedDetails[0]);
            System.out.println("Payment Status: " + selectedDetails[7]);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Stay Duration: " + stayDuration + " days");
            System.out.println("Payment Amount: " + selectedDetails[6]);
            System.out.println("Booking Date: " + selectedDetails[8]);
            System.out.println("Room Number: " + roomNumber);
            System.out.println("Payment Method: " + selectedDetails[9]);
            System.out.println("Booking Status: " + selectedDetails[10]);
            System.out.println("-----------------------------");
        }

        public void manageBookings() {
            int choice;
        
            do {
                System.out.println("Manage Bookings:");
                System.out.println("1. Make Booking");
                System.out.println("2. Make Payment for Booking");
                System.out.println("3. Cancel Booking");
                System.out.println("0. Go Back to Resident Menu");
                System.out.print("Enter your choice: ");
                
                choice = getValidatedChoice(scanner, 0, 3);
        
                switch (choice) {
                    case 1 -> makeBooking();
                    case 2 -> makePaymentForBooking();
                    case 3 -> cancelBooking();
                    case 0 -> {
                        System.out.println("Returning to Resident Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);
        }
        

        public void makePaymentForBooking() {
            List<String[]> payments = new ArrayList<>();
            String residentID = this.getResidentID(); // Get the currently logged-in resident's ID
        
            // Read payments from file
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    payments.add(line.split(","));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Read room data from rooms.txt and store it in a map
            Map<String, String> roomMap = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        roomMap.put(parts[0], parts[3]); // Assuming parts[0] is RoomID and parts[3] is RoomNumber
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }
        
            // Filter unpaid bookings for the current resident, excluding cancelled bookings
            List<String[]> unpaidBookings = new ArrayList<>();
            for (String[] payment : payments) {
                if (payment[1].equals(residentID) && payment[7].equals("unpaid") && !payment[10].equals("cancelled")) {
                    unpaidBookings.add(payment);
                }
            }
        
            if (unpaidBookings.isEmpty()) {
                System.out.println("No unpaid bookings found.");
                return;
            }
        
            // Display unpaid bookings
            System.out.println("Unpaid Bookings:");
            for (int i = 0; i < unpaidBookings.size(); i++) {
                String[] booking = unpaidBookings.get(i);
                long daysBetween = ChronoUnit.DAYS.between(LocalDate.parse(booking[3]), LocalDate.parse(booking[4]));
                String roomNumber = roomMap.getOrDefault(booking[5], "Unknown Room"); // Assuming the sixth element is RoomID
                System.out.printf("%d. PaymentID: %s, ResidentID: %s, Room Number: %s, Stay Duration: %d days, Payment Amount: RM %s%n", 
                                  i + 1, booking[0], booking[1], roomNumber, daysBetween, booking[6]);
            }
        
            // Select booking to pay for
            System.out.printf("Enter the number of the booking to pay for (1-%d): ", unpaidBookings.size());
            int bookingIndex = getValidatedChoice(scanner, 1, unpaidBookings.size()) - 1;
        
            // Display selected booking details
            String[] selectedBooking = unpaidBookings.get(bookingIndex);
            System.out.println("Payment Details:");
            System.out.println("PaymentID: " + selectedBooking[0]);
            System.out.println("ResidentID: " + selectedBooking[1]);
            System.out.println("StaffID: " + selectedBooking[2]);
            System.out.println("Start Date: " + selectedBooking[3]);
            System.out.println("End Date: " + selectedBooking[4]);
            System.out.println("Stay Duration: " + ChronoUnit.DAYS.between(LocalDate.parse(selectedBooking[3]), LocalDate.parse(selectedBooking[4])) + " days");
            System.out.println("RoomID: " + selectedBooking[5]);
            System.out.println("Payment Amount: " + selectedBooking[6]);
            System.out.println("Payment Status: " + selectedBooking[7]);
            System.out.println("Booking Date and Time: " + selectedBooking[8]);
            System.out.println("Payment Method: " + selectedBooking[9]);
            System.out.println("Booking Status: " + selectedBooking[10]);
            System.out.println("=====================");
        
            // Select payment method
            String paymentMethod = "";
            while (true) {
                System.out.println("Select Payment Method:");
                System.out.println("1. Credit Card");
                System.out.println("2. Bank Transfer");
                System.out.println("3. Cash");
                System.out.print("Enter your choice: ");
                int paymentMethodChoice = getValidatedChoice(scanner, 1, 3);
        
                switch (paymentMethodChoice) {
                    case 1 -> paymentMethod = "credit_card";
                    case 2 -> paymentMethod = "bank_transfer";
                    case 3 -> paymentMethod = "cash";
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                    }
                }
                break;
            }
        
            // Confirm payment
            String confirmation = "";
            while (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                System.out.print("Do you want to proceed with the payment? (yes/no): ");
                confirmation = scanner.nextLine();
                if (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Payment cancelled.");
                return;
            }
        
            // Update payment status and method
            selectedBooking[7] = "pending"; // Update payment status to pending
            selectedBooking[9] = paymentMethod; // Update payment method
        
            // Write updated payments back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt"))) {
                for (String[] payment : payments) {
                    writer.write(String.join(",", payment));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            System.out.println("Your payment is successful.");
        }

        public void cancelBooking() {
            List<String[]> payments = new ArrayList<>();
            String residentID = this.getResidentID(); // Get the currently logged-in resident's ID
        
            // Read payments from file
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    payments.add(line.split(","));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Filter unpaid bookings for the current resident, excluding cancelled bookings
            List<String[]> unpaidBookings = new ArrayList<>();
            for (String[] payment : payments) {
                if (payment[1].equals(residentID) && payment[7].equals("unpaid") && !payment[10].equals("cancelled")) {
                    unpaidBookings.add(payment);
                }
            }
        
            if (unpaidBookings.isEmpty()) {
                System.out.println("No unpaid bookings found.");
                return;
            }
        
            // Display unpaid bookings
            System.out.println("Unpaid Bookings:");
            for (int i = 0; i < unpaidBookings.size(); i++) {
                String[] booking = unpaidBookings.get(i);
                long daysBetween = ChronoUnit.DAYS.between(LocalDate.parse(booking[3]), LocalDate.parse(booking[4]));
                System.out.printf("Booking %d: Payment ID: %s, Room Number: %s, Stay Duration: %d Days, Payment Amount: RM %s, Booking DateTime: %s%n",
                        i + 1, booking[0], booking[5], daysBetween, booking[6], booking[8]);
            }
        
            // Select booking to cancel
            System.out.print("Enter the number of the booking to cancel: ");
            int bookingIndex = getValidatedChoice(scanner, 1, unpaidBookings.size()) - 1;
        
            // Show selected booking in detail
            String[] selectedBooking = unpaidBookings.get(bookingIndex);
            System.out.println("Selected Booking Details:");
            System.out.println("Payment ID: " + selectedBooking[0]);
            System.out.println("Resident ID: " + selectedBooking[1]);
            System.out.println("Staff ID: " + selectedBooking[2]);
            System.out.println("Start Date: " + selectedBooking[3]);
            System.out.println("End Date: " + selectedBooking[4]);
            System.out.println("Stay Duration: " + ChronoUnit.DAYS.between(LocalDate.parse(selectedBooking[3]), LocalDate.parse(selectedBooking[4])) + " days");
            System.out.println("Room ID: " + selectedBooking[5]);
            System.out.println("Payment Amount: " + selectedBooking[6]);
            System.out.println("Payment Status: " + selectedBooking[7]);
            System.out.println("Booking DateTime: " + selectedBooking[8]);
            System.out.println("Payment Method: " + selectedBooking[9]);
            System.out.println("Booking Status: " + selectedBooking[10]);
        
            // Confirm cancellation
            String confirmation = "";
            while (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                System.out.print("Do you want to proceed with the cancellation? (yes/no): ");
                confirmation = scanner.nextLine();
                if (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no")) {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Cancellation cancelled.");
                return;
            }
        
            // Update the selected booking's status to "cancelled"
            selectedBooking[10] = "cancelled"; // Assuming the last element is Booking Status
        
            // Write updated payments back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt"))) {
                for (String[] payment : payments) {
                    writer.write(String.join(",", payment));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            // Update room status to available
            String roomID = selectedBooking[5]; // Assuming roomID is at index 5
            List<String[]> rooms = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(roomID)) {
                        parts[4] = "available"; // Assuming parts[4] is RoomStatus
                    }
                    rooms.add(parts);
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }
        
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("rooms.txt"))) {
                for (String[] room : rooms) {
                    writer.write(String.join(",", room));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("An error occurred while updating the room data.");
            }
        
            System.out.println("This booking has been successfully cancelled.");
        }      

        public void makeBooking() {
            // Display room pricing based on fee rates in rooms.txt
            displayRoomPricing();
        
            System.out.println("Room Pricing");
            System.out.println("Room Type\t\tCapacity");
            System.out.println("1. Standard\t\t1");
            System.out.println("2. Large\t\t3");
            System.out.println("3. Family\t\t6");
            System.out.print("Enter your choice: ");
            int roomTypeChoice = getValidatedChoice(scanner, 1, 3);
        
            String roomType = null;
            switch (roomTypeChoice) {
                case 1 -> roomType = "standard";
                case 2 -> roomType = "large";
                case 3 -> roomType = "family";
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    return;
                }
            }
        
            // Select an available room based on roomType
            String roomID = selectAvailableRoomByType(roomType);
            if (roomID == null) {
                System.out.println("No available rooms of the selected type.");
                return;
            }
        
            // Get the fee rate ID from the selected room
            String feeRateID = null;
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(roomID)) {
                        feeRateID = parts[1]; // Assuming parts[1] is FeeRateID
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
                return;
            }
        
            LocalDate startDate = null;
            LocalDate endDate = null;
            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
            LocalDate currentDate = LocalDate.now();
        
            // Prompt for start date
            while (startDate == null) {
                System.out.print("Enter start date of your stay (yyyy-MM-dd): ");
                String startDateInput = scanner.nextLine();
                if (startDateInput.matches(datePattern)) {
                    try {
                        String[] dateParts = startDateInput.split("-");
                        int year = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);
                        int day = Integer.parseInt(dateParts[2]);
                        if (year == 0 || month == 0 || day == 0) {
                            throw new DateTimeParseException("Invalid date components", startDateInput, 0);
                        }
                        if (isInvalidDate(year, month, day)) {
                            throw new DateTimeParseException("Invalid day for the month", startDateInput, 0);
                        }
                        LocalDate parsedDate = LocalDate.of(year, month, day);
                        startDate = parsedDate;
                        if (startDate.isBefore(currentDate)) {
                            System.out.println("You cannot travel back in time. Please enter a valid start date.");
                            startDate = null;
                        }
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.out.println("This date does not exist, please input a valid date.");
                        startDate = null;
                    }
                } else {
                    System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
                }
            }
        
            // Prompt for end date
            while (endDate == null) {
                System.out.print("Enter end date of your stay (yyyy-MM-dd): ");
                String endDateInput = scanner.nextLine();
                if (endDateInput.matches(datePattern)) {
                    try {
                        String[] dateParts = endDateInput.split("-");
                        int year = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);
                        int day = Integer.parseInt(dateParts[2]);
                        if (year == 0 || month == 0 || day == 0) {
                            throw new DateTimeParseException("Invalid date components", endDateInput, 0);
                        }
                        if (isInvalidDate(year, month, day)) {
                            throw new DateTimeParseException("Invalid day for the month", endDateInput, 0);
                        }
                        LocalDate parsedDate = LocalDate.of(year, month, day);
                        endDate = parsedDate;
                        if (!endDate.isAfter(startDate)) {
                            System.out.println("The end date must be after the start date.");
                            endDate = null;
                        }
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.out.println("This date does not exist, please input a valid date.");
                        endDate = null;
                    }
                } else {
                    System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
                }
            }
        
            // Generate a new PaymentID
            String paymentID = generatePaymentID();
        
            // Get the ResidentID of the logged-in user
            String residentID = this.getResidentID();
        
            // Calculate the payment amount
            double paymentAmount = calculatePaymentAmount(startDate, endDate, feeRateID);
        
            // Get the current date and time for BookingDateTime
            String bookingDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
            // Add a new line to payments.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt", true))) {
                writer.write(paymentID + "," + residentID + "," + null + "," + startDate + "," + endDate + "," + roomID + "," + paymentAmount + ",unpaid," + bookingDateTime + "," + null + ",active");
                writer.newLine();
                System.out.println("Booking successful.");
            } catch (IOException e) {
                System.out.println("An error occurred while saving the booking.");
            }
        
            // Update room status to unavailable
            updateRoomStatus(roomID, "unavailable");
        
            // Map room IDs to room numbers
            Map<String, String> roomMap = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        roomMap.put(parts[0], parts[3]); // Assuming parts[0] is RoomID and parts[3] is RoomNumber
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }
        
            // Print confirmation message
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            String roomNumber = roomMap.getOrDefault(roomID, "Unknown Room");
            System.out.println("Your Booking :");
            System.out.println("Payment ID : " + paymentID);
            System.out.println("Resident ID : " + residentID);
            System.out.println("Staff ID : null");
            System.out.println("Start Date : " + startDate);
            System.out.println("End Date : " + endDate);
            System.out.println("Stay Duration : " + daysBetween + " days");
            System.out.println("Room Number : " + roomNumber);
            System.out.println("Payment Amount : RM " + paymentAmount);
            System.out.println("Payment Status : unpaid");
            System.out.println("Booking DateTime : " + bookingDateTime);
            System.out.println("Payment Method : null");
            System.out.println("Booking Status : active");
            System.out.println("=========================");
            System.out.println("Please go back to Manage Bookings to make payment for this booking.");
        }

        private void displayRoomPricing() {
            // Map to store room type to fee rate ID
            Map<String, String> roomTypeToFeeRateID = new HashMap<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        roomTypeToFeeRateID.put(parts[2].toLowerCase(), parts[1]); // Assuming parts[2] is RoomType and parts[1] is FeeRateID
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
                return;
            }
        
            // Map to store fee rates
            Map<String, double[]> feeRates = new HashMap<>();
            try (BufferedReader feeRateReader = new BufferedReader(new FileReader("fee_rates.txt"))) {
                String line;
                while ((line = feeRateReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[6].equalsIgnoreCase("true")) { // Check if the fee rate is active
                        String feeRateID = parts[0];
                        double dailyRate = Double.parseDouble(parts[2]);
                        double weeklyRate = Double.parseDouble(parts[3]);
                        double monthlyRate = Double.parseDouble(parts[4]);
                        double yearlyRate = Double.parseDouble(parts[5]);
                        feeRates.put(feeRateID, new double[]{dailyRate, weeklyRate, monthlyRate, yearlyRate});
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the fee rate data.");
                return;
            }
        
            // Display room pricing
            System.out.println("Room Pricing:");
            System.out.printf("%-10s\t%-12s\t%-12s\t%-12s\t%-12s%n", "Room Type", "Daily Rate", "Weekly Rate", "Monthly Rate", "Yearly Rate");
            for (Map.Entry<String, String> entry : roomTypeToFeeRateID.entrySet()) {
                String roomType = entry.getKey();
                String feeRateID = entry.getValue();
                double[] rates = feeRates.get(feeRateID);
                if (rates != null) {
                    System.out.printf("%-10s\tRM %-10.2f\tRM %-10.2f\tRM %-10.2f\tRM %-10.2f%n",
                            roomType.substring(0, 1).toUpperCase() + roomType.substring(1), rates[0], rates[1], rates[2], rates[3]);
                } else {
                    System.out.printf("%-10s\tNo rates found%n", roomType);
                }
            }
        }

        private String selectAvailableRoomByType(String roomType) {
            List<String> availableRooms = new ArrayList<>();
            try (BufferedReader roomReader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = roomReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7 && parts[2].equalsIgnoreCase(roomType) && parts[4].equals("available") && Boolean.parseBoolean(parts[6])) {
                        availableRooms.add(parts[0]); // Assuming parts[0] is RoomID
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }

            if (!availableRooms.isEmpty()) {
                Random random = new Random();
                return availableRooms.get(random.nextInt(availableRooms.size())); // Randomly select an available room
            }
            return null;
        }

        private boolean isInvalidDate(int year, int month, int day) {
            switch (month) {
                case 2 -> {
                    if (day > 29 || (day == 29 && !Year.isLeap(year))) {
                        return true;
                    }
                }
                case 4, 6, 9, 11 -> {
                    if (day > 30) {
                        return true;
                    }
                }
                default -> {
                    if (day > 31) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void updateRoomStatus(String roomID, String status) {
            List<String[]> rooms = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("rooms.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(roomID)) {
                        parts[4] = status; // Assuming parts[4] is RoomStatus
                    }
                    rooms.add(parts);
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the room data.");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("rooms.txt"))) {
                for (String[] room : rooms) {
                    writer.write(String.join(",", room));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("An error occurred while updating the room data.");
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
            }
            return "P" + String.format("%02d", id);
        }

        private double calculatePaymentAmount(LocalDate startDate, LocalDate endDate, String feeRateID) {
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)); // Include the end date
        
            double dailyRate = 0;
            double weeklyRate = 0;
            double monthlyRate = 0;
            double yearlyRate = 0;
        
            try (BufferedReader rateReader = new BufferedReader(new FileReader("fee_rates.txt"))) {
                String line;
                while ((line = rateReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(feeRateID)) {
                        dailyRate = Double.parseDouble(parts[2]);
                        weeklyRate = Double.parseDouble(parts[3]);
                        monthlyRate = Double.parseDouble(parts[4]);
                        yearlyRate = Double.parseDouble(parts[5]);
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the fee rate data.");
            }
        
            long years = totalDays / 365;
            long remainingDaysAfterYears = totalDays % 365;
            long months = remainingDaysAfterYears / 30;
            long remainingDaysAfterMonths = remainingDaysAfterYears % 30;
            long weeks = remainingDaysAfterMonths / 7;
            long remainingDays = remainingDaysAfterMonths % 7;
        
            return (years * yearlyRate) + (months * monthlyRate) + (weeks * weeklyRate) + (remainingDays * dailyRate);
        }

        public void residentLogout() {
            System.out.println("Logging out...");
            
            // Perform any necessary cleanup, such as closing resources or saving state
            // For example:
            // closeDatabaseConnection();
            // saveUserSession();
            
            // Clear user-specific data
            this.userID = null;
            this.icPassportNumber = null;
            this.username = null;
            this.password = null;
            this.contactNumber = null;
            this.dateOfRegistration = null;
            this.role = null;
            this.isActive = false;
            
            System.out.println("You have been logged out successfully.");
            
            // Route back to the main menu
            displayWelcomePage();
        }

        private int getValidatedChoice(Scanner scanner, int min, int max) {
            int choice = -1;
            while (choice < min || choice > max) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (choice < min || choice > max) {
                        System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    scanner.nextLine(); // Consume invalid input
                }
            }
            return choice;
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
        private double dailyRate;
        private double weeklyRate;
        private double monthlyRate;
        private double yearlyRate;
        private boolean isActive;
    
        public FeeRate(String feeRateID, String roomType, double dailyRate, double weeklyRate, double monthlyRate, double yearlyRate, boolean isActive) {
            this.feeRateID = feeRateID;
            this.roomType = roomType;
            this.dailyRate = dailyRate;
            this.weeklyRate = weeklyRate;
            this.monthlyRate = monthlyRate;
            this.yearlyRate = yearlyRate;
            this.isActive = isActive;
        }
    
        public String getFeeRateID() {
            return feeRateID;
        }
    
        public String getRoomType() {
            return roomType;
        }

        public double getDailyRate() {
            return dailyRate;
        }

        public double getWeeklyRate() {
            return weeklyRate;
        }
    
        public double getMonthlyRate() {
            return monthlyRate;
        }

        public double getYearlyRate() {
            return yearlyRate;
        }

        public boolean getIsActive() {
            return isActive;
        }
    
        public boolean isActive() {
            return isActive;
        }
    
        public void setActive(boolean isActive) {
            this.isActive = isActive;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }
    
        public void setDailyRate(double dailyRate) {
            this.dailyRate = dailyRate;
        }
    
        public void setWeeklyRate(double weeklyRate) {
            this.weeklyRate = weeklyRate;
        }
    
        public void setMonthlyRate(double monthlyRate) {
            this.monthlyRate = monthlyRate;
        }
    
        public void setYearlyRate(double yearlyRate) {
            this.yearlyRate = yearlyRate;
        }
    
        @Override
        public String toString() {
            return feeRateID + "," + roomType + "," + dailyRate + "," + weeklyRate + "," + monthlyRate + "," + yearlyRate + "," + isActive;
        }
    
        public void saveToFile(String filename) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(toString());
                writer.newLine();
            }
        }
    
        public double calculateCost(int days) {
            int months = days / 30;
            days %= 30;
            int weeks = days / 7;
            days %= 7;
    
            double cost = (months * monthlyRate) + (weeks * weeklyRate) + (days * dailyRate);
            return cost;
        }
    
        public static List<FeeRate> readFromFile(String filename) throws IOException {
            List<FeeRate> feeRates = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        FeeRate feeRate = new FeeRate(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Boolean.parseBoolean(parts[6]));
                        feeRates.add(feeRate);
                    }
                }
            }
            return feeRates;
        }
    }

    public static class Room {
        private String roomID;
        private String feeRateID;
        private String roomType;
        private int roomNumber;
        private String roomStatus;
        private int roomCapacity;
        private boolean isActive;
    
        public Room(String roomID, String feeRateID, String roomType, int roomNumber, String roomStatus, int roomCapacity, boolean isActive) {
            this.roomID = roomID;
            this.feeRateID = feeRateID;
            this.roomType = roomType;
            this.roomNumber = roomNumber;
            this.roomStatus = roomStatus;
            this.roomCapacity = roomCapacity;
            this.isActive = isActive;
        }
    
        public String getRoomID() {
            return roomID;
        }
    
        public void setRoomID(String roomID) {
            this.roomID = roomID;
        }
    
        public String getFeeRateID() {
            return feeRateID;
        }
    
        public void setFeeRateID(String feeRateID) {
            this.feeRateID = feeRateID;
        }
    
        public String getRoomType() {
            return roomType;
        }
    
        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }
    
        public int getRoomNumber() {
            return roomNumber;
        }
    
        public void setRoomNumber(int roomNumber) {
            this.roomNumber = roomNumber;
        }
    
        public String getRoomStatus() {
            return roomStatus;
        }
    
        public void setRoomStatus(String roomStatus) {
            this.roomStatus = roomStatus;
        }
    
        public int getRoomCapacity() {
            return roomCapacity;
        }
    
        public void setRoomCapacity(int roomCapacity) {
            this.roomCapacity = roomCapacity;
        }
    
        public boolean isActive() {
            return isActive;
        }
    
        public void setActive(boolean active) {
            isActive = active;
        }
    
        @Override
        public String toString() {
            return roomID + "," + feeRateID + "," + roomType + "," + roomNumber + "," + roomStatus + "," + roomCapacity + "," + isActive;
        }
    }




    // Define a single Scanner instance at the APUHostelManagement class level
    private static final Scanner scanner = new Scanner(System.in);

    private static int getValidatedChoice(Scanner scanner, int min, int max) {
            int choice = -1;
            while (choice < min || choice > max) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (choice < min || choice > max) {
                        System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    scanner.nextLine(); // Consume invalid input
                }
            }
            return choice;
        }
    
        // Method to display the welcome page
        public static void displayWelcomePage() {
            System.out.println("Welcome to APU Hostel Management Fees Payment System (AHMFPS)");
            System.out.println("Please choose your role:");
            System.out.println("1. Manager");
            System.out.println("2. Staff");
            System.out.println("3. Resident");
            System.out.print("Enter your choice (1-3): ");
        
            int choice = getValidatedChoice(scanner, 1, 3);
    
        switch (choice) {
            case 1 -> {
                System.out.println("You have chosen Manager.");
                System.out.print("Enter authorization code: ");
                String authCode = scanner.nextLine();
                if (!isValidAuthCode(authCode)) {
                    System.out.println("Invalid authorization code. Access denied.");
                    displayWelcomePage();
                    return;
                }
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int managerChoice = getValidatedChoice(scanner, 1, 2);
                if (managerChoice == 1) {
                    registerManager();
                } else {
                    loginManager();
                }
            }
            case 2 -> {
                System.out.println("You have chosen Staff.");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int staffChoice = getValidatedChoice(scanner, 1, 2);
                if (staffChoice == 1) {
                    registerStaff();
                } else {
                    loginStaff();
                }
            }
            case 3 -> {
                System.out.println("You have chosen Resident.");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice (1-2): ");
                int residentChoice = getValidatedChoice(scanner, 1, 2);
                if (residentChoice == 1) {
                    registerResident();
                } else {
                    loginResident();
                }
            }
            default -> {
                System.out.println("Invalid choice. Please try again.");
                displayWelcomePage(); // Recursively call to retry
            }
        }
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

    // Validation methods
    public static boolean isValidICPassport(String icPassport) {
        String icPassportType;

        // Check IC format
        if (icPassport.length() == 14 && icPassport.charAt(6) == '-' && icPassport.charAt(9) == '-' && icPassport.replace("-", "").matches("\\d+")) {
            icPassportType = "IC";
        }
        // Check Passport format
        else if (icPassport.length() == 9 && Character.isLetter(icPassport.charAt(0)) && icPassport.substring(1).matches("\\d+")) {
            icPassportType = "Passport";
        } else {
            System.out.println("Invalid IC/Passport format. IC format: xxxxxx-xx-xxxx, Passport format: one alphabet followed by 8 numbers.");
            return false;
        }

        // Check uniqueness
        try {
            if (!isUnique(icPassport, "", "")) {
                System.out.println(icPassportType + " number already exists. Please use a different " + icPassportType + " number.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidUsername(String username) {
        // Check length
        if (username.length() < 3 || username.length() > 12) {
            System.out.println("Username must be between 3 and 12 characters long.");
            return false;
        }

        // Check allowed characters
        for (char c : username.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                System.out.println("Username can only contain letters, numbers, and underscores.");
                return false;
            }
        }

        // Check for at least one letter
        if (!username.matches(".*[a-zA-Z]+.*")) {
            System.out.println("Username must contain at least one letter.");
            return false;
        }

        // Check uniqueness
        try {
            if (!isUnique("", username, "")) {
                System.out.println("Username already exists. Please choose a different username.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(String password, String username) {
        // Check length
        if (password.length() < 8 || password.length() > 12) {
            System.out.println("Password must be between 8 and 12 characters long.");
            return false;
        }
    
        // Check if password is similar to username
        if (password.contains(username)) {
            System.out.println("Password cannot be similar to the username.");
            return false;
        }
    
        // Check for at least one number, one special character, and one uppercase letter
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");
    
        // Check for invalid characters
        boolean hasInvalidChar = password.matches(".*[^a-zA-Z0-9!@#$%^&*()].*");
    
        if (!(hasNumber && hasSpecialChar && hasUppercase)) {
            System.out.println("Password must contain at least one number, one special character (!@#$%^&*()), and one uppercase letter.");
            return false;
        }
    
        if (hasInvalidChar) {
            System.out.println("Password contains invalid characters. Only !@#$%^&*() are allowed as special characters.");
            return false;
        }
    
        return true;
    }

    public static boolean isValidContactNumber(String contactNumber) {
        // Check contact number format
        if (contactNumber.length() == 12 && contactNumber.startsWith("01") && contactNumber.charAt(3) == '-' && contactNumber.charAt(7) == '-' && contactNumber.replace("-", "").matches("\\d+")) {
            // Check uniqueness
            try {
                if (!isUnique("", "", contactNumber)) {
                    System.out.println("Contact number already exists. Please choose a different contact number.");
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        } else {
            System.out.println("Invalid contact number format. The correct format is 01X-XXX-XXXX.");
            return false;
        }
    }

    // Method to validate authorization code
    private static boolean isValidAuthCode(String authCode) {
        //check against a predefined list of valid codes
        List<String> validAuthCodes = Arrays.asList("KhongCL", "kcl", "AUTH789");
        return validAuthCodes.contains(authCode);
    }

    // Method to handle Manager registration
    public static void registerManager() {
        String icPassportNumber, username, password, contactNumber;
        boolean isIC = false;
    
        OUTER:
        while (true) {
            System.out.println("Do you want to use IC or Passport Number to register?");
            System.out.println("1. IC");
            System.out.println("2. Passport");
            System.out.print("Enter your choice (1-2): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    isIC = true;
                    break OUTER;
                }
                case "2" -> {
                    isIC = false;
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please enter '1' for IC or '2' for Passport.");
            }
        }
    
        while (true) {
            System.out.print("Enter " + (isIC ? "IC" : "Passport") + " Number: ");
            icPassportNumber = scanner.nextLine();
            if (!isValidICPassport(icPassportNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }
    
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (!isValidUsername(username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }
    
        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!isValidPassword(password, username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }
    
        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (!isValidContactNumber(contactNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }
    
        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String managerID = generateUserID("M");
            Manager manager = new Manager(managerID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "manager", true);
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
            if (user != null && user.getRole().equals("manager")) {
                if (user.getIsActive()) {
                    System.out.println("Login successful.");
                    user.displayMenu();
                } else {
                    System.out.println("Your account is deactivated. Please contact the administrator.");
                    displayWelcomePage();
                }
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
        String icPassportNumber, username, password, contactNumber;
        boolean isIC = false;

        OUTER:
        while (true) {
            System.out.println("Do you want to use IC or Passport Number to register?");
            System.out.println("1. IC");
            System.out.println("2. Passport");
            System.out.print("Enter your choice (1-2): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    isIC = true;
                    break OUTER;
                }
                case "2" -> {
                    isIC = false;
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please enter '1' for IC or '2' for Passport.");
            }
        }

        while (true) {
            System.out.print("Enter " + (isIC ? "IC" : "Passport") + " Number: ");
            icPassportNumber = scanner.nextLine();
            if (!isValidICPassport(icPassportNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (!isValidUsername(username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!isValidPassword(password, username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (!isValidContactNumber(contactNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String staffID = generateUserID("S");
            Staff staff = new Staff(staffID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "staff", true, null);
            staff.saveToStaffFile(null, null, "unapproved_staffs.txt");
            System.out.println("Staff registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Staff login
    public static void loginStaff() {
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
            if (user != null && user.getRole().equals("staff")) {
                if (user.getIsActive()) {
                    System.out.println("Login successful.");
                    user.displayMenu();
                } else {
                    System.out.println("Your account is deactivated. Please contact the administrator.");
                    displayWelcomePage();
                }
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
        String icPassportNumber, username, password, contactNumber;
        boolean isIC = false;

        OUTER:
        while (true) {
            System.out.println("Do you want to use IC or Passport Number to register?");
            System.out.println("1. IC");
            System.out.println("2. Passport");
            System.out.print("Enter your choice (1-2): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    isIC = true;
                    break OUTER;
                }
                case "2" -> {
                    isIC = false;
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please enter '1' for IC or '2' for Passport.");
            }
        }

        while (true) {
            System.out.print("Enter " + (isIC ? "IC" : "Passport") + " Number: ");
            icPassportNumber = scanner.nextLine();
            if (!isValidICPassport(icPassportNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (!isValidUsername(username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!isValidPassword(password, username)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter contact number: ");
            contactNumber = scanner.nextLine();
            if (!isValidContactNumber(contactNumber)) {
                System.out.print("Do you want to try again? (yes/no): ");
                if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                    displayWelcomePage();
                    return;
                }
                continue;
            }
            break;
        }

        try {
            String dateOfRegistration = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userID = generateUserID("U");
            String residentID = generateUserID("R");
            Resident resident = new Resident(residentID, userID, icPassportNumber, username, password, contactNumber, dateOfRegistration, "resident", true, null);
            resident.saveToResidentFile(null, null, "unapproved_residents.txt");
            System.out.println("Resident registered successfully.");
            displayWelcomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Resident login
    public static void loginResident() {
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
            if (user != null && user.getRole().equals("resident")) {
                if (user.getIsActive()) {
                    System.out.println("Login successful.");
                    user.displayMenu();
                } else {
                    System.out.println("Your account is deactivated. Please contact the administrator.");
                    displayWelcomePage();
                }
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
        switch (prefix) {
            case "U" -> filename = "users.txt";
            case "M" -> filename = "managers.txt";
            case "S" -> filename = "approved_staffs.txt";
            case "R" -> filename = "approved_residents.txt";
            default -> {
            }
        }
        
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
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
        }
        return prefix + String.format("%02d", id);
    }

        // Main method to launch the application
    public static void main(String[] args) {
        displayWelcomePage();
    }
}

//test netbean pull push

//test again bruhhellllojj