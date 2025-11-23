import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for different property types.
 * Holds the collection of DateSlot instances (30 days) and property reservations.
 *
 * Subclasses provide the type-specific multiplier by implementing getRateMultiplier().
 */
public abstract class Property implements PropertyInternalAccessor {
    private String propertyName;
    private double basePrice;
    private final List<DateSlot> dates;
    private final List<Reservation> reservations;

    /**
     * Constructs a Property with the given name and base price.
     *
     * @param name property name
     * @param basePrice base price per night
     */
    public Property(String name, double basePrice) {
        this.propertyName = name;
        this.basePrice = basePrice;
        this.dates = new ArrayList<>();
        this.reservations = new ArrayList<>();

        for (int day = 1; day <= 30; day++) {
            dates.add(new DateSlot(day));
        }
    }

    /**
     * Returns the rate multiplier specific to the concrete property type
     * (e.g., 1.0 for EcoApartment, 1.20 for SustainableHouse).
     *
     * @return type multiplier
     */
    protected abstract double getRateMultiplier();

    /**
     * Returns the display name of the property type.
     *
     * @return property type name
     */
    public abstract String getPropertyType();

    /**
     * Calculates the final nightly rate for a specific day:
     * final = basePrice * typeMultiplier * date.envImpactModifier
     *
     * @param day day number (1..30)
     * @return final price per night, or 0.0 for invalid day
     */
    public double calculateFinalDailyRate(int day) {
        if (day < 1 || day > 30) {
            return 0.0;
        }
        DateSlot slot = dates.get(day - 1);
        return basePrice * getRateMultiplier() * slot.getEnvImpactModifier();
    }

    /** @return property name */
    public String getPropertyName() {
        return propertyName;
    }

    /** @return base price */
    public double getBasePrice() {
        return basePrice;
    }

    /** @return direct access to DateSlot list (owned by property) */
    public List<DateSlot> getDates() {
        return dates;
    }

    /**
     * Returns a defensive copy of the reservations list to avoid external mutation.
     *
     * @return list of reservations
     */
    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    /**
     * Rename the property.
     *
     * @param newName new name to set
     */
    public void setPropertyName(String newName) {
        this.propertyName = newName;
    }

