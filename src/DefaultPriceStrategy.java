import java.util.ArrayList;
import java.util.List;

/**
 * Default PriceStrategy implementation.
 * Computes nightly rate as basePrice * propertyMultiplier * dateEnvModifier.
 */
public class DefaultPriceStrategy implements PriceStrategy {

    /**
     * Calculates nightly rates for the given property and date range.
     * 
     * @param property the property being booked
     * @param checkIn the check-in day number (1–30)
     * @param checkOut the check-out day number (1–30)
     * @return list of nightly rates for each day in the range
     */
    @Override
    public List<Double> calculateNightlyRates(Property property, int checkIn, int checkOut) {
        List<Double> rates = new ArrayList<>();
        for (int d = checkIn; d < checkOut; d++) {
            rates.add(property.calculateFinalDailyRate(d));
        }
        return rates;
    }
}