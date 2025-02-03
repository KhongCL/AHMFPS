package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResidentManageBookingsGUI {
    private JFrame frame;
    private APUHostelManagement.Resident resident; 

    
    public ResidentManageBookingsGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        initialize();
    }

    public ResidentManageBookingsGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Manage Bookings");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Manage Bookings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        
        JButton makeBookingButton = createButton("Make Booking", "booking_icon.png");
        makeBookingButton.setPreferredSize(new Dimension(300, 50)); 
        makeBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakeBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(makeBookingButton, gbc);

        
        gbc.gridy++;
        JButton makePaymentButton = createButton("Make Payment For Booking", "payment_icon.png");
        makePaymentButton.setPreferredSize(new Dimension(300, 50)); 
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMakePaymentForBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(makePaymentButton, gbc);

        
        gbc.gridy++;
        JButton cancelBookingButton = createButton("Cancel Booking", "cancel_icon.png");
        cancelBookingButton.setPreferredSize(new Dimension(300, 50)); 
        cancelBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentCancelBookingGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(cancelBookingButton, gbc);

        
        gbc.gridy++;
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(300, 50)); 
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int choice = JOptionPane.showConfirmDialog(frame, 
                    "Are you sure you want to close this window?", "Confirm Close",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                
            }
        });
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            
            if (text.contains("Cancel")) {
                button.setBackground(new Color(230, 240, 250)); 
            } else if (text.contains("Make")) {
                button.setBackground(new Color(230, 250, 230)); 
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }

    private void addButtonHoverEffect(JButton button) {
        
        Color originalColor = button.getBackground();
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor); 
            }
        });
    }
}