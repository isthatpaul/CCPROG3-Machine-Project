/**
 * Guest loyalty tiers used to apply discounts as part of the bonus feature.
 * Each tier supplies a discount fraction applied to nightly rates.
 *
 * REGULAR = 0%
 * SILVER  = 5%
 * GOLD    = 10%
 * PLATINUM= 15%
 */
public enum GuestTier {
    REGULAR(0.0),
    SILVER(0.05),
    GOLD(0.10),
    PLATINUM(0.15);

    private final double discount;

    GuestTier(double discount) {
        this.discount = discount;
    }

    /**
     * Returns the discount fraction for this tier (e.g., 0.05 for 5%).
     *
     * @return discount fraction
     */
    public double getDiscount() {
        return discount;
    }

    @Override
    public String toString() {
        switch(this) {
            case SILVER: return "Silver (5%)";
            case GOLD: return "Gold (10%)";
            case PLATINUM: return "Platinum (15%)";
            default: return "Regular (0%)";
        }
    }
}