import java.util.List;

/**
 * Strategy interface for calculating nightly rates.
 * Implementations allow different pricing policies (OCP).
 */
public interface PriceStrategy {
    /**
     * Calculate nightly rates (before guest-tier discount) for a property
     * between checkIn (inclusive) and checkOut (exclusive).
     *
     * @param property property instance
     * @param checkIn check-in day (inclusive)
     * @param checkOut check-out day (exclusive)
     * @return list of nightly rates (one per night)
     */
    List<Double> calculateNightlyRates(Property property, int checkIn, int checkOut);
}