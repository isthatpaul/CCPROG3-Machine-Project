import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The Main class serves as the entry point for the Green Property System GUI.
 * It initializes the main view of the application using Java Swing.
 *
 * @author Crisologo, Lim Un
 * @version 2.2 (Updated for MCO2 NDS Style)
 */
public class Main {
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Apply a simple look and feel for stability, but rely on custom colors in MainView
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Ignore L&F issues
            }

            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}