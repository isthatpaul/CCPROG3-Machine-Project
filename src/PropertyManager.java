import java.util.ArrayList;
import java.util.List;

public class PropertyManager
{
    private List<Property> properties;

    public PropertyManager()
    {
        this.properties = new ArrayList<>();
    }

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

    public boolean removeProperty(String name)
    {
        for (int i = 0; i < properties.size(); i++)
        {
            Property p = properties.get(i);
            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                properties.remove(i);
                System.out.println("Property \"" + name + "\" removed.");
                return true;
            }
        }
        System.out.println("No property found with the name \"" + name + "\".");
        return false;
    }

    public Property getProperty(String name)
    {
        for (int i = 0; i < properties.size(); i++)
        {
            Property p = properties.get(i);
            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                return p;
            }
        }
        return null;
    }


    public List<Property> listProperties()
    {
        return properties;
    }

    public boolean isUniqueName(String name)
    {
        for (int i = 0; i < properties.size(); i++)
        {
            Property p = properties.get(i);

            if (p.getPropertyName().equalsIgnoreCase(name))
            {
                return false;
            }
        }
        return true;
    }
}
