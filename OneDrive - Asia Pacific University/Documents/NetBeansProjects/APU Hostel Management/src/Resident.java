import java.io.IOException;

public class Resident extends User {

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