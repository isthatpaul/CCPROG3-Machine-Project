/**
 * EcoApartment class representing an eco-friendly apartment property.
 * Extends the Property class and provides specific implementations
 * for the rate multiplier and property type.
 */
public class EcoApartment extends Property {

    /**
     * Constructs an EcoApartment with the given name and base price.
     * 
     * @param name
     * @param basePrice
     */
    public EcoApartment(String name, double basePrice) {
        super(name, basePrice);
    }

    /**
     * Returns the rate multiplier specific to EcoApartment.
     * 
     * @return the rate multiplier
     */
    @Override
    protected double getRateMultiplier() {
        return 1.0;
    }

    /**
     * Returns the property type as a string.
     * 
     * @return the property type
     */
    @Override
    public String getPropertyType() {
        return "Eco-Apartment";
    }
}