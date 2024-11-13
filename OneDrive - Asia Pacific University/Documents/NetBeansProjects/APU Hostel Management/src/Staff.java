import java.io.IOException;

public class Staff extends User {

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