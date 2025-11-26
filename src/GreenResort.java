/**
 * GreenResort class representing a green resort property.
 * Extends the Property class and provides specific implementations
 * for the rate multiplier and property type.
 */
public class GreenResort extends Property {

    /**
     * Constructs a GreenResort with the given name and base price.
     * 
     * @param name
     * @param basePrice
     */
    public GreenResort(String name, double basePrice) {
        super(name, basePrice);
    }

    /**
     * Returns the rate multiplier specific to GreenResort.
     * 
     * @return the rate multiplier
     */
    @Override
    protected double getRateMultiplier() {
        return 1.35;
    }

    /**
     * Returns the property type as a string.
     * 
     * @return the property type
     */
    @Override
    public String getPropertyType() {
        return "Green Resort";
    }
}