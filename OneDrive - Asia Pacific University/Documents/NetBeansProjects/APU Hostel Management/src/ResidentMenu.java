import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentMenu extends JFrame {

    public ResidentMenu(User user) {
        setTitle("Resident Menu");
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

        JButton viewPaymentRecordsButton = new JButton("View Payment Records");
        viewPaymentRecordsButton.setBounds(10, 140, 200, 25);
        panel.add(viewPaymentRecordsButton);

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

        viewPaymentRecordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement view payment records logic here
            }
        });
    }
}