package apu.hostel.management;

import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
        backButton.setPreferredSize(new Dimension(125, 50)); 
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentMainPageGUI(resident);
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        makeBookingButton.setMnemonic(KeyEvent.VK_M);
        makePaymentButton.setMnemonic(KeyEvent.VK_P);
        cancelBookingButton.setMnemonic(KeyEvent.VK_C);

        makeBookingButton.setToolTipText("Make a new booking (Alt + M)");
        makePaymentButton.setToolTipText("Make payment for a booking (Alt + P)");
        cancelBookingButton.setToolTipText("Cancel a booking (Alt + C)");

        addButtonHoverEffect(makeBookingButton);
        addButtonHoverEffect(makePaymentButton);
        addButtonHoverEffect(cancelBookingButton);

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

    private void addButtonHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color darkerColor = getDarkerColor(originalColor);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkerColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 2),
                    BorderFactory.createEmptyBorder(3, 13, 3, 13)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(button.getForeground(), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
            }
        });
    }
    
    private Color getDarkerColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 1.1f), Math.max(0, hsb[2] - 0.15f));
    }
    
    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("images/" + iconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setIconTextGap(15);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            
            // Enhanced Material Design colors with better contrast
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Make")) {
                button.setBackground(new Color(200, 230, 201));    // Light Green
                button.setForeground(new Color(46, 125, 50));     // Dark Green
            } else if (text.contains("Cancel")) {
                button.setBackground(new Color(255, 205, 210));    // Light Red
                button.setForeground(new Color(198, 40, 40));     // Dark Red
            }
            
            // Add default border matching text color
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
            ));
            
            // Add shadow effect
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(button.getForeground(), 1),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 14, 4, 14),
                    BorderFactory.createEmptyBorder(0, 0, 2, 0)
                )
            ));
            
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }
}