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
 * Provides navigation between main menu, property management, and property creation.
 * Wires together PropertyManager and BookingService.
 */
public class MainView extends JFrame {

    private final PropertyManager manager;
    private final PropertyRepository repository;
    private final BookingService bookingService;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("PHP #,##0.00");
    private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#0.00");
    private final Font retroFont;

    /**
     * Constructs the main view, initializes UI components and services.
     */
    public MainView() {
        super("Green Property Exchange System (MCO2)");

        // Repository / service wiring (Dependency Inversion)
        this.manager = new PropertyManager();
        this.repository = manager; // PropertyManager implements PropertyRepository
        this.bookingService = new BookingServiceImpl(repository, new DefaultPriceStrategy());

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

        add(mainPanel);
    }

    /**
     * Loads the retro pixel font from resources.
     *
     * @param size font size
     * @return the loaded Font object
     */
    private Font loadRetroFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/PressStart2P-Regular.ttf");
            if (is != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                return font.deriveFont(size);
            } else {
                System.out.println("Retro font not found in resources, using fallback font.");
            }
        } catch (Exception e) {
            System.err.println("Error loading retro font: " + e.getMessage());
        }

        return new Font("Courier New", Font.BOLD, (int) size);
    }

    // ======== BACKGROUND PANEL =========
    /**
     * Custom JPanel that paints a background image.
     */
    private static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        /**
         * Constructs a BackgroundPanel with the specified image resource.
         * 
         * @param resourcePath path to the image resource
         */
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

        /**
         * Paints the background image scaled to fit the panel.
         * 
         * @param g the Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // ======== OUTLINED LABEL CREATOR =========
    /**
     * Creates a JLabel with outlined text for better visibility.
     * 
     * @param text the label text
     * @param size font size
     * @param textColor the text color
     * @return the outlined JLabel
     */
    private JLabel createOutlinedLabel(String text, float size, Color textColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            /**
             * Paints the outlined text.
             * 
             * @param g the Graphics context
             */
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

        // Let layout manage sizing; set font so preferred size is computed sensibly
        label.setOpaque(false);
        label.setFont(retroFont.deriveFont(size));
        label.setForeground(textColor);
        // small recommended minimum height so the outline isn't clipped on some LAFs
        label.setMinimumSize(new Dimension(100, (int)(size * 2.0)));
        return label;
    }

    /**
     * Creates a styled JButton with pixel-art aesthetics and scrolling text if needed.
     * 
     * @param text button text
     * @param fontSize font size
     * @return the styled JButton
     */
    private JButton createStyledButton(String text, float fontSize) {

        final String displayText = text;    // We store the real text here
        final int[] textOffset = {0};       // Used for scrolling

        JButton button = new JButton("") {  // Set button text to empty so Swing doesn't draw it

            /**
             * Paints the button with scrolling text if needed.
             * 
             * @param g the Graphics context
             */
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
            
            /**
             * Starts scrolling and changes background on hover.
             * 
             * @param e mouse event
             */
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                scrollTimer.start();
                button.setBackground(new Color(248, 248, 248));
            }

            /**
             * Stops scrolling and resets background on exit.
             * 
             * @param e mouse event
             */
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

    // ========= MAIN MENU PANEL =========
    /**
     * Creates the main menu panel.
     * 
     * @return the main menu JPanel
     */
    private JPanel createMainMenuPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/main_menu_bg.png");
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // Top spacer to push content towards vertical center
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        JPanel topSpacer = new JPanel();
        topSpacer.setOpaque(false);
        panel.add(topSpacer, gbc);

        // Content: title + buttons grouped together so they always move as a block
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title box (stacked)
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLine1 = createOutlinedLabel("GREEN PROPERTY", 38f, new Color(185, 255, 140));
        titleLine1.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLine1.setBorder(BorderFactory.createEmptyBorder(8, 10, 2, 10));
        titleBox.add(titleLine1);

        JLabel titleLine2 = createOutlinedLabel("EXCHANGE", 38f, new Color(185, 255, 140));
        titleLine2.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLine2.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));
        titleBox.add(titleLine2);

        JLabel subtitle = createOutlinedLabel("MCO2", 22f, new Color(210, 230, 190));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        titleBox.add(subtitle);

        content.add(titleBox);

        // Small gap between title and buttons (keeps them visually close)
        content.add(Box.createRigidArea(new Dimension(0, 8)));

        // Buttons box (vertical)
        JPanel buttonsBox = new JPanel();
        buttonsBox.setOpaque(false);
        buttonsBox.setLayout(new BoxLayout(buttonsBox, BoxLayout.Y_AXIS));
        buttonsBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Choose a consistent maximum width so buttons center nicely
        int buttonWidth = 360;
        int buttonHeight = 38;

        JButton btn1 = createStyledButton("MANAGE & VIEW PROPERTIES", 14f);
        btn1.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        btn1.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn1.addActionListener(e -> {
            mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
            cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
        });

        JButton btn2 = createStyledButton("CREATE NEW PROPERTY", 14f);
        btn2.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        btn2.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn2.addActionListener(e -> cardLayout.show(mainPanel, "CREATE_PROPERTY"));

        JButton btn3 = createStyledButton("EXIT", 14f);
        btn3.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        btn3.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn3.addActionListener(e -> System.exit(0));

        // Add buttons with small gaps
        buttonsBox.add(btn1);
        buttonsBox.add(Box.createRigidArea(new Dimension(0, 8)));
        buttonsBox.add(btn2);
        buttonsBox.add(Box.createRigidArea(new Dimension(0, 8)));
        buttonsBox.add(btn3);

        content.add(buttonsBox);

        panel.add(content, gbc);

        // Bottom spacer to keep content centered vertically
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setOpaque(false);
        panel.add(bottomSpacer, gbc);

        return panel;
    }

    // PROPERTY MANAGEMENT - uses main_menu_bg.png for consistency, with tint
    /**
     * Creates the property management panel.
     * 
     * @return the property management JPanel
     */
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

        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        actionsPanel.setBackground(new Color(194,255,97,192));

        JButton backBtn = createStyledButton("BACK TO MAIN MENU", 14f);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        JButton renameBtn = createStyledButton("RENAME", 14f);
        JButton priceBtn = createStyledButton("UPDATE BASE PRICE", 14f);
        JButton bookBtn = createStyledButton("SIMULATE BOOKING", 14f);
        JButton removeBtn = createStyledButton("REMOVE PROPERTY", 14f);
        JButton envImpactBtn = createStyledButton("MANAGE ENV IMPACT", 14f);
        JButton viewReservationsBtn = createStyledButton("VIEW RESERVATIONS", 14f);

        actionsPanel.add(renameBtn);
        actionsPanel.add(priceBtn);
        actionsPanel.add(bookBtn);
        actionsPanel.add(removeBtn);
        actionsPanel.add(envImpactBtn);
        actionsPanel.add(viewReservationsBtn);

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
        envImpactBtn.addActionListener(createEnvImpactListener(propertySelector, properties, detailsPanel));
        viewReservationsBtn.addActionListener(createViewReservationsListener(propertySelector, properties, detailsPanel));

        return panel;
    }

    /**
     * Creates the remove property action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @return the ActionListener
     */
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

    /**
     * Creates the booking action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @param detailsPanel the details panel to refresh
     * @return the ActionListener
     */
    private ActionListener createBookingListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property p = properties.get(selector.getSelectedIndex());
            JPanel bookingPanel = new JPanel(new GridLayout(5, 2));
            bookingPanel.add(new JLabel("GUEST NAME:")).setFont(retroFont.deriveFont(16f));
            JTextField guestField = new JTextField(10);
            bookingPanel.add(guestField);

            bookingPanel.add(new JLabel("GUEST TIER:")).setFont(retroFont.deriveFont(16f));
            String[] tiers = {GuestTier.REGULAR.toString(), GuestTier.SILVER.toString(), GuestTier.GOLD.toString(), GuestTier.PLATINUM.toString()};
            JComboBox<String> tierSelector = new JComboBox<>(tiers);
            bookingPanel.add(tierSelector);

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
                    int tierIndex = tierSelector.getSelectedIndex();
                    GuestTier tier = GuestTier.values()[tierIndex];

                    // Use service to perform booking (validations + pricing)
                    if (bookingService.book(p.getPropertyName(), guest, tier, checkIn, checkOut)) {
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

    /**
     * Creates the update price action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @param detailsPanel the details panel to refresh
     * @return the ActionListener
     */
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

    /**
     * Creates the rename property action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @return the ActionListener
     */
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

    /**
     * Creates the view reservations action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @param detailsPanel the details panel
     * @return the ActionListener
     */
    private ActionListener createViewReservationsListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property property = properties.get(selector.getSelectedIndex());
            // Prefer service to fetch reservations (keeps logic centralized)
            List<Reservation> reservations = bookingService.getReservations(property.getPropertyName());

            if (reservations.isEmpty()) {
                showInfoDialog("No reservations for " + property.getPropertyName());
                return;
            }

            DefaultListModel<Reservation> model = new DefaultListModel<>();
            for (Reservation r : reservations) model.addElement(r);

            JList<Reservation> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setFont(retroFont.deriveFont(12f));

            JScrollPane scroll = new JScrollPane(list);
            scroll.setPreferredSize(new Dimension(480, 220));

            JButton detailsBtn = createStyledButton("VIEW DETAILS", 14f);
            JButton removeBtn = createStyledButton("REMOVE RESERVATION", 14f);
            JButton closeBtn = createStyledButton("CLOSE", 14f);

            JPanel btns = new JPanel();
            btns.add(detailsBtn);
            btns.add(removeBtn);
            btns.add(closeBtn);

            JDialog dialog = new JDialog(this, "Reservations for " + property.getPropertyName(), true);
            dialog.setLayout(new BorderLayout(8,8));
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(btns, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(this);

            detailsBtn.addActionListener(ae -> {
                Reservation sel = list.getSelectedValue();
                if (sel == null) {
                    JOptionPane.showMessageDialog(dialog, "SELECT A RESERVATION FIRST.", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                showReservationDetails(sel);
            });

            removeBtn.addActionListener(ae -> {
                Reservation sel = list.getSelectedValue();
                if (sel == null) {
                    JOptionPane.showMessageDialog(dialog, "SELECT A RESERVATION FIRST.", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(dialog, "REMOVE SELECTED RESERVATION?", "CONFIRM", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = bookingService.removeReservation(property.getPropertyName(), sel);
                    if (ok) {
                        model.removeElement(sel);
                        // Refresh property panel so calendar updates
                        mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
                        cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");
                        JOptionPane.showMessageDialog(dialog, "RESERVATION REMOVED.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "FAILED TO REMOVE RESERVATION.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            closeBtn.addActionListener(ae -> dialog.dispose());

            dialog.setVisible(true);
        };
    }

    /**
     * Displays detailed information about a reservation.
     * 
     * @param reservation the Reservation object
     */
    private void showReservationDetails(Reservation reservation) {
        StringBuilder sb = new StringBuilder();
        sb.append("Guest: ").append(reservation.getGuestName()).append("\n");
        sb.append("Tier: ").append(reservation.getGuestTier()).append("\n");
        sb.append("Check-in: ").append(reservation.getCheckInDay()).append("\n");
        sb.append("Check-out: ").append(reservation.getCheckOutDay()).append("\n");
        sb.append("Nights: ").append(reservation.getNumberOfNights()).append("\n");
        sb.append("BREAKDOWN (Before -> After Discount):\n");
        for (int i = 0; i < reservation.getNumberOfNights(); i++) {
            double before = reservation.getNightlyRateBeforeDiscountByIndex(i);
            double after = reservation.getNightlyRateByIndex(i);
            sb.append(String.format("Night %d: %s -> %s\n", i+1, PRICE_FORMAT.format(before), PRICE_FORMAT.format(after)));
        }
        sb.append("\nTOTAL: ").append(PRICE_FORMAT.format(reservation.getTotalPrice()));

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(retroFont.deriveFont(14f));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(480, 320));

        JOptionPane.showMessageDialog(this, scrollPane, "RESERVATION DETAILS", JOptionPane.INFORMATION_MESSAGE);
    }

    // PROPERTY CREATION - also uses main_menu_bg.png for cohesive style
    /**
     * Creates the property creation panel.
     * 
     * @return the property creation JPanel
     */
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

    // ======== ENVIRONMENTAL IMPACT MANAGEMENT =========
    /**
     * Creates the environmental impact management action listener.
     * 
     * @param selector the property selector JComboBox
     * @param properties the list of properties
     * @param detailsPanel the details panel to refresh
     * @return the ActionListener
     */
    private ActionListener createEnvImpactListener(JComboBox<String> selector, List<Property> properties, JPanel detailsPanel) {
        return e -> {
            Property property = properties.get(selector.getSelectedIndex());

            JDialog envDialog = new JDialog(this, "Manage Environmental Impact - " + property.getPropertyName(), true);
            envDialog.setLayout(new BorderLayout());
            envDialog.setSize(500, 400);
            envDialog.setLocationRelativeTo(this);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Quick presets panel
            JPanel presetsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            presetsPanel.setBorder(BorderFactory.createTitledBorder("Quick Presets"));

            JButton earthDayBtn = createStyledButton("EARTH DAY (90%)", 12f);
            JButton highPollutionBtn = createStyledButton("HIGH POLLUTION (110%)", 12f);
            JButton resetAllBtn = createStyledButton("RESET ALL TO 100%", 12f);
            JButton customRangeBtn = createStyledButton("CUSTOM RANGE", 12f);

            earthDayBtn.addActionListener(evt -> {
                property.setEnvironmentalImpactRange(5, 5, 0.9);
                property.setEnvironmentalImpactRange(22, 22, 0.9);
                showSuccessDialog("Earth Day preset applied (Days 5 & 22 at 90%)");
                refreshDetailsPanel(detailsPanel, property);
                envDialog.dispose();
            });

            highPollutionBtn.addActionListener(evt -> {
                property.setEnvironmentalImpactRange(15, 15, 1.1);
                property.setEnvironmentalImpactRange(30, 30, 1.1);
                showSuccessDialog("High Pollution preset applied (Days 15 & 30 at 110%)");
                refreshDetailsPanel(detailsPanel, property);
                envDialog.dispose();
            });

            resetAllBtn.addActionListener(evt -> {
                for (int day = 1; day <= 30; day++) {
                    property.resetEnvironmentalImpact(day);
                }
                showSuccessDialog("All days reset to 100% modifier");
                refreshDetailsPanel(detailsPanel, property);
                envDialog.dispose();
            });

            // Custom range: open a small dialog for start/end/modifier
            customRangeBtn.addActionListener(evt -> {
                JPanel input = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField startField = new JTextField(4);
                JTextField endField = new JTextField(4);
                JTextField modField = new JTextField("1.00", 6);

                input.add(new JLabel("Start day (1-30):")); input.add(startField);
                input.add(new JLabel("End day (1-30):")); input.add(endField);
                input.add(new JLabel("Modifier (0.8 - 1.2):")); input.add(modField);

                int res = JOptionPane.showConfirmDialog(envDialog, input, "Custom Range", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    try {
                        int s = Integer.parseInt(startField.getText().trim());
                        int t = Integer.parseInt(endField.getText().trim());
                        double m = Double.parseDouble(modField.getText().trim());
                        if (!property.setEnvironmentalImpactRange(s, t, m)) {
                            throw new IllegalArgumentException("Invalid range or modifier");
                        }
                        showSuccessDialog(String.format("Modifier set for days %d-%d to %s", s, t, MODIFIER_FORMAT.format(m)));
                        refreshDetailsPanel(detailsPanel, property);
                        envDialog.dispose();
                    } catch (Exception ex) {
                        showErrorDialog("Invalid input: " + ex.getMessage());
                    }
                }
            });

            presetsPanel.add(earthDayBtn);
            presetsPanel.add(highPollutionBtn);
            presetsPanel.add(resetAllBtn);
            presetsPanel.add(customRangeBtn);

            JTextArea modifiersArea = new JTextArea();
            modifiersArea.setFont(retroFont.deriveFont(12f));
            modifiersArea.setEditable(false);
            updateModifiersDisplay(modifiersArea, property);

            JScrollPane scrollPane = new JScrollPane(modifiersArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Current Modifiers"));

            mainPanel.add(presetsPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JButton closeBtn = createStyledButton("CLOSE", 14f);
            closeBtn.addActionListener(evt -> envDialog.dispose());

            envDialog.add(mainPanel, BorderLayout.CENTER);
            envDialog.add(closeBtn, BorderLayout.SOUTH);
            envDialog.setVisible(true);
        };
    }

    /**
     * Updates the modifiers display area with current environmental impact modifiers.
     * 
     * @param area the JTextArea to update
     * @param property the Property object
     */
    private void updateModifiersDisplay(JTextArea area, Property property) {
        StringBuilder sb = new StringBuilder();
        for (int day = 1; day <= 30; day++) {
            double modifier = property.getDates().get(day - 1).getEnvImpactModifier();
            if (Math.abs(modifier - 1.0) > 1e-9) {
                sb.append("Day ").append(day).append(": ").append(MODIFIER_FORMAT.format(modifier * 100)).append("%\n");
            }
        }
        if (sb.length() == 0) {
            sb.append("All days at standard rate (100%)");
        }
        area.setText(sb.toString());
    }

    // CALENDAR PANEL - updated to match the calendar layout provided earlier
    /**
     * Creates the calendar panel for a property.
     * 
     * @param property the Property object
     * @return the calendar JPanel
     */
    private JPanel createCalendarPanel(Property property) {
        JPanel calendarPanel = new BackgroundPanel("/resources/main_menu_bg.png");
        calendarPanel.setLayout(new BorderLayout(10, 10));

        // INFO PANEL (top)
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

        // CALENDAR TABLE (SUN..SAT columns, 5 rows)
        String[] dayNames = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        DefaultTableModel model = new DefaultTableModel(dayNames, 5) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable calendarTable = new JTable(model);
        calendarTable.setFont(retroFont.deriveFont(14f));
        calendarTable.setRowHeight(40);
        calendarTable.getTableHeader().setFont(retroFont.deriveFont(13f));

        // Fill table cells with day info for days 1..30
        for (int day = 1; day <= 30; day++) {
            int row = (day - 1) / 7;
            int col = (day - 1) % 7;
            if (row < 5) {
                DateSlot slot = property.getDates().get(day - 1);
                double finalRate = property.calculateFinalDailyRate(day);
                String cellHtml = slot.isBooked()
                        ? "<html><b>DAY " + day + "</b><br><font color=gray><i>BOOKED</i></font></html>"
                        : "<html><b>DAY " + day + "</b><br>RATE: " + PRICE_FORMAT.format(finalRate) + "</html>";
                model.setValueAt(cellHtml, row, col);
            }
        }

        // Custom renderer to color cells based on booked/modifier status and provide tooltips
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(retroFont.deriveFont(13f));
                int day = row * 7 + column + 1;
                if (day > 30) { // empty cells after day 30
                    setBackground(new Color(36, 63, 41)); // match game bg
                    setText("");
                    setToolTipText(null);
                    return this;
                }

                DateSlot slot = property.getDates().get(day - 1);
                double mod = slot.getEnvImpactModifier();

                if (slot.isBooked()) {
                    setBackground(new Color(100, 85, 53)); // Brown for booked
                    if (slot.getReservation() != null) {
                        setToolTipText("Booked by: " + slot.getReservation().getGuestName());
                    } else {
                        setToolTipText("BOOKED");
                    }
                } else if (mod >= 0.80 && mod <= 0.89) {
                    setBackground(new Color(194, 255, 97)); // Neon leaf
                    setToolTipText("Modifier: " + MODIFIER_FORMAT.format(mod));
                } else if (Math.abs(mod - 1.0) < 1e-9) {
                    setBackground(new Color(228, 240, 211, 222)); // Soft menu bg
                    setToolTipText("Modifier: 1.00");
                } else if (mod >= 1.01 && mod <= 1.20) {
                    setBackground(new Color(255, 240, 58)); // Arcade yellow
                    setToolTipText("Modifier: " + MODIFIER_FORMAT.format(mod));
                } else {
                    setBackground(new Color(228, 240, 211, 222));
                    setToolTipText("Modifier: " + MODIFIER_FORMAT.format(mod));
                }
                setForeground(new Color(36, 63, 41));
                return this;
            }
        });

        // Mouse click => show details for the clicked day (same behavior as earlier)
        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Handles mouse click events on the calendar table.
             * 
             * @param e the MouseEvent
             */
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = calendarTable.rowAtPoint(e.getPoint());
                int col = calendarTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    int day = row * 7 + col + 1;
                    if (day > 30) return;
                    DateSlot slot = property.getDates().get(day - 1);
                    StringBuilder msg = new StringBuilder();
                    msg.append("Day ").append(day).append("\n");
                    msg.append("Env Modifier: ").append(MODIFIER_FORMAT.format(slot.getEnvImpactModifier())).append("\n");
                    msg.append("Final Rate: ").append(PRICE_FORMAT.format(property.calculateFinalDailyRate(day))).append("\n");
                    msg.append("Status: ").append(slot.isBooked() ? "BOOKED" : "AVAILABLE").append("\n");
                    if (slot.isBooked() && slot.getReservation() != null) {
                        Reservation r = slot.getReservation();
                        msg.append("\nReservation Details:\n");
                        msg.append("Guest: ").append(r.getGuestName()).append(" (").append(r.getGuestTier()).append(")\n");
                        msg.append("Check-in: ").append(r.getCheckInDay()).append("\n");
                        msg.append("Check-out: ").append(r.getCheckOutDay()).append("\n");
                        msg.append("Total: ").append(PRICE_FORMAT.format(r.getTotalPrice())).append("\n");
                    }
                    JOptionPane.showMessageDialog(MainView.this, msg.toString(), "DATE DETAILS", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(calendarTable);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);

        // Add modifier panel (existing helper)
        JPanel modifierPanel = createModifierPanel(property, calendarPanel);
        calendarPanel.add(modifierPanel, BorderLayout.SOUTH);

        return calendarPanel;
    }

    /**
     * Creates the environmental impact modifier panel.
     * 
     * @param property the Property object
     * @param parentPanel the parent JPanel
     * @return the modifier JPanel
     */
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
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(updateBtn, gbc);

        JButton checkRangeBtn = createStyledButton("CHECK RANGE COUNTS", 12f);
        gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(checkRangeBtn, gbc);

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

                if (start < 1 || end > 30 || start > end) {
                    throw new IllegalArgumentException("Invalid day range (1-30).");
                }
                if (modifier < 0.8 || modifier > 1.2) {
                    throw new IllegalArgumentException("Modifier must be between 0.80 and 1.20.");
                }

                // Use property-level API so any internal validation/logic is centralized there
                boolean ok = property.setEnvironmentalImpactRange(start, end, modifier);
                if (!ok) {
                    throw new IllegalArgumentException("Property rejected the modifier range (check implementation).");
                }

                JOptionPane.showMessageDialog(this,
                        String.format("MODIFIER UPDATED FOR DAYS %d-%d TO %s.", start, end, MODIFIER_FORMAT.format(modifier)),
                        "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                // Refresh the whole property-management view (consistent with other refreshes)
                mainPanel.add(createPropertyManagementPanel(), "MANAGE_PROPERTY_REFRESH");
                cardLayout.show(mainPanel, "MANAGE_PROPERTY_REFRESH");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ENTER VALID NUMBERS FOR START, END, AND MODIFIER.", "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage(), "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        checkRangeBtn.addActionListener(e -> {
            try {
                int start = Integer.parseInt(startDayField.getText().trim());
                int end = Integer.parseInt(endDayField.getText().trim());
                if (start < 1 || end > 30 || start > end) {
                    throw new IllegalArgumentException("INVALID DAY RANGE.");
                }
                int booked = property.countBookedDatesInRange(start, end);
                int available = property.countAvailableDatesInRange(start, end);
                JOptionPane.showMessageDialog(this,
                        String.format("Days %d-%d => BOOKED: %d | AVAILABLE: %d", start, end, booked, available),
                        "RANGE COUNTS", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ENTER VALID NUMBERS FOR START AND END DAYS.", "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage(), "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * ColorIcon is a simple Icon implementation that displays a colored square.
     * Used for the legend in the calendar panel.
     */
    private static class ColorIcon implements Icon {
        private final Color color;
        private static final int SIZE = 15;
        /**
         * Creates a ColorIcon with the specified color.
         * 
         * @param color the Color to use
         */
        public ColorIcon(Color color) { 
            this.color = color; 
        }

        /**
         * Paints the icon at the specified location.
         * 
         * @param c the component to which the icon is added
         * @param g the Graphics context
         * @param x the x-coordinate
         * @param y the y-coordinate
         * @return void
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, SIZE - 1, SIZE - 1);
        }

        /**
         * Returns the icon width.
         * 
         * @return the width of the icon
         */
        @Override
        public int getIconWidth() { 
            return SIZE; 
        }

        /**
         * Returns the icon height.
         * 
         * @return the height of the icon
         */
        @Override
        public int getIconHeight() { 
            return SIZE; 
        }
    }

    // ======== DIALOG MANAGEMENT =========
    /**
     * Displays an error dialog with the specified message.
     * 
     * @param message the error message
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success dialog with the specified message.
     * 
     * @param message the success message
     */
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an info dialog with the specified message.
     * 
     * @param message the info message
     */
    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "INFO", JOptionPane.INFORMATION_MESSAGE);
    }

    // ======== PANEL MANAGEMENT =========
    /**
     * Refreshes the details panel with updated property information.
     * 
     * @param detailsPanel the details JPanel to refresh
     * @param property the Property object
     */
    private void refreshDetailsPanel(JPanel detailsPanel, Property property) {
        detailsPanel.removeAll();
        detailsPanel.add(createCalendarPanel(property), BorderLayout.CENTER);
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
}