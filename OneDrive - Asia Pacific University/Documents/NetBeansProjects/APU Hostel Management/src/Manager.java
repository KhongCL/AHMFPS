import java.io.IOException;

public class Manager extends User {

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
    public class RegisterUser {
    public static void main(String[] args) {
        User newUser = new Manager("admin", "admin123");
        try {
            newUser.saveToFile();
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }
}
}