    /**
     * Remove a reservation and cancel booked DateSlots.
     *
     * @param reservation reservation to remove
     * @return true if removed, false if reservation not found
     */
    public boolean removeReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            return false;
        }

        for (int d = reservation.getCheckInDay(); d < reservation.getCheckOutDay(); d++) {
            dates.get(d - 1).cancelBooking();
        }

        reservations.remove(reservation);
        return true;
    }

    /** @return true if property has no active reservations */
    public boolean canBeRemoved() {
        return reservations.isEmpty();
    }

    /** @return total earnings from all reservations */
    public double getTotalEarnings() {
        double total = 0.0;
        for (Reservation r : reservations) {
            total += r.getTotalPrice();
        }
        return total;
    }

    /** @return number of available dates (not booked) */
    public int countAvailableDates() {
        int count = 0;
        for (DateSlot date : dates) {
            if (date.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count booked dates in an inclusive range. Bounds are clamped to [1,30].
     *
     * @param startDay range start (inclusive)
     * @param endDay range end (inclusive)
     * @return number of booked days in range
     */
    public int countBookedDatesInRange(int startDay, int endDay) {
        if (startDay < 1) startDay = 1;
        if (endDay > 30) endDay = 30;
        if (startDay > endDay) return 0;
        int count = 0;
        for (int d = startDay; d <= endDay; d++) {
            if (!dates.get(d - 1).isAvailable()) count++;
        }
        return count;
    }

    /**
     * Count available dates in an inclusive range. Bounds are clamped to [1,30].
     *
     * @param startDay range start (inclusive)
     * @param endDay range end (inclusive)
     * @return number of available days in range
     */
    public int countAvailableDatesInRange(int startDay, int endDay) {
        if (startDay < 1) startDay = 1;
        if (endDay > 30) endDay = 30;
        if (startDay > endDay) return 0;
        int count = 0;
        for (int d = startDay; d <= endDay; d++) {
            if (dates.get(d - 1).isAvailable()) count++;
        }
        return count;
    }

    /**
     * Returns a color representing the environmental impact level for UI rendering.
     *
     * @param day day number (1..30)
     * @return Color instance representing impact
     */
    public Color getEnvironmentalImpactColor(int day) {
        if (day < 1 || day > 30) return Color.WHITE;

        DateSlot slot = dates.get(day - 1);
        double modifier = slot.getEnvImpactModifier();

        final double EPS = 1e-9;
        if (modifier >= 0.80 && modifier <= 0.89) {
            return Color.GREEN;
        } else if (Math.abs(modifier - 1.0) < EPS) {
            return Color.WHITE;
        } else if (modifier >= 1.01 && modifier <= 1.20) {
            return Color.YELLOW;
        } else {
            return Color.WHITE;
        }
    }

    /**
     * Set the environmental impact modifier for a single date.
     *
     * @param day day number
     * @param modifier modifier in [0.8,1.2]
     * @return true if set successfully
     */
    public boolean setEnvironmentalImpact(int day, double modifier) {
        if (day < 1 || day > 30) return false;
        if (modifier < 0.8 || modifier > 1.2) return false;

        DateSlot slot = dates.get(day - 1);
        slot.setEnvImpactModifier(modifier);
        return true;
    }

    /**
     * Set the environmental impact modifier for a range of dates.
     *
     * @param startDay start day (inclusive)
     * @param endDay end day (inclusive)
     * @param modifier modifier in [0.8,1.2]
     * @return true if updated successfully
     */
    public boolean setEnvironmentalImpactRange(int startDay, int endDay, double modifier) {
        if (startDay < 1 || endDay > 30 || startDay > endDay) return false;
        if (modifier < 0.8 || modifier > 1.2) return false;

        for (int day = startDay; day <= endDay; day++) {
            DateSlot slot = dates.get(day - 1);
            slot.setEnvImpactModifier(modifier);
        }
        return true;
    }

    /**
     * Reset a date to the standard environmental modifier (1.0).
     *
     * @param day day to reset
     * @return true if successful
     */
    public boolean resetEnvironmentalImpact(int day) {
        if (day < 1 || day > 30) return false;

        dates.get(day - 1).setEnvImpactModifier(1.0);
        return true;
    }

    /**
     * Controlled internal method used by BookingServiceImpl to persist reservations
     * and book the corresponding date slots. Keeps the mutation of internal lists inside Property.
     *
     * @param reservation reservation to add
     */
    @Override
    public void addReservationDirect(Reservation reservation) {
        for (int d = reservation.getCheckInDay(); d < reservation.getCheckOutDay(); d++) {
            dates.get(d - 1).book(reservation);
        }
        reservations.add(reservation);
    }

    /**
     * Update the base price. Only allowed when there are no active reservations and price >= 100.
     *
     * @param newPrice new base price
     * @return true if update succeeded
     */
    public boolean updateBasePrice(double newPrice) {
        if (newPrice < 100.0) return false;
        if (!reservations.isEmpty()) return false;
        this.basePrice = newPrice;
        return true;
    }

    /**
     * Checks if a date range is completely available for booking.
     *
     * @param checkIn check-in day (inclusive)
     * @param checkOut check-out day (exclusive)
     * @return true if all dates in range are available
     */
    public boolean isDateRangeAvailable(int checkIn, int checkOut) {
        if (checkIn < 1 || checkOut > 31 || checkIn >= checkOut) return false;
        
        for (int d = checkIn; d < checkOut; d++) {
            if (!dates.get(d - 1).isAvailable()) {
                return false;
            }
        }
        return true;
    }
}