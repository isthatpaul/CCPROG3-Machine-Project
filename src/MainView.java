import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

/**
 * The MainView class provides the main JFrame and functionality for the
 * Green Property Exchange System using Java Swing, featuring an NDS aesthetic.
 */
public class MainView extends JFrame {

    private final PropertyManager manager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("PHP #,##0.00");
    private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#0.00");

    // --- NDS Aesthetic Configuration ---
    // NDS screens often use low-res, chunky fonts. Using bold and larger size mimics this.
    private static final Font NDS_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font NDS_TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);

    // NDS-style color palette
    private static final Color NDS_BLUE_DARK = new Color(50, 100, 150);
    private static final Color NDS_BLUE_LIGHT = new Color(150, 200, 255);
    private static final Color NDS_BG = new Color(240, 240, 240);

    // Calendar Colors
    private static final Color NDS_GREEN_LOW = new Color(50, 200, 50);   // Reduced Impact (80-89%)
    private static final Color NDS_YELLOW_HIGH = new Color(255, 200, 0); // Increased Impact (101-120%)
    private static final Color NDS_RED_BOOKED = new Color(200, 50, 50);  // Booked
    private static final Color NDS_WHITE_STD = Color.WHITE;               // Standard (100%)

    // Custom Border for that chunky UI element look
    private static final Border BLOCK_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NDS_BLUE_DARK, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Constructor sets up the main frame and all views
    public MainView() {
        super("Green Property Exchange (NDS Edition)");
        this.manager = new PropertyManager();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650); // Slightly larger screen for the game aesthetic
        setLocationRelativeTo(null);

        // --- Apply NDS Theme Properties ---
        UIManager.put("Panel.background", NDS_BG);
        UIManager.put("Button.font", NDS_FONT);
        UIManager.put("Label.font", NDS_FONT);
        UIManager.put("ComboBox.font", NDS_FONT);
        UIManager.put("TextField.font", NDS_FONT);
        UIManager.put("TextArea.font", NDS_FONT);
        UIManager.put("TitledBorder.font", NDS_FONT);

        UIManager.put("Button.background", NDS_BLUE_LIGHT);
        UIManager.put("Button.foreground", NDS_BLUE_DARK);
        UIManager.put("Button.border", BorderFactory.createLineBorder(NDS_BLUE_DARK, 2));

        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("Label.foreground", NDS_BLUE_DARK);
        UIManager.put("TitledBorder.titleColor", NDS_BLUE_DARK);

        SwingUtilities.updateComponentTreeUI(this);

        // Initialize and add all the primary screens (JPanels)
        mainPanel.add(createMainMenuPanel(), "MAIN_MENU");

        // Use a wrapper panel to mimic the two-screen layout for management
        mainPanel.add(createDualScreenWrapper("Manage Property", createPropertyManagementPanel()), "MANAGE_PROPERTY");
        mainPanel.add(createDualScreenWrapper("New Listing", createPropertyCreationPanel()), "CREATE_PROPERTY");

        // Add sample data for testing MCO2 features quickly
        manager.addProperty("Eco-Apt 101", 1000.0, 1);
        manager.addProperty("Sustainable Home", 1000.0, 2);
        manager.bookProperty("Eco-Apt 101", "Test Guest", 5, 8);

        add(mainPanel);
    }

    // NDS Wrapper: Creates a top and bottom panel to mimic the dual-screen aesthetic
    private JPanel createDualScreenWrapper(String title, JPanel contentPanel) {
        JPanel wrapper = new JPanel(new GridLayout(2, 1, 10, 10));
        wrapper.setBackground(NDS_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Screen (Info/Title)
        JPanel topScreen = new JPanel(new BorderLayout());
        topScreen.setBorder(BLOCK_BORDER);
        topScreen.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("GREEN PROPERTY EXCHANGE | " + title, SwingConstants.CENTER);
        titleLabel.setFont(NDS_FONT.deriveFont(Font.PLAIN, 16f));
        titleLabel.setForeground(NDS_BLUE_DARK);
        topScreen.add(titleLabel, BorderLayout.CENTER);

        // Bottom Screen (Interactive Content)
        contentPanel.setBorder(BLOCK_BORDER);
        contentPanel.setBackground(Color.WHITE);

        wrapper.add(topScreen);
        wrapper.add(contentPanel);
        return wrapper;
    }

    // --- GUI PANEL FACTORY METHODS ---

    private JPanel createMainMenuPanel() {
        // Standard panel for the main menu, still following NDS style
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BLOCK_BORDER);
        panel.setBackground(NDS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);

        JLabel titleLabel = new JLabel("GREEN PROPERTY EXCHANGE", SwingConstants.CENTER);
        titleLabel.setFont(NDS_TITLE_FONT);
        titleLabel.setForeground(NDS_BLUE_DARK);
        panel.add(titleLabel, gbc);

        panel.add(new JLabel("MCO2", SwingConstants.CENTER) {{
            setFont(NDS_FONT.deriveFont(Font.ITALIC));
            setForeground(NDS_BLUE_DARK);
        }}, gbc);
        panel.add(Box.createVerticalStrut(20), gbc);

        // Menu Buttons
        JButton manageBtn = new JButton("START: MANAGE PROPERTIES");
        manageBtn.addActionListener(e -> {
            mainPanel.add(createDualScreenWrapper("Manage Property", createPropertyManagementPanel()), "MANAGE_PROPERTY_REFRESH");
            cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
        });
        panel.add(manageBtn, gbc);

        JButton createBtn = new JButton("NEW: CREATE LISTING");
        createBtn.addActionListener(e -> cardLayout.show(mainPanel, "CREATE_PROPERTY"));
        panel.add(createBtn, gbc);

        JButton exitBtn = new JButton("EXIT PROGRAM");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn, gbc);

        return panel;
    }

    private JPanel createPropertyCreationPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Property Name Field
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("LISTING NAME:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; form.add(nameField, gbc);

        // Base Price Field
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("BASE PRICE (PHP >= 100):"), gbc);
        JTextField priceField = new JTextField("1500.00", 20);
        gbc.gridx = 1; form.add(priceField, gbc);

        // Property Type Selection (MCO2 Requirement)
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("PROPERTY TYPE:"), gbc);
        String[] types = {"1. Eco-Apartment (1.0x)", "2. Sustainable House (1.20x)",
                "3. Green Resort (1.35x)", "4. Eco-Glamping (1.50x)"};
        JComboBox<String> typeDropdown = new JComboBox<>(types);
        typeDropdown.setBackground(NDS_BLUE_LIGHT);
        typeDropdown.setForeground(NDS_BLUE_DARK);
        gbc.gridx = 1; form.add(typeDropdown, gbc);

        // Create Button
        JButton createBtn = new JButton("CREATE LISTING");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        form.add(createBtn, gbc);

        // Back Button
        JButton backBtn = new JButton("BACK");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.add(createBtn);
        buttonWrapper.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonWrapper, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPropertyManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // 1. Property Selection List
        List<Property> properties = manager.listProperties();

        if (properties.isEmpty()) {
            panel.add(new JLabel("NO LISTINGS FOUND.", SwingConstants.CENTER) {{ setForeground(NDS_RED_BOOKED); }}, BorderLayout.CENTER);
            JButton backBtn = new JButton("MAIN MENU");
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
            panel.add(backBtn, BorderLayout.SOUTH);
            return panel;
        }

        String[] propertyNames = properties.stream().map(Property::getPropertyName).toArray(String[]::new);
        JComboBox<String> propertySelector = new JComboBox<>(propertyNames);
        propertySelector.setBorder(BorderFactory.createTitledBorder("SELECT LISTING:"));
        propertySelector.setBackground(NDS_BLUE_LIGHT);
        propertySelector.setForeground(NDS_BLUE_DARK);
        panel.add(propertySelector, BorderLayout.NORTH);

        // 2. Details and Calendar Panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        panel.add(detailsPanel, BorderLayout.CENTER);

        // 3. Management Actions Panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        actionsPanel.setBackground(Color.WHITE);

        JButton renameBtn = new JButton("RENAME");
        JButton priceBtn = new JButton("PRICE UPDATE");
        JButton bookBtn = new JButton("NEW BOOKING");
        JButton removeBtn = new JButton("REMOVE");

        actionsPanel.add(renameBtn);
        actionsPanel.add(priceBtn);
        actionsPanel.add(bookBtn);
        actionsPanel.add(removeBtn);

        JButton backBtn = new JButton("BACK");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(Color.WHITE);
        bottomWrapper.add(actionsPanel, BorderLayout.CENTER);
        bottomWrapper.add(backBtn, BorderLayout.SOUTH);

        panel.add(bottomWrapper, BorderLayout.SOUTH);

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

        // --- ACTION LISTENERS ---
        renameBtn.addActionListener(createRenameListener(propertySelector, properties));
        priceBtn.addActionListener(createUpdatePriceListener(propertySelector, properties, detailsPanel));
        bookBtn.addActionListener(createBookingListener(propertySelector, properties, detailsPanel));
        removeBtn.addActionListener(createRemovePropertyListener(propertySelector, properties));

        return panel;
    }

    // Creates the NDS-style color-coded calendar panel
    private JPanel createCalendarPanel(Property property) {
        JPanel calendarPanel = new JPanel(new BorderLayout(10, 10));
        calendarPanel.setBackground(Color.WHITE);

        // Property Info (Top section of the management screen)
        JPanel infoPanel = new JPanel(new GridLayout(4, 2));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("LISTING INFO"));
        infoPanel.add(new JLabel("TYPE:"));
        infoPanel.add(new JLabel(property.getPropertyType()));
        infoPanel.add(new JLabel("BASE RATE:"));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getBasePrice())));
        infoPanel.add(new JLabel("TOTAL INCOME:"));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getTotalEarnings())));
        infoPanel.add(new JLabel("AVAILABLE SLOTS:"));
        infoPanel.add(new JLabel(String.valueOf(property.countAvailableDates())));

        calendarPanel.add(infoPanel, BorderLayout.NORTH);

        // 30-Day Calendar Table
        String[] columnNames = new String[7];
        for (int i = 0; i < 7; i++) { columnNames[i] = "DAY " + (i + 1); }

        DefaultTableModel model = new DefaultTableModel(columnNames, 5) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable calendarTable = new JTable(model);
        calendarTable.setRowHeight(40);
        calendarTable.setFont(NDS_FONT.deriveFont(Font.PLAIN, 12f));
        calendarTable.getTableHeader().setFont(NDS_FONT);
        calendarTable.setGridColor(NDS_BLUE_DARK); // Chunky blue lines
        calendarTable.setShowGrid(true);
        calendarTable.setIntercellSpacing(new Dimension(1, 1));

        // Populate the Table
        for (int day = 1; day <= 30; day++) {
            int row = (day - 1) / 7;
            int col = (day - 1) % 7;

            if (row < 5) {
                double finalRate = property.calculateFinalDailyRate(day);
                // HTML for multiline, low-res look
                String display = String.format("<html><b>D%d</b><br>%s</html>", day, PRICE_FORMAT.format(finalRate));
                model.setValueAt(display, row, col);
            }
        }

        // Custom Renderer for Color-Coding (NDS-style)
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int day = (row * 7) + column + 1;
                c.setFont(NDS_FONT.deriveFont(Font.PLAIN, 12f));
                c.setForeground(Color.BLACK);

                if (day > 30) {
                    c.setBackground(NDS_BG);
                    return c;
                }

                DateSlot slot = property.getDates().get(day - 1);
                double modifier = slot.getEnvImpactModifier();

                if (slot.isBooked()) {
                    c.setBackground(NDS_RED_BOOKED); // Bright Red for booked
                    c.setForeground(Color.WHITE);
                } else if (modifier >= 0.80 && modifier <= 0.89) {
                    c.setBackground(NDS_GREEN_LOW); // Bright Green
                } else if (modifier == 1.0) {
                    c.setBackground(NDS_WHITE_STD); // Pure White
                } else if (modifier >= 1.01 && modifier <= 1.20) {
                    c.setBackground(NDS_YELLOW_HIGH); // Bright Yellow/Gold
                } else {
                    c.setBackground(NDS_WHITE_STD);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(calendarTable);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);

        // Modifier Management Panel and Legend
        JPanel modifierPanel = createModifierPanel(property, calendarPanel);
        calendarPanel.add(modifierPanel, BorderLayout.SOUTH);

        return calendarPanel;
    }

    // Creates the panel for updating the environmental impact modifier
    private JPanel createModifierPanel(Property property, JPanel parentPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("IMPACT MODIFIER MENU"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Day Range Fields
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("DAY START (1-30):"), gbc);
        JTextField startDayField = new JTextField(5);
        gbc.gridx = 1; panel.add(startDayField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("DAY END (1-30):"), gbc);
        JTextField endDayField = new JTextField(5);
        gbc.gridx = 3; panel.add(endDayField, gbc);

        // Modifier Field
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("NEW MODIFIER (0.80 - 1.20):"), gbc);
        JTextField modifierField = new JTextField(MODIFIER_FORMAT.format(1.0), 5);
        gbc.gridx = 1; panel.add(modifierField, gbc);

        // Update Button
        JButton updateBtn = new JButton("APPLY CHANGES");
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(updateBtn, gbc);

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(new JLabel("STATUS LEGEND:") {{ setForeground(NDS_BLUE_DARK); }});
        legendPanel.add(new JLabel("REDUCED", new ColorIcon(NDS_GREEN_LOW), SwingConstants.LEFT));
        legendPanel.add(new JLabel("STANDARD", new ColorIcon(NDS_WHITE_STD), SwingConstants.LEFT));
        legendPanel.add(new JLabel("INCREASED", new ColorIcon(NDS_YELLOW_HIGH), SwingConstants.LEFT));
        legendPanel.add(new JLabel("BOOKED", new ColorIcon(NDS_RED_BOOKED), SwingConstants.LEFT));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panel.add(legendPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int start = Integer.parseInt(startDayField.getText().trim());
                int end = Integer.parseInt(endDayField.getText().trim());
                double modifier = Double.parseDouble(modifierField.getText().trim());

                if (start < 1 || end > 30 || start > end || modifier < 0.8 || modifier > 1.2) {
                    throw new IllegalArgumentException("Invalid day range or modifier value (0.80 to 1.20).");
                }

                for (int d = start; d <= end; d++) {
                    property.getDates().get(d - 1).setEnvImpactModifier(modifier);
                }

                JOptionPane.showMessageDialog(this, String.format("MODIFIER APPLIED! DAYS %d-%d UPDATED.", start, end), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                // Re-render the calendar panel to show changes
                Container container = parentPanel.getParent();
                container.removeAll();
                container.add(createCalendarPanel(property), BorderLayout.CENTER);
                container.revalidate();
                container.repaint();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage(), "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Custom Icon for Legend (Blocky NDS style)
    private static class ColorIcon implements Icon {
        private final Color color;
        private static final int SIZE = 14;

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

    // --- HELPER LISTENERS (Logic remains the same) ---

    private ActionListener createRenameListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String newName = JOptionPane.showInputDialog(this, "ENTER NEW LISTING NAME:");
            if (newName != null && !newName.trim().isEmpty()) {
                if (manager.renameProperty(p.getPropertyName(), newName.trim())) {
                    JOptionPane.showMessageDialog(this, "LISTING RENAMED.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "MAIN_MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "NAME ALREADY EXISTS OR IS INVALID.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createUpdatePriceListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String priceInput = JOptionPane.showInputDialog(this, "ENTER NEW BASE RATE (PHP >= 100):");
            if (priceInput != null) {
                try {
                    double newPrice = Double.parseDouble(priceInput.trim());
                    if (p.updateBasePrice(newPrice)) {
                        JOptionPane.showMessageDialog(this, "BASE RATE UPDATED!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "CANNOT UPDATE: ACTIVE BOOKINGS OR RATE TOO LOW.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "INVALID RATE FORMAT.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createBookingListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            JPanel bookingPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField guestField = new JTextField(10);
            JTextField inField = new JTextField(5);
            JTextField outField = new JTextField(5);

            bookingPanel.add(new JLabel("GUEST NAME:"));
            bookingPanel.add(guestField);
            bookingPanel.add(new JLabel("CHECK-IN DAY (1-30):"));
            bookingPanel.add(inField);
            bookingPanel.add(new JLabel("CHECK-OUT DAY (1-30):"));
            bookingPanel.add(outField);

            int result = JOptionPane.showConfirmDialog(this, bookingPanel,
                    "NEW BOOKING: " + p.getPropertyName(), JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String guest = guestField.getText().trim();
                    int checkIn = Integer.parseInt(inField.getText().trim());
                    int checkOut = Integer.parseInt(outField.getText().trim());

                    if (p.addReservation(guest, checkIn, checkOut)) {
                        double total = p.getReservations().get(p.getReservations().size() - 1).getTotalPrice();
                        JOptionPane.showMessageDialog(this, "BOOKING COMPLETE! TOTAL: " + PRICE_FORMAT.format(total), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "BOOKING FAILED: INVALID RANGE OR CONFLICT.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "DAYS MUST BE VALID NUMBERS.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createRemovePropertyListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "REMOVE " + p.getPropertyName() + "?", "CONFIRM REMOVAL", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.removeProperty(p.getPropertyName())) {
                    JOptionPane.showMessageDialog(this, "LISTING REMOVED.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    mainPanel.add(createDualScreenWrapper("Manage Property", createPropertyManagementPanel()), "MANAGE_PROPERTY_REFRESH");
                    cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
                } else {
                    JOptionPane.showMessageDialog(this, "CANNOT REMOVE: ACTIVE BOOKINGS EXIST.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
}