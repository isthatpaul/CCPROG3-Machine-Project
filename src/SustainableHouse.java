public class SustainableHouse extends Property {
    public SustainableHouse(String name, double basePrice) {
        super(name, basePrice);
    }
    @Override
    protected double getRateMultiplier() {
        return 1.20;
    }
    @Override
    public String getPropertyType() {
        return "Sustainable House";
    }
}