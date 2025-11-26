/**
 * EcoGlamping class representing an eco-friendly glamping property.
 * Extends the Property class and provides specific implementations
 * for the rate multiplier and property type.
 */
public class EcoGlamping extends Property {

    /**
     * Constructs an EcoGlamping with the given name and base price.
     * 
     * @param name
     * @param basePrice
     */
    public EcoGlamping(String name, double basePrice) {
        super(name, basePrice);
    }

    /**
     * Returns the rate multiplier specific to EcoGlamping.
     * 
     * @return the rate multiplier
     */
    @Override
    protected double getRateMultiplier() {
        return 1.50;
    }

    /**
     * Returns the property type as a string.
     * 
     * @return the property type
     */
    @Override
    public String getPropertyType() {
        return "Eco-Glamping";
    }
}