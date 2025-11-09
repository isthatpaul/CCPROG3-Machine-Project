public class GreenResort extends Property {
    public GreenResort(String name, double basePrice) {
        super(name, basePrice);
    }
    @Override
    protected double getRateMultiplier() {
        return 1.35;
    }
    @Override
    public String getPropertyType() {
        return "Green Resort";
    }
}