import java.util.List;
import java.util.Scanner;

/**
 * The Main class serves as the entry point for the Green Property System.
 * It provides a text-based interface for managing properties and reservations
 * through various menu options.
 * <p>
 * This class coordinates actions such as creating properties, viewing details,
 * managing reservations, and simulating bookings.
 * </p>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     1.3
 */
public class Main {

    /** The manager responsible for all property operations. */
    private PropertyManager propertyManager;

    /** Scanner used for reading user input from the console. */
    private Scanner scanner;

    /**
     * Creates a new Main application instance with an initialized
     * PropertyManager and Scanner.
     */
    public Main() {
        this.propertyManager = new PropertyManager();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main entry point of the program.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * Starts the main application loop, showing the main menu and
     * responding to user selections until the program exits.
     */
    public void start() {
        System.out.println("===========================================");
        System.out.println("      GREEN PROPERTY EXCHANGE SYSTEM      ");
        System.out.println("===========================================");

        boolean running = true;

        while (running) {
            displayMainMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    createProperty();
                    break;
                case 2:
                    viewProperty();
                    break;
                case 3:
                    manageProperty();
                    break;
                case 4:
                    simulateBooking();
                    break;
                case 5:
                    listAllProperties();
                    break;
                case 6:
                    running = false;
                    System.out.println("Thank you for using Green Property Exchange System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /** Displays the main menu options. */
    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Create Property");
        System.out.println("2. View Property Details");
        System.out.println("3. Manage Property");
        System.out.println("4. Simulate Booking");
        System.out.println("5. List All Properties");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    /** Creates a new property by prompting the user for details. */
    private void createProperty() {
        System.out.println("\n=== CREATE PROPERTY ===");
        System.out.print("Enter property name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Property name cannot be empty.");
            return;
        }

        double basePrice = 0.0;
        System.out.print("Enter base price per night (default 1500.0): ");
        String priceInput = scanner.nextLine().trim();

        if (priceInput.isEmpty()) {
            basePrice = 1500.0;
        } else {
            try {
                basePrice = Double.parseDouble(priceInput);
                if (basePrice < 100) {
                    System.out.println("Base price must be at least PHP 100.00.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for base price.");
                return;
            }
        }

        boolean success = propertyManager.addProperty(name, basePrice);
        if (success) {
            System.out.println("Property \"" + name + "\" created successfully with base price PHP " + basePrice + ".");
        }
    }

    /** Allows the user to view details of an existing property. */
    private void viewProperty() {
        System.out.println("\n=== VIEW PROPERTY DETAILS ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to view details: ");
        int choice = choice();

        if (choice < 1 || choice > properties.size()) {
            System.out.println("Invalid property selection.");
            return;
        }

        Property selectedProperty = properties.get(choice - 1);
        viewPropertySubMenu(selectedProperty);
    }

    /** Displays options for viewing property details. */
    private void viewPropertySubMenu(Property property) {
        boolean viewing = true;
        while (viewing) {
            System.out.println("\n=== VIEW PROPERTY: " + property.getPropertyName() + " ===");
            System.out.println("1. Calendar View");
            System.out.println("2. High-Level Information");
            System.out.println("3. Detailed Information");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    displayCalendar(property);
                    break;
                case 2:
                    displayHighLevelInfo(property);
                    break;
                case 3:
                    displayPropertyDetails(property);
                    break;
                case 4:
                    viewing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }

    /** Displays the calendar view for the selected property. */
    private void displayCalendar(Property property) {
        System.out.println("\n=== CALENDAR VIEW FOR " + property.getPropertyName() + " ===");
        System.out.println("┌───┬───┬───┬───┬───┬───┬───┐");

        for (int week = 0; week < 5; week++) {
            System.out.print("│");
            for (int day = 1; day <= 7; day++) {
                int dayNumber = week * 7 + day;
                if (dayNumber > 30) {
                    System.out.print("   │");
                } else {
                    DateSlot slot = property.getDates().get(dayNumber - 1);
                    System.out.print(slot.isBooked() ? " R │" : " A │");
                }
            }
            System.out.println();
            if (week < 4) {
                System.out.println("├───┼───┼───┼───┼───┼───┼───┤");
            }
        }

        System.out.println("└───┴───┴───┴───┴───┴───┴───┘");
        System.out.println("\n[A] Available");
        System.out.println("[R] Reserved");
        System.out.println("Base Price: PHP " + property.getBasePrice());
    }

    /** Displays high-level summary information for the property. */
    private void displayHighLevelInfo(Property property) {
        System.out.println("\n=== HIGH-LEVEL INFORMATION ===");
        System.out.println("Property Name: " + property.getPropertyName());
        System.out.println("Base Price per Night: PHP " + property.getBasePrice());
        System.out.println("Total Reservations Made: " + property.getReservations().size());
        System.out.println("Available Dates: " + property.countAvailableDates());
        System.out.println("Total Earnings: PHP " + property.getTotalEarnings());
    }

    /** Displays full details and reservations of the property. */
    private void displayPropertyDetails(Property property) {
        System.out.println("\n=== PROPERTY DETAILS ===");
        System.out.println("Property Name: " + property.getPropertyName());
        System.out.println("Base Price per Night: PHP " + property.getBasePrice());
        System.out.println("30-Day Calendar:");
        property.displayCalendar();
        property.displayReservations();
    }

    /** Opens the property management menu. */
    private void manageProperty() {
        System.out.println("\n=== MANAGE PROPERTY ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to manage: ");
        int choice = choice();

        if (choice < 1 || choice > properties.size()) {
            System.out.println("Invalid property selection.");
            return;
        }

        Property selectedProperty = properties.get(choice - 1);
        boolean managing = true;

        while (managing) {
            System.out.println("\n=== MANAGE PROPERTY: " + selectedProperty.getPropertyName() + " ===");
            System.out.println("1. Change Property Name");
            System.out.println("2. Update Base Price");
            System.out.println("3. Remove Reservation");
            System.out.println("4. Remove Property");
            System.out.println("5. View Property Details");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int manageChoice = getUserChoice();

            switch (manageChoice) {
                case 1:
                    changePropertyName(selectedProperty);
                    break;
                case 2:
                    updateBasePrice(selectedProperty);
                    break;
                case 3:
                    removeReservation(selectedProperty);
                    break;
                case 4:
                    removeProperty(selectedProperty);
                    managing = false;
                    break;
                case 5:
                    displayPropertyDetails(selectedProperty);
                    break;
                case 6:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }

    /** Removes a reservation from the specified property. */
    private void removeReservation(Property property) {
        System.out.println("\n=== REMOVE RESERVATION ===");
        List<Reservation> reservations = property.getReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations to remove.");
            return;
        }

        for (int i = 0; i < reservations.size(); i++) {
            System.out.println((i + 1) + ". " + reservations.get(i));
        }

        System.out.print("Enter the number of the reservation to remove: ");
        int choice = choice();

        if (choice < 1 || choice > reservations.size()) {
            System.out.println("Invalid reservation selection.");
            return;
        }

        Reservation toRemove = reservations.get(choice - 1);
        property.removeReservation(toRemove);
        System.out.println("Reservation for " + toRemove.getGuestName() + " removed successfully.");
    }

    /** Changes the name of a property. */
    private void changePropertyName(Property property) {
        System.out.print("Enter new property name: ");
        String newName = scanner.nextLine().trim();

        if (newName.isEmpty()) {
            System.out.println("Property name cannot be empty.");
            return;
        }

        if (!propertyManager.isUniqueName(newName)) {
            System.out.println("A property with that name already exists.");
            return;
        }

        property.setPropertyName(newName);
        System.out.println("Property name updated successfully to \"" + newName + "\".");
    }

    /** Updates the base price per night of a property. */
    private void updateBasePrice(Property property) {
        System.out.print("Enter new base price per night: ");
        String input = scanner.nextLine().trim();

        try {
            double newBasePrice = Double.parseDouble(input);
            if (newBasePrice < 100) {
                System.out.println("Base price must be at least PHP 100.00.");
                return;
            }
            property.updateBasePrice(newBasePrice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for base price.");
        }
    }

    /** Removes a property from the system. */
    private void removeProperty(Property property) {
        boolean success = propertyManager.removeProperty(property.getPropertyName());
        if (success) {
            System.out.println("Property \"" + property.getPropertyName() + "\" removed successfully.");
        }
    }

    /** Lists all properties currently managed by the system. */
    private void listAllProperties() {
        System.out.println("\n=== LIST OF ALL PROPERTIES ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
    }

    /** Displays all properties with their base prices. */
    private void displayPropertiesList(List<Property> properties) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            System.out.println((i + 1) + ". " + p.getPropertyName() + " (Base Price: PHP " + p.getBasePrice() + ")");
        }
    }

    /** Simulates creating a new reservation for a selected property. */
    private void simulateBooking() {
        System.out.println("\n=== SIMULATE BOOKING ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to book: ");
        int choice = choice();

        if (choice < 1 || choice > properties.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Property selected = properties.get(choice - 1);
        System.out.print("Enter guest name: ");
        String guest = scanner.nextLine().trim();
        System.out.print("Enter check-in day (1-30): ");
        int in = choice();
        System.out.print("Enter check-out day (1-30): ");
        int out = choice();

        selected.addReservation(guest, in, out);
    }

    /** Reads and validates user input for menu selections. */
    private int getUserChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /** Reads and validates numeric input for general use. */
    private int choice() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
