import java.util.ArrayList;
import java.util.List;

/**
 * The PropertyManager class manages all properties in the Green Property System.
 * It now uses polymorphism to create different property types.
 * * @author Crisologo, Lim Un
 * @version 3.0 (Updated for MCO2)
 */
public class PropertyManager {
    private List<Property> properties;

    public PropertyManager() {
        this.properties = new ArrayList<>();
    }

    /**
     * Adds a new property to the system, instantiating the correct subclass
     * based on the provided type.
     *
     * @param name the name of the property to add
     * @param basePrice the base price per night for the property
     * @param type the type of property (1: Eco-Apartment, 2: Sustainable House, etc.)
     * @return true if the property was added successfully, false if a duplicate name exists or type is invalid
     */
    public boolean addProperty(String name, double basePrice, int type) {
        if (!isUniqueName(name)) {
            // In GUI, this should return false and an error dialog will show.
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
                // Invalid type selected
                return false;
        }

        properties.add(newProperty);
        return true;
    }

    // --- EXISTING MCO1 METHODS ---

    public Property getProperty(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<Property> listProperties() {
        return properties;
    }

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

    public boolean renameProperty(String oldName, String newName) {
        if (!isUniqueName(newName)) { return false; }
        Property p = getProperty(oldName);
        if (p != null) {
            p.setPropertyName(newName);
            return true;
        }
        return false;
    }

    public boolean bookProperty(String name, String guestName, int checkIn, int checkOut) {
        Property p = getProperty(name);
        if (p == null) { return false; }
        return p.addReservation(guestName, checkIn, checkOut);
    }

    public boolean isUniqueName(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }
}