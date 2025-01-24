package apu.hostel.management;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.swing.JButton;
import javax.swing.JFrame;

public class StaffMakePaymentForResidentGUI {
        private JFrame frame;
    private APUHostelManagement.Staff staff; // Add staff field

    // Add new constructor
    public StaffMakePaymentForResidentGUI(APUHostelManagement.Staff staff) {
        this.staff = staff;
        initialize();
    }

    public StaffMakePaymentForResidentGUI() {
        initialize();
    }

    private void initialize() {

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add Back button
        gbc.gridy++;
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout());


                // Add Back button
                gbc.gridy++;
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(300, 50)); // Set button size
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StaffMainPageGUI(staff); // Pass staff
                frame.dispose();
            }
        });
        buttonPanel.add(backButton, gbc);

    }
    
}
