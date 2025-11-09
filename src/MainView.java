import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

/**
 * The MainView class provides the main JFrame and functionality for the
 * Green Property Exchange System using Java Swing.
 */
public class MainView extends JFrame {

    private final PropertyManager manager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("PHP #,##0.00");
    private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#0.00");

    // Constructor sets up the main frame and all views
    public MainView() {
        super("Green Property Exchange System (MCO2)");
        this.manager = new PropertyManager();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize and add all the primary screens (JPanels)
        mainPanel.add(createMainMenuPanel(), "MAIN_MENU");
        mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY");
        mainPanel.add(createPropertyCreationPanel(), "CREATE_PROPERTY");

        // Add sample data for testing MCO2 features quickly
        manager.addProperty("Eco-Apt 101", 1000.0, 1); // Eco-Apartment (1.0x)
        manager.addProperty("Sustainable Home", 1000.0, 2); // Sustainable House (1.20x)
        manager.bookProperty("Eco-Apt 101", "Test Guest", 5, 8);

        add(mainPanel);
    }

    // --- GUI PANEL FACTORY METHODS ---

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        panel.add(new JLabel("Main Menu", SwingConstants.CENTER) {{
            setFont(new Font("Arial", Font.BOLD, 24));
        }}, gbc);

        // Button to List/Manage Properties
        JButton manageBtn = new JButton("Manage & View Properties");
        manageBtn.addActionListener(e -> {
            // When going to manage screen, recreate/refresh the panel
            mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
            cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
        });
        panel.add(manageBtn, gbc);

        // Button to Create Property
        JButton createBtn = new JButton("Create New Property Listing");
        createBtn.addActionListener(e -> cardLayout.show(mainPanel, "CREATE_PROPERTY"));
        panel.add(createBtn, gbc);

