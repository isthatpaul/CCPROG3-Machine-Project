import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Green Property Exchange System (MCO2)
 * Retro pixel-art themed main view using Java Swing.
 */
public class MainView extends JFrame {

    private final PropertyManager manager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("PHP #,##0.00");
    private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#0.00");
    private final Font retroFont;

    public MainView() {
        super("Green Property Exchange System (MCO2)");
        this.manager = new PropertyManager();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.retroFont = loadRetroFont(24f);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        mainPanel.add(createMainMenuPanel(), "MAIN_MENU");
        mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY");
        mainPanel.add(createPropertyCreationPanel(), "CREATE_PROPERTY");

        addSampleData();
        add(mainPanel);
    }

    private Font loadRetroFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/PressStart2P-Regular.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            System.err.println("Retro font not loaded: " + e.getMessage());
            return new Font("Monospaced", Font.BOLD, (int) size);
        }
    }

    private void addSampleData() {
        manager.addProperty("Eco-Apt 101", 1000.0, 1);
        manager.addProperty("Sustainable Home", 1000.0, 2);
        manager.bookProperty("Eco-Apt 101", "Test Guest", 5, 8);
    }

    // ======== BACKGROUND PANEL =========
    private static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String resourcePath) {
            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL == null) {
                System.err.println("Image not found: " + resourcePath);
                backgroundImage = null;
            } else {
                backgroundImage = new ImageIcon(imgURL).getImage();
            }
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // ======== OUTLINED LABEL CREATOR =========
    private JLabel createOutlinedLabel(String text, float size, Color textColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(retroFont.deriveFont(size));

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();

                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 2;

                // Draw black border outline
                g2.setColor(Color.BLACK);
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2.drawString(text, x + dx, y + dy);
                        }
                    }
                }

                g2.setColor(textColor);
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        label.setPreferredSize(new Dimension(800, (int) (size * 3.5)));
        label.setOpaque(false);
        return label;
    }

    private JButton createStyledButton(String text, float fontSize) {

        final String displayText = text;    // We store the real text here
        final int[] textOffset = {0};       // Used for scrolling

        JButton button = new JButton("") {  // Set button text to empty so Swing doesn't draw it
            @Override
            protected void paintComponent(Graphics g) {

                // Draw button background & border normally
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(displayText);
                int btnWidth = getWidth();
                int y = (getHeight() + fm.getAscent()) / 2 - 3;

                // If text fits → center it normally (no scroll)
                if (textWidth <= btnWidth - 14) {
                    int x = (btnWidth - textWidth) / 2;
                    g2.drawString(displayText, x, y);
                }
                // Otherwise → scroll horizontally
                else {
                    g2.drawString(displayText, textOffset[0], y);
                    if (textOffset[0] < -textWidth) {
                        textOffset[0] = btnWidth;
                    }
                }

                g2.dispose();
            }
        };

        // Pixel-DS inspired minimal UI style
        button.setFont(loadRetroFont(fontSize));
        button.setPreferredSize(new Dimension(260, 38));
        button.setBackground(new Color(235, 235, 235));
        button.setForeground(new Color(45, 45, 45));
        button.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 2));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Smooth sideways scrolling animation timer
        javax.swing.Timer scrollTimer = new javax.swing.Timer(45, e -> {
            textOffset[0] -= 2;
            button.repaint();
        });

        // Hover actions
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                scrollTimer.start();
                button.setBackground(new Color(248, 248, 248));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                scrollTimer.stop();
                textOffset[0] = 0;
                button.setBackground(new Color(235, 235, 235));
                button.repaint();
            }
        });

        return button;
    }


    private JPanel createMainMenuPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/main_menu_bg.png");
        panel.setLayout(null);

        int panelWidth = 1024;
        int panelHeight = 768;

        // Slight horizontal offset to center relative to background art
        int xOffset = -25;

        // === TITLE LINE 1 ===
        JLabel titleLine1 = createOutlinedLabel("GREEN PROPERTY", 38f, new Color(185, 255, 140));
        titleLine1.setHorizontalAlignment(SwingConstants.CENTER);
        titleLine1.setBounds((panelWidth - 800) / 2 + xOffset, 135, 800, 60);
        panel.add(titleLine1);

        // === TITLE LINE 2 ===
        JLabel titleLine2 = createOutlinedLabel("EXCHANGE", 38f, new Color(185, 255, 140));
        titleLine2.setHorizontalAlignment(SwingConstants.CENTER);
        titleLine2.setBounds((panelWidth - 800) / 2 + xOffset, 195, 800, 60);
        panel.add(titleLine2);

        // === SUBTITLE ===
        JLabel subtitle = createOutlinedLabel("MCO2", 22f, new Color(210, 230, 190));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setBounds((panelWidth - 200) / 2 + xOffset, 260, 200, 40);
        panel.add(subtitle);

        // === BUTTONS ===
        int buttonWidth = 260;
        int buttonHeight = 38;

        int gap = 25;

        // Move upward to sit in the green box area
        int startY = 300; // moved up ~50px

        JButton btn1 = createStyledButton("MANAGE & VIEW PROPERTIES", 14f);
        btn1.setBounds((panelWidth - buttonWidth) / 2 + xOffset, startY, buttonWidth, buttonHeight);
        btn1.addActionListener(e -> {
            mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
            cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
        });

        JButton btn2 = createStyledButton("CREATE NEW PROPERTY", 14f);
        btn2.setBounds((panelWidth - buttonWidth) / 2 + xOffset, startY + buttonHeight + gap, buttonWidth, buttonHeight);
        btn2.addActionListener(e -> cardLayout.show(mainPanel, "CREATE_PROPERTY"));

        JButton btn3 = createStyledButton("EXIT", 14f);
        btn3.setBounds((panelWidth - buttonWidth) / 2 + xOffset, startY + 2 * (buttonHeight + gap), buttonWidth, buttonHeight);
        btn3.addActionListener(e -> System.exit(0));

        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);

        return panel;
    }

    // PROPERTY MANAGEMENT - uses main_menu_bg.png for consistency, with tint
    private JPanel createPropertyManagementPanel() {
        JPanel panel = new BackgroundPanel("/resources/main_menu_bg.png");
        panel.setLayout(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        List<Property> properties = manager.listProperties();
        String[] propertyNames = properties.stream().map(Property::getPropertyName).toArray(String[]::new);

        if (properties.isEmpty()) {
            panel.add(new JLabel("NO PROPERTIES AVAILABLE. CREATE ONE FIRST.", SwingConstants.CENTER), BorderLayout.CENTER);
            JButton backBtn = createStyledButton("BACK TO MAIN MENU", 14f);
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
            panel.add(backBtn, BorderLayout.SOUTH);
            return panel;
        }

        JComboBox<String> propertySelector = new JComboBox<>(propertyNames);
        propertySelector.setFont(retroFont.deriveFont(18f));
        propertySelector.setBorder(BorderFactory.createTitledBorder("SELECT PROPERTY"));
        propertySelector.setBackground(new Color(231, 245, 197)); // pixel bg green
        propertySelector.setForeground(new Color(30, 63, 41));
        panel.add(propertySelector, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(228, 240, 211, 222)); // translucent pale green pixel
        detailsPanel.setBorder(BorderFactory.createTitledBorder("PROPERTY DETAILS & CALENDAR"));
        panel.add(detailsPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 8, 8));
        actionsPanel.setBackground(new Color(194,255,97,192));

        JButton backBtn = createStyledButton("BACK TO MAIN MENU", 14f);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        JButton renameBtn = createStyledButton("RENAME", 14f);
        JButton priceBtn = createStyledButton("UPDATE BASE PRICE", 14f);
        JButton bookBtn = createStyledButton("SIMULATE BOOKING", 14f);
        JButton removeBtn = createStyledButton("REMOVE PROPERTY", 14f);

        actionsPanel.add(renameBtn);
        actionsPanel.add(priceBtn);
        actionsPanel.add(bookBtn);
        actionsPanel.add(removeBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(173,193,176,222));
        bottomPanel.add(actionsPanel, BorderLayout.CENTER);
        bottomPanel.add(backBtn, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        Property selectedProperty = properties.get(propertySelector.getSelectedIndex());
        detailsPanel.add(createCalendarPanel(selectedProperty), BorderLayout.CENTER);

        propertySelector.addActionListener(e -> {
            Property p = properties.get(propertySelector.getSelectedIndex());
            detailsPanel.removeAll();
            detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
            detailsPanel.revalidate();
            detailsPanel.repaint();
        });

        renameBtn.addActionListener(createRenameListener(propertySelector, properties));
        priceBtn.addActionListener(createUpdatePriceListener(propertySelector, properties, detailsPanel));
        bookBtn.addActionListener(createBookingListener(propertySelector, properties, detailsPanel));
        removeBtn.addActionListener(createRemovePropertyListener(propertySelector, properties));

        return panel;
    }

    private ActionListener createRemovePropertyListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "ARE YOU SURE YOU WANT TO REMOVE " + p.getPropertyName() + "?", "CONFIRM REMOVAL", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.removeProperty(p.getPropertyName())) {
                    JOptionPane.showMessageDialog(this, "PROPERTY REMOVED!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
                    cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
                } else {
                    JOptionPane.showMessageDialog(this, "CANNOT REMOVE: ACTIVE RESERVATIONS EXIST.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createBookingListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            JPanel bookingPanel = new JPanel(new GridLayout(4, 2));
            bookingPanel.add(new JLabel("GUEST NAME:")).setFont(retroFont.deriveFont(16f));
            JTextField guestField = new JTextField(10);
            bookingPanel.add(guestField);
            bookingPanel.add(new JLabel("CHECK-IN DAY (1-30):")).setFont(retroFont.deriveFont(16f));
            JTextField inField = new JTextField(5);
            bookingPanel.add(inField);
            bookingPanel.add(new JLabel("CHECK-OUT DAY (1-30):")).setFont(retroFont.deriveFont(16f));
            JTextField outField = new JTextField(5);
            bookingPanel.add(outField);

            int result = JOptionPane.showConfirmDialog(this, bookingPanel,
                    "SIMULATE BOOKING FOR " + p.getPropertyName(), JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String guest = guestField.getText().trim();
                    int checkIn = Integer.parseInt(inField.getText().trim());
                    int checkOut = Integer.parseInt(outField.getText().trim());
                    if (p.addReservation(guest, checkIn, checkOut)) {
                        JOptionPane.showMessageDialog(this, "BOOKING CONFIRMED!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "BOOKING FAILED: INVALID DAYS OR DATE CONFLICT.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "DAYS MUST BE VALID NUMBERS.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createUpdatePriceListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String priceInput = JOptionPane.showInputDialog(this, "ENTER NEW BASE PRICE FOR " + p.getPropertyName() + ":");
            if (priceInput != null) {
                try {
                    double newPrice = Double.parseDouble(priceInput.trim());
                    if (p.updateBasePrice(newPrice)) {
                        JOptionPane.showMessageDialog(this, "BASE PRICE UPDATED TO " + PRICE_FORMAT.format(newPrice), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        detailsPanel.removeAll();
                        detailsPanel.add(createCalendarPanel(p), BorderLayout.CENTER);
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "CANNOT UPDATE PRICE: ACTIVE RESERVATIONS OR PRICE TOO LOW (>= PHP 100).", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "INVALID PRICE FORMAT.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener createRenameListener(JComboBox<String> selector, List<Property> properties) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            String newName = JOptionPane.showInputDialog(this, "ENTER NEW NAME FOR " + p.getPropertyName() + ":");
            if (newName != null && !newName.trim().isEmpty()) {
                if (manager.renameProperty(p.getPropertyName(), newName.trim())) {
                    JOptionPane.showMessageDialog(this, "PROPERTY RENAMED TO " + newName.trim(), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "MAIN_MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "NAME ALREADY EXISTS OR IS INVALID.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    // PROPERTY CREATION - also uses main_menu_bg.png for cohesive style
    private JPanel createPropertyCreationPanel() {
        JPanel panel = new BackgroundPanel("/resources/main_menu_bg.png");
        panel.setLayout(new BorderLayout(14, 14));

        JLabel title = new JLabel("CREATE PROPERTY LISTING", SwingConstants.CENTER);
        title.setFont(retroFont.deriveFont(28f));
        title.setForeground(new Color(250,180,60));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(255,255,255,220));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(81,107,75),2),
                "CREATE NEW PROPERTY", TitledBorder.LEFT, TitledBorder.TOP, retroFont.deriveFont(17f)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("PROPERTY NAME:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("BASE PRICE (PHP >= 100):"), gbc);
        JTextField priceField = new JTextField("1500.00", 20);
        gbc.gridx = 1; form.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("PROPERTY TYPE:"), gbc);
        String[] types = {"ECO-APARTMENT (1.0X)", "SUSTAINABLE HOUSE (1.20X)",
                "GREEN RESORT (1.35X)", "ECO-GLAMPING (1.50X)"};
        JComboBox<String> typeDropdown = new JComboBox<>(types);
        gbc.gridx = 1; form.add(typeDropdown, gbc);

        JButton createBtn = createStyledButton("CREATE PROPERTY", 14f);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(createBtn, gbc);

        JButton backBtn = createStyledButton("BACK TO MAIN MENU", 14f);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int type = typeDropdown.getSelectedIndex() + 1;

                if (name.isEmpty() || price < 100.0) {
                    JOptionPane.showMessageDialog(this, "INVALID NAME OR PRICE.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (manager.addProperty(name, price, type)) {
                    JOptionPane.showMessageDialog(this, "PROPERTY '" + name + "' CREATED!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText(""); // Clear field on success
                    priceField.setText("1500.00");
                    cardLayout.show(mainPanel, "MAIN_MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "PROPERTY NAME MUST BE UNIQUE.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "BASE PRICE MUST BE A NUMBER.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(255,255,255,220));
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // CALENDAR PANEL - uses main_menu_bg.png for cohesive game vibe
    private JPanel createCalendarPanel(Property property) {
        JPanel calendarPanel = new BackgroundPanel("/resources/main_menu_bg.png");
        calendarPanel.setLayout(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(4, 2));
        infoPanel.setBackground(new Color(240,255,239,222));
        infoPanel.setBorder(BorderFactory.createTitledBorder("PROPERTY INFO"));
        infoPanel.add(new JLabel("TYPE:")).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel(property.getPropertyType())).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel("BASE PRICE:")).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getBasePrice()))).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel("TOTAL EARNINGS:")).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel(PRICE_FORMAT.format(property.getTotalEarnings()))).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel("AVAILABLE DAYS:")).setFont(retroFont.deriveFont(16f));
        infoPanel.add(new JLabel(String.valueOf(property.countAvailableDates()))).setFont(retroFont.deriveFont(16f));
        calendarPanel.add(infoPanel, BorderLayout.NORTH);

        String[] columnNames = new String[7];
        for (int i = 0; i < 7; i++) columnNames[i] = "DAY " + (i + 1);

        DefaultTableModel model = new DefaultTableModel(columnNames, 5) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable calendarTable = new JTable(model);
        calendarTable.setFont(retroFont.deriveFont(14f));
        calendarTable.setRowHeight(40);
        calendarTable.getTableHeader().setFont(retroFont.deriveFont(13f));

        for (int day = 1; day <= 30; day++) {
            int row = (day - 1) / 7;
            int col = (day - 1) % 7;
            if (row < 5) {
                DateSlot slot = property.getDates().get(day - 1);
                double finalRate = property.calculateFinalDailyRate(day);
                String nameInfo = slot.isBooked()
                        ? "<html><b>DAY " + day + "</b><br><font color=gray><i>BOOKED</i></font></html>"
                        : "<html><b>DAY " + day + "</b><br>RATE: " + PRICE_FORMAT.format(finalRate) + "</html>";
                model.setValueAt(nameInfo, row, col);
            }
        }

        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(retroFont.deriveFont(13f));
                int day = (row * 7) + column + 1;
                if (day > 30) {
                    setBackground(new Color(36, 63, 41)); // match game bg
                    setText("");
                    return this;
                }
                DateSlot slot = property.getDates().get(day - 1);
                double mod = slot.getEnvImpactModifier();

                if (slot.isBooked()) {
                    setBackground(new Color(100, 85, 53)); // Brown, match vines
                } else if (mod >= 0.80 && mod <= 0.89) {
                    setBackground(new Color(194, 255, 97)); // Neon leaf
                } else if (mod == 1.0) {
                    setBackground(new Color(228, 240, 211, 222)); // Soft menu bg
                } else if (mod >= 1.01 && mod <= 1.20) {
                    setBackground(new Color(255, 240, 58)); // Arcade yellow
                } else {
                    setBackground(new Color(228, 240, 211, 222));
                }
                setForeground(new Color(36, 63, 41));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(calendarTable);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel modifierPanel = createModifierPanel(property, calendarPanel);
        calendarPanel.add(modifierPanel, BorderLayout.SOUTH);

        return calendarPanel;
    }

    private JPanel createModifierPanel(Property property, JPanel parentPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(228,240,211,210));
        panel.setBorder(BorderFactory.createTitledBorder("UPDATE ENVIRONMENTAL IMPACT MODIFIER"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("START DAY (1-30):"), gbc);
        JTextField startDayField = new JTextField(5);
        gbc.gridx = 1; panel.add(startDayField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("END DAY (1-30):"), gbc);
        JTextField endDayField = new JTextField(5);
        gbc.gridx = 3; panel.add(endDayField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("NEW MODIFIER (0.80 - 1.20):"), gbc);
        JTextField modifierField = new JTextField(MODIFIER_FORMAT.format(1.0), 5);
        gbc.gridx = 1; panel.add(modifierField, gbc);

        JButton updateBtn = createStyledButton("UPDATE MODIFIER", 14f);
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(updateBtn, gbc);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        legendPanel.setBackground(new Color(173,193,176,220));
        legendPanel.add(new JLabel("LEGEND: "));
        legendPanel.add(new JLabel("GREEN (80-89%)", new ColorIcon(new Color(194, 255, 97)), SwingConstants.LEFT));
        legendPanel.add(new JLabel("MENU BG (100%)", new ColorIcon(new Color(228,240,211,222)), SwingConstants.LEFT));
        legendPanel.add(new JLabel("YELLOW (101-120%)", new ColorIcon(new Color(255,240,58)), SwingConstants.LEFT));
        legendPanel.add(new JLabel("BROWN (BOOKED)", new ColorIcon(new Color(100, 85, 53)), SwingConstants.LEFT));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panel.add(legendPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int start = Integer.parseInt(startDayField.getText().trim());
                int end = Integer.parseInt(endDayField.getText().trim());
                double modifier = Double.parseDouble(modifierField.getText().trim());

                if (start < 1 || end > 30 || start > end || modifier < 0.8 || modifier > 1.2) {
                    throw new IllegalArgumentException("INVALID DAY RANGE OR MODIFIER VALUE.");
                }

                for (int d = start; d <= end; d++) {
                    property.getDates().get(d - 1).setEnvImpactModifier(modifier);
                }

                JOptionPane.showMessageDialog(this, String.format("MODIFIER UPDATED FOR DAYS %d-%d TO %s.", start, end, MODIFIER_FORMAT.format(modifier)), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                Container container = parentPanel.getParent();
                container.removeAll();
                container.add(createCalendarPanel(property), BorderLayout.CENTER);
                container.revalidate();
                container.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage(), "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private static class ColorIcon implements Icon {
        private final Color color;
        private static final int SIZE = 15;
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
}
