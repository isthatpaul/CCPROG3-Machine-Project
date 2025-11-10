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


    // ======== PROPERTY MANAGEMENT PANEL =========
    private JPanel createPropertyManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(228, 240, 211));

        JLabel label = new JLabel("PROPERTY MANAGEMENT COMING SOON", SwingConstants.CENTER);
        label.setFont(retroFont.deriveFont(20f));
        panel.add(label, BorderLayout.CENTER);

        JButton backBtn = createStyledButton("BACK TO MAIN MENU", 13f);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        panel.add(backBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ======== PROPERTY CREATION PANEL (placeholder) =========
    private JPanel createPropertyCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("CREATE PROPERTY FORM COMING SOON", SwingConstants.CENTER);
        label.setFont(retroFont.deriveFont(18f));
        panel.add(label, BorderLayout.CENTER);

        JButton backBtn = createStyledButton("BACK TO MAIN MENU", 13f);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        panel.add(backBtn, BorderLayout.SOUTH);
        return panel;
    }
}