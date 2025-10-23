import java.util.ArrayList;
import java.util.List;

/**
 * The {@code PropertyManager} class manages a collection of {@link Property} objects
 * within the Green Property system.
 * <p>
 * It provides functionality to add, remove, rename, and list properties,
 * as well as handle bookings on behalf of properties.
 * <p>
 * This class demonstrates the concept of <b>encapsulation</b> by keeping
 * the property list private and exposing controlled access through public methods.
 *
 * @author
 * Crisologo, Lim Un
 * @version 2.0
 */
public class PropertyManager
{
    /** A list storing all {@link Property} objects managed by this system. */
    private List<Property> properties;

    /**
     * Constructs a new {@code PropertyManager} with an empty list of properties.
     */
    public PropertyManager()
    {
        this.properties = new ArrayList<>();
    }

    /**
     * Adds a new property to the system, if the name is unique.
     *
     * @param name       The name of the property to add
     * @param basePrice  The base price per night for the property
     * @return {@code true} if the property was successfully added;
     *         {@code false} if the name is not unique
     */
    public boolean addProperty(String name, double basePrice)
    {
        if (!isUniqueName(name))
        {
            System.out.println("Invalid: A property named \"" + name + "\" already exists.");
            return false;
        }

        Property newProperty = new Property(name, basePrice);
        properties.add(newProperty);
        System.out.println("Property \"" + name + "\" added successfully.");
        return true;
    }

    /**
     * Removes an existing property by name, if no active reservations exist.
     *
     * @param name The name of the property to remove
     * @return {@code true} if the property was successfully removed;
     *         {@code false} if the property does not exist or cannot be removed
     */
    public boolean removeProperty(String name)
    {
        for (int i = 0; i < properties.size(); i++)
        {
            Property p = properties.get(i);
            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                if (!p.canBeRemoved())
                {
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
     * Renames an existing property, provided the new name is unique.
     *
     * @param oldName The current name of the property
     * @param newName The desired new name
     * @return {@code true} if renamed successfully; {@code false} if the new name
     *         is not unique or the property cannot be found
     */
    public boolean renameProperty(String oldName, String newName)
    {
        if (!isUniqueName(newName))
        {
            System.out.println("Invalid: A property named \"" + newName + "\" already exists.");
            return false;
        }

        Property p = getProperty(oldName);
        if (p != null)
        {
            p.renameProperty(newName);
            System.out.println("Property \"" + oldName + "\" renamed to \"" + newName + "\".");
            return true;
        }

        System.out.println("No property found with the name \"" + oldName + "\".");
        return false;
    }

    /**
     * Attempts to create a reservation for a specified property.
     *
     * @param name       The name of the property to book
     * @param guestName  The guest making the reservation
     * @param checkIn    The check-in day (1–30)
     * @param checkOut   The check-out day (1–30)
     * @return {@code true} if the booking was successful; {@code false} otherwise
     */
    public boolean bookProperty(String name, String guestName, int checkIn, int checkOut)
    {
        Property p = getProperty(name);
        if (p == null)
        {
            System.out.println("No property found with the name \"" + name + "\".");
            return false;
        }
        return p.addReservation(guestName, checkIn, checkOut);
    }

    /**
     * Retrieves a {@link Property} object by name.
     *
     * @param name The name of the property to find
     * @return The corresponding {@link Property} if found, or {@code null} if not
     */
    public Property getProperty(String name)
    {
        for (Property p : properties)
        {
            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns a list of all properties currently managed.
     * <p>
     * The returned list is a direct reference to the internal list,
     * so modifications affect the original data.
     *
     * @return A list of all managed {@link Property} objects
     */
    public List<Property> listProperties()
    {
        return properties;
    }

    /**
     * Checks if a property name is unique among all managed properties.
     *
     * @param name The property name to check
     * @return {@code true} if no other property shares this name;
     *         {@code false} otherwise
     */
    public boolean isUniqueName(String name)
    {
        for (Property p : properties)
        {
            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                return false;
            }
        }
        return true;
    }
}
