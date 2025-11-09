/**
 * The DateSlot class represents a single day in the 30-day calendar.
 * It tracks the day number, booking status, and environmental impact modifier.
 * The final price must be calculated externally by the Property object.
 * * @author Crisologo, Lim Un
 * @version 3.0 (Updated for MCO2)
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
     * Creates a new DateSlot for the given day.
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
     * Gets the environmental impact modifier for this slot.
     *
     * @return the modifier (0.8 to 1.2)
     */
    public double getEnvImpactModifier() {
        return envImpactModifier;
    }

    /**
     * Sets a new environmental impact modifier for this slot.
     *
     * @param modifier the new modifier (will be capped between 0.8 and 1.2)
     */
    public void setEnvImpactModifier(double modifier) {
        // Enforce the 0.8 to 1.2 range
        this.envImpactModifier = Math.max(0.8, Math.min(1.2, modifier));
    }

    // NOTE: Removed setPricePerNight and getPricePerNight as price is now dynamic.
    // ... (rest of the MCO1 methods: getDayNumber, isBooked, getReservation, isAvailable, book, cancelBooking, toString)

    /** Gets the day number for this slot. */
    public int getDayNumber() { return dayNumber; }

    /** Checks if this day is booked. */
    public boolean isBooked() { return booked; }

    /** Gets the reservation that booked this day. */
    public Reservation getReservation() { return reservation; }

    /** Checks if this day is available for booking. */
    public boolean isAvailable() { return !booked; }

    /** Books this day for the given reservation. */
    public boolean book(Reservation res) {
        if (booked) { return false; }
        booked = true;
        reservation = res;
        return true;
    }

    /** Cancels any existing booking for this day, making it available again. */
    public void cancelBooking() {
        booked = false;
        reservation = null;
    }

    @Override
    public String toString() {
        // The display logic should be handled by the GUI, but keeping the original for model integrity
        if (booked && reservation != null) {
            return "Day " + dayNumber + " - BOOKED (" + reservation.getGuestName() + ")";
        }
        return "Day " + dayNumber + " - AVAILABLE";
    }
}