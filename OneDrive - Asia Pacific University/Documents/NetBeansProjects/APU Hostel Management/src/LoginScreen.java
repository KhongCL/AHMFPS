import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
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
                    new StaffMenu(user).setVisible(true);
                } else if (role.equals("Resident")) {
                    user = new Resident(username, password);
                    new ResidentMenu(user).setVisible(true);
                }

                if (user != null) {
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Invalid login credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }


}