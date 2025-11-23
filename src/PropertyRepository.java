import java.util.List;

/**
 * Repository abstraction for managing properties. Implementations (e.g., PropertyManager)
 * should provide persistence and lookup operations.
 */
public interface PropertyRepository {
    boolean addProperty(String name, double basePrice, int type);
    Property getProperty(String name);
    List<Property> listProperties();
    boolean removeProperty(String name);
    boolean renameProperty(String oldName, String newName);
    boolean isUniqueName(String name);
}