        // Button to Exit
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn, gbc);

        return panel;
    }

    private JPanel createPropertyCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Create New Property"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Create Property Listing", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Property Name Field
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Property Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; form.add(nameField, gbc);

        // Base Price Field
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Base Price (PHP >= 100):"), gbc);
        JTextField priceField = new JTextField("1500.00", 20);
        gbc.gridx = 1; form.add(priceField, gbc);

        // Property Type Selection (MCO2 Requirement)
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Property Type:"), gbc);
        String[] types = {"1. Eco-Apartment (1.0x)", "2. Sustainable House (1.20x)",
                "3. Green Resort (1.35x)", "4. Eco-Glamping (1.50x)"};
        JComboBox<String> typeDropdown = new JComboBox<>(types);
        gbc.gridx = 1; form.add(typeDropdown, gbc);

        // Create Button
        JButton createBtn = new JButton("Create Property");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        form.add(createBtn, gbc);

        // Back Button
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int type = typeDropdown.getSelectedIndex() + 1;

                if (name.isEmpty() || price < 100.0) {
                    JOptionPane.showMessageDialog(this, "Invalid name or price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (manager.addProperty(name, price, type)) {
                    JOptionPane.showMessageDialog(this, "Property '" + name + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText(""); // Clear field on success
                    priceField.setText("1500.00");
                    cardLayout.show(mainPanel, "MAIN_MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "Property name must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Base price must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPropertyManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Property Selection List
        List<Property> properties = manager.listProperties();
        String[] propertyNames = properties.stream().map(Property::getPropertyName).toArray(String[]::new);

        if (properties.isEmpty()) {
            panel.add(new JLabel("No properties available. Create one first.", SwingConstants.CENTER), BorderLayout.CENTER);
            JButton backBtn = new JButton("Back to Main Menu");
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
            panel.add(backBtn, BorderLayout.SOUTH);
            return panel;
        }

        JComboBox<String> propertySelector = new JComboBox<>(propertyNames);
        propertySelector.setBorder(BorderFactory.createTitledBorder("Select Property to Manage/View"));
        panel.add(propertySelector, BorderLayout.NORTH);

        // 2. Details and Calendar Panel (CardLayout for different views)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Property Details & Calendar"));
        panel.add(detailsPanel, BorderLayout.CENTER);

        // 3. Management Actions Panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        JButton renameBtn = new JButton("Rename");
        JButton priceBtn = new JButton("Update Base Price");
        JButton bookBtn = new JButton("Simulate Booking");
        JButton removeBtn = new JButton("Remove Property");

        actionsPanel.add(renameBtn);
        actionsPanel.add(priceBtn);
        actionsPanel.add(bookBtn);
        actionsPanel.add(removeBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actionsPanel, BorderLayout.CENTER);
        bottomPanel.add(backBtn, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- CALENDAR LOGIC (MCO2 Core Feature) ---

        // Initial load of the selected property's calendar
        Property selectedProperty = properties.get(propertySelector.getSelectedIndex());
        detailsPanel.add(createCalendarPanel(selectedProperty), BorderLayout.CENTER);

        // Listener to switch the displayed calendar
        propertySelector.addActionListener(e -> {
            Property p = properties.get(propertySelector.getSelectedIndex());
            detailsPanel.removeAll();
            detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
            detailsPanel.revalidate();
            detailsPanel.repaint();
        });

        // --- ACTION LISTENERS (Mapping MCO1/MCO2 features to GUI) ---

        renameBtn.addActionListener(createRenameListener(propertySelector, properties));
        priceBtn.addActionListener(createUpdatePriceListener(propertySelector, properties, detailsPanel));
        bookBtn.addActionListener(createBookingListener(propertySelector, properties, detailsPanel));
        removeBtn.addActionListener(createRemovePropertyListener(propertySelector, properties));

        return panel;
    }

    // Creates the JPanel containing the MCO2 color-coded calendar and the modifier update form.
    private JPanel createCalendarPanel(Property property) {
        JPanel calendarPanel = new JPanel(new BorderLayout(10, 10));

        // Property Info
        JPanel infoPanel = new JPanel(new GridLayout(4, 2));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Property Info"));
        infoPanel.add(new JLabel("Type:"));
        infoPanel.add(new JLabel(property.getPropertyType()));
        infoPanel.add(new JLabel("Base Price:"));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getBasePrice())));
        infoPanel.add(new JLabel("Total Earnings:"));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getTotalEarnings())));
        infoPanel.add(new JLabel("Available Days:"));
        infoPanel.add(new JLabel(String.valueOf(property.countAvailableDates())));

        calendarPanel.add(infoPanel, BorderLayout.NORTH);

        // 30-Day Calendar Table
        String[] columnNames = new String[7];
        for (int i = 0; i < 7; i++) { columnNames[i] = "Day " + (i + 1); }

        DefaultTableModel model = new DefaultTableModel(columnNames, 5) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable calendarTable = new JTable(model);
        calendarTable.setRowHeight(40);

        // Populate and Color-Code the Table (MCO2 requirement)
        for (int day = 1; day <= 30; day++) {
            int row = (day - 1) / 7;
            int col = (day - 1) % 7;

            if (row < 5) {
                DateSlot slot = property.getDates().get(day - 1);
                double finalRate = property.calculateFinalDailyRate(day);
                String display = String.format("<html><b>Day %d</b><br>Rate: %s</html>", day, PRICE_FORMAT.format(finalRate));
                model.setValueAt(display, row, col);
            }
        }

        // Custom Renderer for Color-Coding and Booked Status
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int day = (row * 7) + column + 1;
                if (day > 30) {
                    c.setBackground(UIManager.getColor("Panel.background"));
                    return c;
                }

                DateSlot slot = property.getDates().get(day - 1);
                double modifier = slot.getEnvImpactModifier();

                if (slot.isBooked()) {
                    c.setBackground(Color.LIGHT_GRAY);
                } else if (modifier >= 0.80 && modifier <= 0.89) { // Green: Reduced Impact (80-89%)
                    c.setBackground(new Color(144, 238, 144)); // Light Green
                } else if (modifier == 1.0) { // White: Standard Effects (100%)
                    c.setBackground(Color.WHITE);
                } else if (modifier >= 1.01 && modifier <= 1.20) { // Yellow: Increased Impact (101-120%)
                    c.setBackground(new Color(255, 255, 102)); // Light Yellow
                } else {
                    c.setBackground(Color.WHITE); // Default/fallback
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(calendarTable);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);

        // Modifier Management Panel (MCO2 New Feature)
        JPanel modifierPanel = createModifierPanel(property, calendarPanel);
        calendarPanel.add(modifierPanel, BorderLayout.SOUTH);

        return calendarPanel;
    }

    // Creates the panel for updating the environmental impact modifier
    private JPanel createModifierPanel(Property property, JPanel parentPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Update Environmental Impact Modifier"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Day Range Fields
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Start Day (1-30):"), gbc);
        JTextField startDayField = new JTextField(5);
        gbc.gridx = 1; panel.add(startDayField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("End Day (1-30):"), gbc);
        JTextField endDayField = new JTextField(5);
        gbc.gridx = 3; panel.add(endDayField, gbc);

        // Modifier Field
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("New Modifier (0.80 - 1.20):"), gbc);
        JTextField modifierField = new JTextField(MODIFIER_FORMAT.format(1.0), 5);
        gbc.gridx = 1; panel.add(modifierField, gbc);

        // Update Button
        JButton updateBtn = new JButton("Update Modifier");
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(updateBtn, gbc);

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        legendPanel.add(new JLabel("<html><b>Legend:</b></html>"));
        legendPanel.add(new JLabel("Green (80-89% ↓)", new ColorIcon(new Color(144, 238, 144)), SwingConstants.LEFT));
        legendPanel.add(new JLabel("White (100% →)", new ColorIcon(Color.WHITE), SwingConstants.LEFT));
        legendPanel.add(new JLabel("Yellow (101-120% ↑)", new ColorIcon(new Color(255, 255, 102)), SwingConstants.LEFT));
        legendPanel.add(new JLabel("Gray (Booked)", new ColorIcon(Color.LIGHT_GRAY), SwingConstants.LEFT));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panel.add(legendPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int start = Integer.parseInt(startDayField.getText().trim());
                int end = Integer.parseInt(endDayField.getText().trim());
                double modifier = Double.parseDouble(modifierField.getText().trim());

                if (start < 1 || end > 30 || start > end || modifier < 0.8 || modifier > 1.2) {
                    throw new IllegalArgumentException("Invalid day range or modifier value.");
                }

                for (int d = start; d <= end; d++) {
                    property.getDates().get(d - 1).setEnvImpactModifier(modifier);
                }

                JOptionPane.showMessageDialog(this, String.format("Modifier updated for Days %d-%d to %s.", start, end, MODIFIER_FORMAT.format(modifier)), "Success", JOptionPane.INFORMATION_MESSAGE);

                // Re-render the calendar panel to show changes
                Container container = parentPanel.getParent();
                container.removeAll();
                container.add(createCalendarPanel(property), BorderLayout.CENTER);
                container.revalidate();
                container.repaint();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Simple custom icon to show the legend color boxes
    private static class ColorIcon implements Icon {
        private final Color color;
        private static final int SIZE = 12;

        public ColorIcon(Color color) { this.color = color; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, SIZE - 1, SIZE - 1);
        }
        @Override
        public int getIconWidth() { return SIZE; }
        @Override
        public int getIconHeight() { return SIZE; }
    }

    // --- HELPER LISTENERS FOR MANAGEMENT ACTIONS ---

    private ActionListener createRenameListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String newName = JOptionPane.showInputDialog(this, "Enter new name for " + p.getPropertyName() + ":");
            if (newName != null && !newName.trim().isEmpty()) {
                if (manager.renameProperty(p.getPropertyName(), newName.trim())) {
                    JOptionPane.showMessageDialog(this, "Property renamed to " + newName.trim(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Refresh the whole panel to update the dropdown
                    cardLayout.show(mainPanel, "MAIN_MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "Name already exists or is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createUpdatePriceListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String priceInput = JOptionPane.showInputDialog(this, "Enter new base price for " + p.getPropertyName() + ":");
            if (priceInput != null) {
                try {
                    double newPrice = Double.parseDouble(priceInput.trim());
                    if (p.updateBasePrice(newPrice)) {
                        JOptionPane.showMessageDialog(this, "Base price updated to " + PRICE_FORMAT.format(newPrice), "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Re-render the calendar to show the change immediately
                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot update price: active reservations or price too low (must be >= 100).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createBookingListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            JPanel bookingPanel = new JPanel(new GridLayout(4, 2));
            JTextField guestField = new JTextField(10);
            JTextField inField = new JTextField(5);
            JTextField outField = new JTextField(5);

            bookingPanel.add(new JLabel("Guest Name:"));
            bookingPanel.add(guestField);
            bookingPanel.add(new JLabel("Check-in Day (1-30):"));
            bookingPanel.add(inField);
            bookingPanel.add(new JLabel("Check-out Day (1-30):"));
            bookingPanel.add(outField);

            int result = JOptionPane.showConfirmDialog(this, bookingPanel,
                    "Simulate Booking for " + p.getPropertyName(), JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String guest = guestField.getText().trim();
                    int checkIn = Integer.parseInt(inField.getText().trim());
                    int checkOut = Integer.parseInt(outField.getText().trim());

                    if (p.addReservation(guest, checkIn, checkOut)) {
                        JOptionPane.showMessageDialog(this, "Booking confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Re-render the calendar to show the booking status change
                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "Booking failed: invalid days or date conflict.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Days must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createRemovePropertyListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove " + p.getPropertyName() + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.removeProperty(p.getPropertyName())) {
                    JOptionPane.showMessageDialog(this, "Property removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Refresh the entire management panel
                    mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
                    cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot remove: Active reservations exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
}