import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffMenu extends JFrame {

    public StaffMenu(User user) {
        setTitle("Staff Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setBounds(10, 20, 300, 25);
        panel.add(welcomeLabel);

        JButton registerAccountButton = new JButton("Register Login Account");
        registerAccountButton.setBounds(10, 60, 200, 25);
        panel.add(registerAccountButton);

        JButton updateAccountButton = new JButton("Update Login Account");
        updateAccountButton.setBounds(10, 100, 200, 25);
        panel.add(updateAccountButton);

        JButton makePaymentButton = new JButton("Make Payment for Resident");
        makePaymentButton.setBounds(10, 140, 200, 25);
        panel.add(makePaymentButton);

        JButton generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.setBounds(10, 180, 200, 25);
        panel.add(generateReceiptButton);

        // Add action listeners for buttons
        registerAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement register account logic here
            }
        });

        updateAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement update account logic here
            }
        });

        makePaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement make payment logic here
            }
        });

        generateReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement generate receipt logic here
            }
        });
    }
}