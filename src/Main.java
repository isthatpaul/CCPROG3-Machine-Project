/**
 * Main class to launch the application.
 * Initializes and displays the main view.
 */
public class Main {
    /**
     * Main method to start the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}