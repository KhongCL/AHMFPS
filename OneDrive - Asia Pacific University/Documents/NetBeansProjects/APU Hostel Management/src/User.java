import java.io.*;

public abstract class User {
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