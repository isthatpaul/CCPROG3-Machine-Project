import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of PropertyRepository.
 * Manages the in-memory collection of Property objects.
 */
public class PropertyManager implements PropertyRepository {
    private List<Property> properties;

    public PropertyManager() {
        this.properties = new ArrayList<>();
    }

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

    @Override
    public Property getProperty(String name) {
        for (Property p : properties) {
            if (p.getPropertyName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Property> listProperties() {
        return new ArrayList<>(properties);
    }

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