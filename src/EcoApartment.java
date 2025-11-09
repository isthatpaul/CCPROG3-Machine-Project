public class EcoApartment extends Property {
    public EcoApartment(String name, double basePrice) {
        super(name, basePrice);
    }
    @Override
    protected double getRateMultiplier() {
        return 1.0;
    }
    @Override
    public String getPropertyType() {
        return "Eco-Apartment";
    }
}