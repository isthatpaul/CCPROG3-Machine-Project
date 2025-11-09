public class EcoGlamping extends Property {
    public EcoGlamping(String name, double basePrice) {
        super(name, basePrice);
    }
    @Override
    protected double getRateMultiplier() {
        return 1.50;
    }
    @Override
    public String getPropertyType() {
        return "Eco-Glamping";
    }
}