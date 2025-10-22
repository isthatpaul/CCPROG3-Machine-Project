import java.util.List;
import java.util.Scanner;

/**
 * The {@code Main} class serves as the entry point for the Green Property System.
 * It provides a text-based menu interface for managing properties and reservations.
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     1.3
 */
public class Main
{
    private PropertyManager propertyManager;
    private Scanner scanner;

    /**
     * Constructs a new Main application with a PropertyManager instance.
     */
    public Main()
    {
        this.propertyManager = new PropertyManager();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main entry point of the program.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        Main app = new Main();
        app.start();
    }

    /**
     * Starts the main application loop, displaying the menu and handling user input.
     */
    public void start()
    {
        System.out.println("===========================================");
        System.out.println("      GREEN PROPERTY EXCHANGE SYSTEM      ");
        System.out.println("===========================================");

        boolean running = true;

        while (running)
        {
            displayMainMenu();
            int choice = getUserChoice();

            switch (choice)
            {
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

            if (running)
            {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /** Displays the main menu options to the user. */
    private void displayMainMenu()
    {
        System.out.println("\nMain Menu:");
        System.out.println("1. Create Property");
        System.out.println("2. View Property Details");
        System.out.println("3. Manage Property");
        System.out.println("4. Simulate Booking");
        System.out.println("5. List All Properties");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    /** Handles property creation by prompting the user for details. */
    private void createProperty()
    {
        System.out.println("\n=== CREATE PROPERTY ===");

        System.out.print("Enter property name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty())
        {
            System.out.println("Property name cannot be empty.");
            return;
        }

        double basePrice = 0.0;
        System.out.print("Enter base price per night (default 1500.0): ");
        String priceInput = scanner.nextLine().trim();

        if (priceInput.isEmpty())
        {
            basePrice = 1500.0;
        }
        else
        {
            try
            {
                basePrice = Double.parseDouble(priceInput);
                if (basePrice < 100)
                {
                    System.out.println("Base price must be at least PHP 100.00.");
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid input for base price.");
                return;
            }
        }

        boolean success = propertyManager.addProperty(name, basePrice);

        if (success)
        {
            System.out.println("Property \"" + name + "\" created successfully with base price PHP " + basePrice + ".");
        }
    }

    /** Handles viewing details of a specific property. */
    private void viewProperty()
    {
        System.out.println("\n=== VIEW PROPERTY DETAILS ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty())
        {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to view details: ");
        int choice = choice();
        if (choice < 1 || choice > properties.size())
        {
            System.out.println("Invalid property selection.");
            return;
        }

        Property selectedProperty = properties.get(choice - 1);
        viewPropertySubMenu(selectedProperty);
    }

    /** Displays the submenu for viewing a specific property's details. */
    private void viewPropertySubMenu(Property property)
    {
        boolean viewing = true;
        while (viewing)
        {
            System.out.println("\n=== VIEW PROPERTY: " + property.getPropertyName() + " ===");
            System.out.println("1. Calendar View");
            System.out.println("2. High-Level Information");
            System.out.println("3. Detailed Information");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            switch (choice)
            {
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

    /** Displays calendar view for a property. */
    private void displayCalendar(Property property)
    {
        System.out.println("\n=== CALENDAR VIEW FOR " + property.getPropertyName() + " ===");
        System.out.println("┌───┬───┬───┬───┬───┬───┬───┐");

        for (int week = 0; week < 5; week++)
        {
            System.out.print("│");
            for (int day = 1; day <= 7; day++)
            {
                int dayNumber = week * 7 + day;
                if (dayNumber > 30)
                {
                    System.out.print("   │");
                }
                else
                {
                    DateSlot dateSlot = property.getDates().get(dayNumber - 1);
                    if (dateSlot.isBooked())
                    {
                        System.out.print(" R │");
                    }
                    else
                    {
                        System.out.print(" A │");
                    }
                }
            }
            System.out.println();
            if (week < 4)
            {
                System.out.println("├───┼───┼───┼───┼───┼───┼───┤");
            }
        }
        System.out.println("└───┴───┴───┴───┴───┴───┴───┘");

        System.out.println("\n[A] Available");
        System.out.println("[R] Reserved");
        System.out.println("Base Price: PHP " + property.getBasePrice());
    }

    /** Displays high-level information about a given property. */
    private void displayHighLevelInfo(Property property)
    {
        System.out.println("\n=== HIGH-LEVEL INFORMATION ===");
        System.out.println("Property Name: " + property.getPropertyName());
        System.out.println("Base Price per Night: PHP " + property.getBasePrice());
        System.out.println("Total Reservations Made: " + property.getReservations().size());
        System.out.println("Available Dates: " + property.countAvailableDates());
        System.out.println("Total Earnings: PHP " + property.getTotalEarnings());
    }

    /** Displays detailed information about a given property. */
    private void displayPropertyDetails(Property property)
    {
        System.out.println("\n=== PROPERTY DETAILS ===");
        System.out.println("Property Name: " + property.getPropertyName());
        System.out.println("Base Price per Night: PHP " + property.getBasePrice());
        System.out.println("30-Day Calendar:");
        property.displayCalendar();
        property.displayReservations();
    }

    /** Handles managing reservations for a specific property. */
    private void manageProperty()
    {
        System.out.println("\n=== MANAGE PROPERTY ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty())
        {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to manage: ");
        int choice = choice();

        if (choice < 1 || choice > properties.size())
        {
            System.out.println("Invalid property selection.");
            return;
        }

        Property selectedProperty = properties.get(choice - 1);

        boolean managing = true;
        while (managing)
        {
            System.out.println("\n=== MANAGE PROPERTY: " + selectedProperty.getPropertyName() + " ===");
            System.out.println("1. Change Property Name");
            System.out.println("2. Update Base Price");
            System.out.println("3. Remove Reservation");
            System.out.println("4. Remove Property");
            System.out.println("5. View Property Details");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int manageChoice = getUserChoice();

            switch (manageChoice)
            {
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

    /** Removes a reservation from a property. */
    private void removeReservation(Property property)
    {
        System.out.println("\n=== REMOVE RESERVATION ===");
        List<Reservation> reservations = property.getReservations();

        if (reservations.isEmpty())
        {
            System.out.println("No reservations to remove.");
            return;
        }

        for (int i = 0; i < reservations.size(); i++)
        {
            Reservation r = reservations.get(i);
            System.out.println((i + 1) + ". " + r);
        }

        System.out.print("Enter the number of the reservation to remove: ");
        int choice = choice();

        if (choice < 1 || choice > reservations.size())
        {
            System.out.println("Invalid reservation selection.");
            return;
        }

        Reservation toRemove = reservations.get(choice - 1);
        property.removeReservation(toRemove);
        System.out.println("Reservation for " + toRemove.getGuestName() + " removed successfully.");
    }

    /** Changes the name of a property. */
    private void changePropertyName(Property property)
    {
        System.out.print("Enter new property name: ");
        String newName = scanner.nextLine().trim();

        if (newName.isEmpty())
        {
            System.out.println("Property name cannot be empty.");
            return;
        }

        if (!propertyManager.isUniqueName(newName))
        {
            System.out.println("A property with that name already exists.");
            return;
        }

        property.setPropertyName(newName);
        System.out.println("Property name updated successfully to \"" + newName + "\".");
    }

    /** Updates the base price of a property. */
    private void updateBasePrice(Property property)
    {
        System.out.print("Enter new base price per night: ");
        String priceInput = scanner.nextLine().trim();

        double newBasePrice;
        try
        {
            newBasePrice = Double.parseDouble(priceInput);
            if (newBasePrice < 100)
            {
                System.out.println("Base price must be at least PHP 100.00.");
                return;
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Invalid input for base price.");
            return;
        }

        property.updateBasePrice(newBasePrice);
    }

    /** Removes a property from the PropertyManager. */
    private void removeProperty(Property property)
    {
        boolean success = propertyManager.removeProperty(property.getPropertyName());
        if (success)
        {
            System.out.println("Property \"" + property.getPropertyName() + "\" removed successfully.");
        }
    }

    /** Lists all properties managed by the PropertyManager. */
    private void listAllProperties()
    {
        System.out.println("\n=== LIST OF ALL PROPERTIES ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty())
        {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
    }

    /** Displays a numbered list of properties. */
    private void displayPropertiesList(List<Property> properties)
    {
        for (int i = 0; i < properties.size(); i++)
        {
            Property p = properties.get(i);
            System.out.println((i + 1) + ". " + p.getPropertyName() + " (Base Price: PHP " + p.getBasePrice() + ")");
        }
    }

    /** Simulates booking a reservation for a property. */
    private void simulateBooking()
    {
        System.out.println("\n=== SIMULATE BOOKING ===");
        List<Property> properties = propertyManager.listProperties();

        if (properties.isEmpty())
        {
            System.out.println("No properties available.");
            return;
        }

        displayPropertiesList(properties);
        System.out.print("Enter the number of the property to simulate booking: ");
        int choice = choice();
        if (choice < 1 || choice > properties.size())
        {
            System.out.println("Invalid property selection.");
            return;
        }

        Property selectedProperty = properties.get(choice - 1);
        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine().trim();
        System.out.print("Enter check-in day (1–30): ");
        int checkIn = choice();
        System.out.print("Enter check-out day (1–30): ");
        int checkOut = choice();

        selectedProperty.addReservation(guestName, checkIn, checkOut);
    }

    /** Gets user choice with validation. */
    private int getUserChoice()
    {
        while (true)
        {
            String input = scanner.nextLine().trim();
            try
            {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e)
            {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /** Helper for numeric input parsing. */
    private int choice()
    {
        while (true)
        {
            String input = scanner.nextLine().trim();
            try
            {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e)
            {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
