import java.util.ArrayList;
import java.util.List;

/**
 * The PropertyManager class manages all properties
 * in the Green Property System. It handles adding, removing,
 * renaming, and booking operations for each Property.
 * <p>
 * Each property is uniquely identified by name, and duplicate names
 * are not allowed. The manager also provides access to the complete
 * list of managed properties.
 * </p>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     2.1
 */
public class PropertyManager {
    private List<Property> properties;

    /** Creates a new PropertyManager with an empty list of properties. */
    public PropertyManager() {
        this.properties = new ArrayList<>();
    }

    /**
     * Adds a new property to the system if its name is unique.
     *
     * @param name the name of the property to add
     * @param basePrice the base price per night for the property
     * @return true if the property was added successfully, false if a duplicate name exists
     */
    public boolean addProperty(String name, double basePrice) {
        if (!isUniqueName(name)) {
            System.out.println("Invalid: A property named \"" + name + "\" already exists.");
            return false;
        }

        Property newProperty = new Property(name, basePrice);
        properties.add(newProperty);
        System.out.println("Property \"" + name + "\" added successfully.");
        return true;
    }

    /**
     * Removes an existing property if it has no active reservations.
     *
     * @param name the name of the property to remove
     * @return true if the property was removed successfully, false otherwise
     */
    public boolean removeProperty(String name) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);

            if (p.getPropertyName().equalsIgnoreCase(name)) {
                if (!p.canBeRemoved()) {
                    System.out.println("Cannot remove \"" + name + "\": Active reservations exist.");
                    return false;
                }

                properties.remove(i);
                System.out.println("Property \"" + name + "\" removed.");
                return true;
            }
        }

        System.out.println("No property found with the name \"" + name + "\".");
        return false;
    }

    /**
     * Renames an existing property if the new name is unique.
     *
     * @param oldName the current name of the property
     * @param newName the new name to assign
     * @return true if the property was renamed successfully, false otherwise
     */
    public boolean renameProperty(String oldName, String newName) {
        if (!isUniqueName(newName)) {
            System.out.println("Invalid: A property named \"" + newName + "\" already exists.");
            return false;
        }

        Property p = getProperty(oldName);
        if (p != null) {
            p.renameProperty(newName);
            System.out.println("Property \"" + oldName + "\" renamed to \"" + newName + "\".");
            return true;
        }

        System.out.println("No property found with the name \"" + oldName + "\".");
        return false;
    }

    /**
     * Books a reservation for the specified property.
     *
     * @param name the name of the property to book
     * @param guestName the name of the guest making the reservation
     * @param checkIn the check-in day (1–30)
     * @param checkOut the check-out day (1–30)
     * @return true if the booking was successful, false otherwise
     */
    public boolean bookProperty(String name, String guestName, int checkIn, int checkOut) {
        Property p = getProperty(name);
        if (p == null) {
            System.out.println("No property found with the name \"" + name + "\".");
            return false;
        }
        return p.addReservation(guestName, checkIn, checkOut);
    }

    /**
     * Retrieves a property by its name.
     *
     * @param name the name of the property
     * @return the Property object if found, or null if no match exists
     */
    public Property getProperty(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns a list of all properties currently managed by the system.
     * The returned list directly references the internal data.
     *
     * @return a list of all managed Property objects
     */
    public List<Property> listProperties() {
        return properties;
    }

    /**
     * Checks if the given property name is unique.
     *
     * @param name the name to check
     * @return true if the name is unique, false otherwise
     */
    public boolean isUniqueName(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }
}
