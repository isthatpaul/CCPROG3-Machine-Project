import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of BookingService.
 * Validates booking rules, uses PriceStrategy for pricing, and persists reservations via PropertyInternalAccessor.
 */
public class BookingServiceImpl implements BookingService {
    private final PropertyRepository repository;
    private final PriceStrategy priceStrategy;

    /**
     * Constructs the booking service with the given repository and pricing strategy.
     *
     * @param repository the property repository
     * @param priceStrategy the pricing strategy to compute nightly rates
     */
    public BookingServiceImpl(PropertyRepository repository, PriceStrategy priceStrategy) {
        this.repository = repository;
        this.priceStrategy = priceStrategy;
    }

    /**
     * Attempts to book the property with the provided details.
     *
     * @return true when booking succeeded; false otherwise (invalid bounds or conflict)
     */
    @Override
    public boolean book(String propertyName, String guestName, GuestTier tier, int checkIn, int checkOut) {
        Property p = repository.getProperty(propertyName);
        if (p == null) return false;

        // Validate date bounds
        if (checkOut == 1 || checkIn == 30 || checkIn < 1 || checkOut > 30 || checkIn >= checkOut) {
            return false;
        }

        // Check for date conflicts
        for (int d = checkIn; d < checkOut; d++) {
            if (!p.getDates().get(d - 1).isAvailable()) {
                return false;
            }
        }

        // Calculate rates and create reservation
        List<Double> nightlyRates = priceStrategy.calculateNightlyRates(p, checkIn, checkOut);
        Reservation newRes = new Reservation(guestName, tier, checkIn, checkOut, nightlyRates);

        // Use PropertyInternalAccessor to persist the reservation
        if (p instanceof PropertyInternalAccessor) {
            ((PropertyInternalAccessor) p).addReservationDirect(newRes);
            return true;
        } else {
            // Fallback: This should not happen if all Properties implement PropertyInternalAccessor
            return false;
        }
    }

    /**
     * Removes the specified reservation from the property.
     * 
     * @param propertyName the name of the property
     * @param reservation the reservation to remove
     * @return true if removal was successful; false otherwise
     */
    @Override
    public boolean removeReservation(String propertyName, Reservation reservation) {
        Property p = repository.getProperty(propertyName);
        if (p == null) return false;
        return p.removeReservation(reservation);
    }

    /**
     * Retrieves all reservations for the specified property.
     * 
     * @param propertyName the name of the property
     * @return list of reservations
     */
    @Override
    public List<Reservation> getReservations(String propertyName) {
        Property p = repository.getProperty(propertyName);
        if (p == null) return new ArrayList<>();
        return p.getReservations();
    }
}