/**
 * The DateSlot class represents a single day in the 30-day calendar
 * of a Property in the Green Property system. It tracks the day number,
 * nightly rate, and whether the day is booked or available.
 * <p>
 * Each slot may also reference a Reservation that has booked it.
 * </p>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     2.0
 */
public class DateSlot {

    /** The day number in the 30-day month (1–30). */
    private final int dayNumber;

    /** The price per night for this day. */
    private double pricePerNight;

    /** Indicates whether this day is currently booked. */
    private boolean booked;

    /** The reservation that has booked this day, if any. */
    private Reservation reservation;

    /**
     * Creates a new DateSlot for the given day and assigns its base price.
     *
     * @param dayNumber the day number (1–30)
     * @param basePrice the default nightly price
     */
    public DateSlot(int dayNumber, double basePrice) {
        this.dayNumber = dayNumber;
        this.pricePerNight = basePrice;
        this.booked = false;
        this.reservation = null;
    }

    /**
     * Gets the day number for this slot.
     *
     * @return the day number (1–30)
     */
    public int getDayNumber() {
        return dayNumber;
    }

    /**
     * Gets the nightly price for this slot.
     *
     * @return the price per night
     */
    public double getPricePerNight() {
        return pricePerNight;
    }

    /**
     * Sets a new nightly price for this slot.
     *
     * @param pricePerNight the new nightly price
     */
    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    /**
     * Checks if this day is booked.
     *
     * @return true if booked, false otherwise
     */
    public boolean isBooked() {
        return booked;
    }

    /**
     * Gets the reservation that booked this day.
     * Returns null if the day is available.
     *
     * @return the reservation that booked this day, or null if available
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Checks if this day is available for booking.
     *
     * @return true if available, false if already booked
     */
    public boolean isAvailable() {
        return !booked;
    }

    /**
     * Books this day for the given reservation.
     * If the day is already booked, the booking will fail.
     *
     * @param res the reservation to assign to this day
     * @return true if successfully booked, false if already booked
     */
    public boolean book(Reservation res) {
        if (booked) {
            return false;
        }
        booked = true;
        reservation = res;
        return true;
    }

    /**
     * Cancels any existing booking for this day, making it available again.
     */
    public void cancelBooking() {
        booked = false;
        reservation = null;
    }

    /**
     * Returns a string summary of this date slot’s status.
     *
     * @return a formatted string showing whether the day is booked or available
     */
    @Override
    public String toString() {
        if (booked && reservation != null) {
            return "Day " + dayNumber + " - BOOKED (" + reservation.getGuestName() + ")";
        }
        return "Day " + dayNumber + " - AVAILABLE";
    }
}
