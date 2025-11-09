import javax.swing.SwingUtilities;

/**
 * The Main class serves as the entry point for the Green Property System GUI.
 * It initializes the main view of the application using Java Swing.
 * * @author Crisologo, Lim Un
 * @version 2.0 (Updated for MCO2 GUI)
 */
public class Main {
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}