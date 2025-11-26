import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of PropertyRepository.
 * Manages the in-memory collection of Property objects.
 */
public class PropertyManager implements PropertyRepository {
    private List<Property> properties;

    /**
     * Constructs an empty PropertyManager.
     */
    public PropertyManager() {
        this.properties = new ArrayList<>();
    }

    /**
     * Adds a new property of the specified type if the name is unique.
     * 
     * @param name name of the property
     * @param basePrice base price per night
     * @param type type of property (1=EcoApartment, 2=SustainableHouse, 3=GreenResort, 4=EcoGlamping)
     * @return true if added successfully, false otherwise
     */
    @Override
    public boolean addProperty(String name, double basePrice, int type) {
        if (!isUniqueName(name)) {
            return false;
        }

        Property newProperty;
        switch (type) {
            case 1:
                newProperty = new EcoApartment(name, basePrice);
                break;
            case 2:
                newProperty = new SustainableHouse(name, basePrice);
                break;
            case 3:
                newProperty = new GreenResort(name, basePrice);
                break;
            case 4:
                newProperty = new EcoGlamping(name, basePrice);
                break;
            default:
                return false;
        }

        properties.add(newProperty);
        return true;
    }

    /**
     * Retrieves a property by its name.
     * 
     * @param name name of the property
     * @return the Property object, or null if not found
     */
    @Override
    public Property getProperty(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Lists all properties.
     * 
     * @return list of all Property objects
     */
    @Override
    public List<Property> listProperties() {
        return new ArrayList<>(properties);
    }

    /**
     * Removes a property by its name if it can be removed.
     * 
     * @param name name of the property
     * @return true if removed successfully, false otherwise
     */
    @Override
    public boolean removeProperty(String name) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                if (!p.canBeRemoved()) { return false; }
                properties.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Renames a property if the new name is unique.
     * 
     * @param oldName the current name of the property
     * @param newName the new name to set
     * @return true if renamed successfully, false otherwise
     */
    @Override
    public boolean renameProperty(String oldName, String newName) {
        if (!isUniqueName(newName)) { return false; }
        Property p = getProperty(oldName);
        if (p != null) {
            p.setPropertyName(newName);
            return true;
        }
        return false;
    }

    /**
     * Checks if a property name is unique (case-insensitive).
     * 
     * @param name name to check
     * @return true if unique, false otherwise
     */
    @Override
    public boolean isUniqueName(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }
}