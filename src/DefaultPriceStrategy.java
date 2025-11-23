import java.util.ArrayList;
import java.util.List;

/**
 * Default PriceStrategy implementation.
 * Computes nightly rate as basePrice * propertyMultiplier * dateEnvModifier.
 */
public class DefaultPriceStrategy implements PriceStrategy {

    @Override
    public List<Double> calculateNightlyRates(Property property, int checkIn, int checkOut) {
        List<Double> rates = new ArrayList<>();
        for (int d = checkIn; d < checkOut; d++) {
            rates.add(property.calculateFinalDailyRate(d));
        }
        return rates;
    }
}