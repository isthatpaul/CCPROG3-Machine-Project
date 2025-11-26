import java.util.List;

/**
 * Repository abstraction for managing properties. Implementations (e.g., PropertyManager)
 * should provide persistence and lookup operations.
 */
public interface PropertyRepository {
    /**
     * Adds a new property to the repository.
     * 
     * @param name       the name of the property
     * @param basePrice  the base nightly price
     * @param type       the type of property (e.g., 0=Apartment, 1=House)
     * @return true if added successfully, false if a property with the same name exists
     */
    boolean addProperty(String name, double basePrice, int type);

    /**
     * Retrieves a property by its name.
     * 
     * @param name the name of the property
     * @return the Property object if found, null otherwise
     */
    Property getProperty(String name);

    /**
     * Lists all properties in the repository.
     * 
     * @return list of all properties
     */
    List<Property> listProperties();

    /**
     * Removes a property by its name.
     * 
     * @param name the name of the property to remove
     * @return true if removed successfully, false if not found
     */
    boolean removeProperty(String name);

    /**
     * Renames an existing property.
     * 
     * @param oldName the current name of the property
     * @param newName the new name for the property
     * @return true if renamed successfully, false if oldName not found or newName already exists
     */
    boolean renameProperty(String oldName, String newName);

    /**
     * Checks if a property name is unique in the repository.
     * 
     * @param name the property name to check
     * @return true if the name is unique, false otherwise
     */
    boolean isUniqueName(String name);
}