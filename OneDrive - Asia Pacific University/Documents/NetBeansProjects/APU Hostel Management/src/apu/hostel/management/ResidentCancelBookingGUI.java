// KHONG CHEE LEONG TP075846
// JUSTIN NG KEN HONG TP073469

package apu.hostel.management;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResidentCancelBookingGUI {
    private JFrame frame;
    private DefaultTableModel tableModel; 
    private Map<Integer, String[]> paymentDetailsMap; 
    private APUHostelManagement.Resident resident; 
    private JTable table;
    private JButton filterButton;
    private JButton sortButton;
    private String currentFilterChoice = null;
    private String currentFilterValue = null;
    private String currentSortCategory = null;
    private String currentSortOrder = null;
    private List<String[]> filteredBookingList;

    public ResidentCancelBookingGUI(APUHostelManagement.Resident resident) {
        this.resident = resident;
        this.filteredBookingList = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Cancel Booking");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout(10, 10)); 
        frame.setTitle("Cancel Booking - " + resident.getUsername());
        frame.setLocationRelativeTo(null);
        
        JButton backButton = createButton("Back", "back_icon.png");
        backButton.setPreferredSize(new Dimension(125, 40));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResidentManageBookingsGUI(resident);
                frame.dispose();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Cancel Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        // Create a panel to hold the table and approve button
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = createButton("Filter", "filter_icon.png");
        sortButton = createButton("Sort", "sort_icon.png");
        JTextField searchField = new JTextField(20);
        searchField.setText("Search payments...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (searchField.getText().equals("Search payments...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search payments...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = createButton("Search", "search_icon.png");
        JButton clearButton = createButton("Clear", "clear_icon.png");

        filterButton.addActionListener(e -> {
            if (filterButton.getText().startsWith("Filter: ")) {
                int choice = JOptionPane.showConfirmDialog(frame,
                    "Do you want to clear the current filter?",
                    "Clear Filter",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    filterButton.setText("Filter");
                    currentFilterChoice = null;
                    currentFilterValue = null;
                    frame.setTitle("Cancel Booking - " + resident.getUsername());
                    if (currentSortCategory != null) {
                        applySorting(new ArrayList<>(paymentDetailsMap.values()));
                    } else {
                        loadPayments();
                    }
                }
            } else {
                filterPayments();
            }
        });

        sortButton.addActionListener(e -> {
            if (sortButton.getText().startsWith("Sort: ")) {
                int choice = JOptionPane.showConfirmDialog(frame,
                    "Do you want to clear the current sort?",
                    "Clear Sort",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    sortButton.setText("Sort");
                    currentSortCategory = null;
                    currentSortOrder = null;
                    if (currentFilterChoice != null) {
                        reapplyCurrentFilter();
                    } else {
                        loadPayments();
                    }
                }
            } else {
                sortPayments();
            }
        });

        searchButton.addActionListener(e -> searchPayments(searchField.getText()));
        clearButton.addActionListener(e -> {
            searchField.setText("Search payments...");
            searchField.setForeground(Color.GRAY);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPayments();
            }
        });

        filterPanel.add(filterButton);
        filterPanel.add(sortButton);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(backButton, BorderLayout.WEST);

        frame.add(topPanel, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(
            new Object[]{"Room Number", "Stay Duration", "Booking Date and Time", "Payment Amount (RM)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30); 
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionBackground(new Color(230, 240, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                return c;
            }
        });

        // Fix column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        List<String[]> cancellableBookings = APUHostelManagement.Resident.getCancellableBookingsForResident(resident.getResidentID());
        Map<String, String> roomMap = APUHostelManagement.Resident.getRoomMap();
        paymentDetailsMap = new HashMap<>(); 
        filteredBookingList = new ArrayList<>();

        int rowIndex = 0;
        for (String[] details : cancellableBookings) {
            paymentDetailsMap.put(rowIndex, details); 
            filteredBookingList.add(details);
            String roomNumber = roomMap.getOrDefault(details[5], "Unknown Room");
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            tableModel.addRow(new Object[]{roomNumber, stayDuration + " days", details[8], "RM" + details[6]});
            rowIndex++;
        }

        
        JButton cancelButton = createButton("Cancel Booking", "cancel_icon.png");
        cancelButton.setPreferredSize(new Dimension(200, 40)); 
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showCancelBookingPopup(filteredBookingList.get(selectedRow), selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a booking to cancel.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancelButton);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        backButton.setMnemonic(KeyEvent.VK_B);
        filterButton.setMnemonic(KeyEvent.VK_F);
        sortButton.setMnemonic(KeyEvent.VK_S);
        searchButton.setMnemonic(KeyEvent.VK_ENTER);
        clearButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setMnemonic(KeyEvent.VK_A);

        // Add tooltips
        backButton.setToolTipText("Go back to manage bookings (Alt+B)");
        filterButton.setToolTipText("Filter bookings (Alt+F)");
        sortButton.setToolTipText("Sort bookings (Alt+S)");
        searchButton.setToolTipText("Search bookings (Enter)");
        clearButton.setToolTipText("Clear search and filters (Alt+C)");
        cancelButton.setToolTipText("Cancel selected booking (Alt+A)");

        // Add button hover effects
        addButtonHoverEffect(backButton);
        addButtonHoverEffect(filterButton);
        addButtonHoverEffect(sortButton);
        addButtonHoverEffect(searchButton);
        addButtonHoverEffect(clearButton);
        addButtonHoverEffect(cancelButton);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showCancelBookingPopup(filteredBookingList.get(row), row);
                    }
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !searchField.getText().equals("Search payments...")) {
                    searchButton.doClick();
                }
            }
        });

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().registerKeyboardAction(e -> {
            backButton.doClick();
        }, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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

    private void showCancelBookingPopup(String[] details, int rowIndex) {
        String[] bookingDetails = filteredBookingList.get(rowIndex);
        String roomNumber = APUHostelManagement.Resident.getRoomMap().getOrDefault(bookingDetails[5], "Unknown Room");
        LocalDate startDate = LocalDate.parse(bookingDetails[3]);
        LocalDate endDate = LocalDate.parse(bookingDetails[4]);
        long stayDuration = ChronoUnit.DAYS.between(startDate, endDate);
        String username = resident.getUsername(); 
        String roomType = APUHostelManagement.Resident.getRoomType(bookingDetails[5]); 

        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("APU HOSTEL PAYMENT DETAILS");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(headerLabel);
        detailsPanel.add(Box.createVerticalStrut(20));

        String[][] detailsArray = {
            {"Payment ID:", details[0]},
            {"Username:", username},
            {"Start Date:", startDate.toString()},
            {"End Date:", endDate.toString()},
            {"Stay Duration:", stayDuration + " days"},
            {"Booking Date and Time:", details[8]},
            {"Room Type:", roomType},
            {"Room Number:", roomNumber},
            {"Booking Status:", details[10]},
            {"Payment Amount:", "RM " + details[6]}
        };

        for (String[] detail : detailsArray) {
            JPanel detailRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            detailRow.add(new JLabel(detail[0]));
            detailRow.add(new JLabel(detail[1]));
            detailsPanel.add(detailRow);
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(detailsPanel, BorderLayout.CENTER);
        JLabel confirmLabel = new JLabel("Are you sure you want to cancel this booking?");
        confirmLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(confirmLabel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Booking Details",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            cancelBooking(details[0], rowIndex, details[5]);
        }
    }

    private void cancelBooking(String paymentID, int rowIndex, String roomID) {
        if (APUHostelManagement.Resident.cancelBooking(paymentID)) {
            JOptionPane.showMessageDialog(frame, "Booking cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            filteredBookingList.remove(rowIndex);
            tableModel.removeRow(rowIndex);
            APUHostelManagement.Resident.updateRoomStatus1(roomID, "available");
            
            if (filteredBookingList.isEmpty()) {
                if (currentFilterChoice != null) {
                    reapplyCurrentFilter();
                } else {
                    loadPayments();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "An error occurred while cancelling the booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPayments() {
        paymentDetailsMap = new HashMap<>();
        tableModel.setRowCount(0);

        filteredBookingList = null;
        currentFilterChoice = null;
        currentFilterValue = null;
        currentSortCategory = null;
        currentSortOrder = null;
        filterButton.setText("Filter");
        sortButton.setText("Sort");
        
        List<String[]> unpaidBookings = APUHostelManagement.Resident.getUnpaidBookingsForResident(resident.getResidentID());
        if (unpaidBookings.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No unpaid bookings found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int rowIndex = 0;
        for (String[] details : unpaidBookings) {
            paymentDetailsMap.put(rowIndex, details);
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(details[5]);
            long stayDuration = ChronoUnit.DAYS.between(LocalDate.parse(details[3]), LocalDate.parse(details[4]));
            
            tableModel.addRow(new Object[]{
                roomNumber,
                stayDuration + " days", 
                details[8],
                "RM" + details[6]
            });
            rowIndex++;
        }
    }

    private void filterPayments() {
        String[] filterOptions = {"All", "Room Number"};
        String filterChoice = (String) JOptionPane.showInputDialog(frame,
            "Filter by:", "Filter Bookings", 
            JOptionPane.QUESTION_MESSAGE, null, filterOptions, filterOptions[0]);

        if (filterChoice == null) return;

        List<String[]> originalData = new ArrayList<>(paymentDetailsMap.values());

        if (filterChoice.equals("All")) {
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            frame.setTitle("Cancel Booking - " + resident.getUsername());
            if (currentSortCategory != null) {
                applySorting(originalData);
            } else {
                updateTable(originalData);
            }
            return;
        }

        Set<String> uniqueRooms = paymentDetailsMap.values().stream()
            .map(p -> APUHostelManagement.Resident.getRoomNumber(p[5]))
            .collect(Collectors.toSet());
        String[] rooms = uniqueRooms.toArray(new String[0]);
        
        String value = (String) JOptionPane.showInputDialog(frame,
            "Select Room Number:", "Filter Bookings",
            JOptionPane.QUESTION_MESSAGE, null, rooms, rooms[0]);
            
        if (value == null) return;

        currentFilterChoice = filterChoice;
        currentFilterValue = value;
        filterButton.setText("Filter: " + value);

        List<String[]> filtered = originalData.stream()
            .filter(booking -> APUHostelManagement.Resident.getRoomNumber(booking[5])
                    .equals(currentFilterValue))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
            "No bookings found for selected filter.",
            "No Results", JOptionPane.INFORMATION_MESSAGE);
            currentFilterChoice = null;
            currentFilterValue = null;
            filterButton.setText("Filter");
            updateTable(originalData);
            return;
        } else {
            frame.setTitle("Cancel Booking - " + resident.getUsername() + 
                " (Filtered: " + value + ")");
            if (currentSortCategory != null) {
                applySorting(filtered);
            } else {
                updateTable(filtered);
            }
        }
    }

    private void sortPayments() {
        String[] options = {
            "Amount (Low-High)", "Amount (High-Low)", 
            "Stay Duration (Shortest)", "Stay Duration (Longest)",
            "Booking Date (Newest)", "Booking Date (Oldest)"
        };
        
        String choice = (String) JOptionPane.showInputDialog(frame, "Sort by:",
            "Sort Bookings", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == null) return;

        currentSortCategory = choice.split(" ")[0];
        currentSortOrder = choice.contains("High-Low") || 
                        choice.contains("Longest") || 
                        choice.contains("Newest") ? "Descending" : "Ascending";
        sortButton.setText("Sort: " + currentSortCategory);

        List<String[]> listToSort = filteredBookingList != null ? 
            filteredBookingList : new ArrayList<>(paymentDetailsMap.values());
        applySorting(listToSort);
    }

    private void searchPayments(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty() || 
            searchQuery.equals("Search payments...")) {
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPayments();
            }
            return;
        }

        String lowerCaseQuery = searchQuery.toLowerCase();
        List<String[]> searchList = filteredBookingList != null ? 
        filteredBookingList : new ArrayList<>(paymentDetailsMap.values());
    
        List<String[]> searchResults = searchList.stream()
            .filter(booking -> {
                String roomNumber = APUHostelManagement.Resident.getRoomNumber(booking[5]);
                long stayDuration = ChronoUnit.DAYS.between(
                    LocalDate.parse(booking[3]), 
                    LocalDate.parse(booking[4])
                );
                
                return roomNumber.toLowerCase().contains(lowerCaseQuery) ||
                    String.valueOf(stayDuration).contains(lowerCaseQuery) ||
                    booking[8].toLowerCase().contains(lowerCaseQuery) ||
                    booking[6].toLowerCase().contains(lowerCaseQuery);
            })
            .collect(Collectors.toList());

        if (!searchResults.isEmpty()) {
            frame.setTitle("Cancel Booking - " + resident.getUsername() + 
                " (Found " + searchResults.size() + " results)");
            updateTable(searchResults);
        } else {
            frame.setTitle("Cancel Booking - " + resident.getUsername());
            JOptionPane.showMessageDialog(frame, 
                "No bookings found matching your search.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice != null) {
                reapplyCurrentFilter();
            } else {
                loadPayments();
            }
        }
    }

    private void reapplyCurrentFilter() {
        if (currentFilterChoice != null) {
            filteredBookingList = paymentDetailsMap.values().stream()
                .filter(booking -> APUHostelManagement.Resident.getRoomNumber(booking[5])
                        .equals(currentFilterValue))
                .collect(Collectors.toList());
                
            if (currentSortCategory != null) {
                applySorting(filteredBookingList);
            } else {
                updateTable(filteredBookingList);
            }
        } else {
            if (currentSortCategory != null) {
                applySorting(new ArrayList<>(paymentDetailsMap.values()));
            } else {
                loadPayments();
            }
        }
    }

    private void applySorting(List<String[]> listToSort) {
        if (currentSortCategory == null || currentSortOrder == null) return;
        
        List<String[]> sortedList = new ArrayList<>(listToSort);
        
        Comparator<String[]> comparator = switch (currentSortCategory) {
            case "Amount" -> Comparator.comparing(p -> Double.parseDouble(p[6]));
            case "Stay" -> Comparator.comparing(p -> ChronoUnit.DAYS.between(
                LocalDate.parse(p[3]), LocalDate.parse(p[4])));
            case "Booking" -> Comparator.comparing(p -> p[8]);
            default -> null;
        };

        if (comparator != null) {
            if (currentSortOrder.equals("Descending")) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            updateTable(sortedList);
        }
    }

    private void updateTable(List<String[]> bookingList) {
        tableModel.setRowCount(0);

        if (bookingList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No bookings found.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            if (currentFilterChoice == null) {
                loadPayments();
            }
            return;
        }

        filteredBookingList = new ArrayList<>(bookingList);
        
        for (String[] booking : bookingList) {
            String roomNumber = APUHostelManagement.Resident.getRoomNumber(booking[5]);
            long stayDuration = ChronoUnit.DAYS.between(
                LocalDate.parse(booking[3]), 
                LocalDate.parse(booking[4])
            );
            
            tableModel.addRow(new Object[]{
                roomNumber,
                stayDuration + " days",
                booking[8],
                "RM" + booking[6]
            });
        }
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
            button.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (text.contains("Back")) {
                button.setBackground(new Color(245, 245, 245));    // Light Gray
                button.setForeground(new Color(66, 66, 66));      // Dark Gray
            } else if (text.contains("Filter") || text.contains("Sort")) {
                button.setBackground(new Color(187, 222, 251));    // Light Blue
                button.setForeground(new Color(25, 118, 210));    // Dark Blue
            } else if (text.contains("Search")) {
                button.setBackground(new Color(225, 190, 231));    // Light Purple
                button.setForeground(new Color(106, 27, 154));    // Dark Purple
            } else if (text.contains("Clear")) {
                button.setBackground(new Color(255, 205, 210));    // Light Red
                button.setForeground(new Color(198, 40, 40));     // Dark Red
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
        
        return button;
    }
}