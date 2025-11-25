/**
 * Represents a Sustainable House property with specific rate multiplier.
 * Extends the Property class and provides implementations
 * for the rate multiplier and property type.
 */
public class SustainableHouse extends Property {

    /**
     * Constructs a SustainableHouse with the given name and base price.
     * 
     * @param name property name
     * @param basePrice base price per night
     */
    public SustainableHouse(String name, double basePrice) {
        super(name, basePrice);
    }

    /**
     * Returns the rate multiplier specific to SustainableHouse.
     * 
     * @return the rate multiplier
     */
    @Override
    protected double getRateMultiplier() {
        return 1.20;
    }

    /**
     * Returns the property type as a string.
     * 
     * @return the property type
     */
    @Override
    public String getPropertyType() {
        return "Sustainable House";
    }
}