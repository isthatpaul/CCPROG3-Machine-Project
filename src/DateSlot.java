/**
 * Represents a single day in the 30-day calendar.
 * Tracks the day number, booking status, and environmental impact modifier.
 * Pricing is computed externally (Property / PriceStrategy) using this slot's modifier.
 *
 * @author Crisologo, Lim Un
 * @version 5.0
 */
public class DateSlot {

    /** The day number in the 30-day month (1–30). */
    private final int dayNumber;

    /** The environmental impact multiplier (0.80 to 1.20). */
    private double envImpactModifier;

    /** Indicates whether this day is currently booked. */
    private boolean booked;

    /** The reservation that has booked this day, if any. */
    private Reservation reservation;

    /**
     * Creates a new DateSlot for the given day number.
     *
     * @param dayNumber the day number (1–30)
     */
    public DateSlot(int dayNumber) {
        this.dayNumber = dayNumber;
        this.envImpactModifier = 1.0; // Default to 1.0 (100% standard rate)
        this.booked = false;
        this.reservation = null;
    }

    /**
     * Returns the environmental impact modifier for this slot.
     *
     * @return modifier value in the range [0.8, 1.2]
     */
    public double getEnvImpactModifier() {
        return envImpactModifier;
    }

    /**
     * Sets the environmental impact modifier for this slot.
     * Value will be clamped to the allowed range [0.8, 1.2].
     *
     * @param modifier new modifier to set (0.8 - 1.2)
     */
    public void setEnvImpactModifier(double modifier) {
        this.envImpactModifier = Math.max(0.8, Math.min(1.2, modifier));
    }

    /**
     * Returns the day number (1..30).
     *
     * @return day number
     */
    public int getDayNumber() { return dayNumber; }

    /**
     * Returns true if this slot is currently booked.
     *
     * @return true when booked, false otherwise
     */
    public boolean isBooked() { return booked; }

    /**
     * Returns the reservation that booked this slot, or null if available.
     *
     * @return Reservation or null
     */
    public Reservation getReservation() { return reservation; }

    /**
     * Returns whether this slot is available for booking.
     *
     * @return true if available
     */
    public boolean isAvailable() { return !booked; }

    /**
     * Books this slot for the provided reservation.
     *
     * @param res the reservation to assign
     * @return true if booking succeeded; false if already booked
     */
    public boolean book(Reservation res) {
        if (booked) { return false; }
        booked = true;
        reservation = res;
        return true;
    }

    /**
     * Cancels any existing booking for this slot, making it available.
     */
    public void cancelBooking() {
        booked = false;
        reservation = null;
    }

    @Override
    public String toString() {
        if (booked && reservation != null) {
            return "Day " + dayNumber + " - BOOKED (" + reservation.getGuestName() + ")";
        }
        return "Day " + dayNumber + " - AVAILABLE";
    }
}