import java.util.ArrayList;
import java.util.List;

/**
 * The {@code PropertyManager} class manages all {@link Property} objects
 * within the Green Property system.
 * <p>
 * It provides centralized operations for adding, removing, renaming, and booking properties.
 * Each property is uniquely identified by name, and the manager ensures that
 * no duplicates are added.
 *
 * <p><b>Key Responsibilities:</b></p>
 * <ul>
 *     <li>Maintain a collection of all {@link Property} instances</li>
 *     <li>Ensure unique property names</li>
 *     <li>Support property booking and removal</li>
 *     <li>Provide access to the list of managed properties</li>
 * </ul>
 *
 * @author
 * Crisologo, Lim Un
 * @version 2.1
 */
public class PropertyManager
{
    /** The list storing all {@link Property} objects managed by the system. */
    private List<Property> properties;

    // CONSTRUCTOR

    /**
     * Constructs a new {@code PropertyManager} with an empty list of properties.
     */
    public PropertyManager()
    {
        this.properties = new ArrayList<>();
    }


    // CORE METHODS

    /**
     * Adds a new property to the system if its name is unique.
     *
     * @param name       The name of the property to add
     * @param basePrice  The base price per night for the property
     * @return {@code true} if the property was successfully added;
     *         {@code false} if a property with the same name already exists
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
     * Removes an existing property from the system, provided it has no active reservations.
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
     * Renames an existing property if the new name is unique.
     *
     * @param oldName The current name of the property
     * @param newName The desired new name
     * @return {@code true} if the property was successfully renamed;
     *         {@code false} if the new name already exists or the property cannot be found
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
     * Attempts to book a reservation for the specified property.
     *
     * @param name       The name of the property to book
     * @param guestName  The name of the guest making the reservation
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


    // HELPER METHODS

    /**
     * Retrieves a {@link Property} object by its name.
     *
     * @param name The name of the property to search for
     * @return The {@link Property} object if found, or {@code null} if no match exists
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
     * Returns a list of all properties currently managed by the system.
     * <p>
     * The returned list directly references the internal list,
     * so modifications will affect the original data.
     *
     * @return A list of all managed {@link Property} objects
     */
    public List<Property> listProperties()
    {
        return properties;
    }

    /**
     * Checks whether a given property name is unique across all managed properties.
     *
     * @param name The name to verify
     * @return {@code true} if the name is unique; {@code false} otherwise
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
