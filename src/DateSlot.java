/**
 * The {@code DateSlot} class represents a single day (slot) in the
 * 30-day calendar of a {@link Property} in the Green Property system.
 * <p>
 * Each slot records:
 * <ul>
 *     <li>The day number (1–30)</li>
 *     <li>The base price per night</li>
 *     <li>Whether the day is booked or available</li>
 *     <li>The {@link Reservation} that booked it (if any)</li>
 * </ul>
 *
 * @author Crisologo, Lim Un
 * @version 2.0
 */
public class DateSlot
{
    /** The numerical day in the 30-day month (1–30). */
    private final int dayNumber;

    /** The price per night for this day. */
    private double pricePerNight;

    /** Indicates whether the day is already booked. */
    private boolean booked;

    /** The reservation associated with this day, if booked. */
    private Reservation reservation;

    // CONSTRUCTOR

    /**
     * Constructs a {@code DateSlot} for a specific day and assigns the base price.
     *
     * @param dayNumber  The day number (1–30)
     * @param basePrice  The default price per night
     */
    public DateSlot(int dayNumber, double basePrice)
    {
        this.dayNumber = dayNumber;
        this.pricePerNight = basePrice;
        this.booked = false;
        this.reservation = null;
    }

    // GETTERS AND SETTERS

    /**
     * Returns the day number for this slot.
     *
     * @return The day number (1–30)
     */
    public int getDayNumber()
    {
        return dayNumber;
    }

    /**
     * Returns the price per night for this slot.
     *
     * @return The nightly price
     */
    public double getPricePerNight()
    {
        return pricePerNight;
    }

    /**
     * Updates the price per night for this slot.
     *
     * @param pricePerNight The new nightly price
     */
    public void setPricePerNight(double pricePerNight)
    {
        this.pricePerNight = pricePerNight;
    }

    /**
     * Returns {@code true} if the day is already booked.
     *
     * @return Whether this day is booked
     */
    public boolean isBooked()
    {
        return booked;
    }

    /**
     * Returns the {@link Reservation} that has booked this day.
     * If the day is not booked, this method returns {@code null}.
     *
     * @return The reservation that booked this day, or null if available
     */
    public Reservation getReservation()
    {
        return reservation;
    }

    /**
     * Returns {@code true} if the day is currently available for booking.
     *
     * @return True if not booked, false if already booked
     */
    public boolean isAvailable()
    {
        return !booked;
    }

    // CORE FUNCTIONALITY

    /**
     * Attempts to book this day for a given reservation.
     * <p>
     * If the day is already booked, the booking will fail.
     *
     * @param res The reservation attempting to book the day
     * @return {@code true} if successfully booked; {@code false} if already booked
     */
    public boolean book(Reservation res)
    {
        if (booked) {
            return false;
        } else {
            booked = true;
            reservation = res;
            return true;
        }
    }

    /**
     * Cancels any existing booking on this day.
     * After calling this method, the day becomes available again.
     */
    public void cancelBooking()
    {
        booked = false;
        reservation = null;
    }

    // DISPLAY METHOD

    /**
     * Returns a readable string showing the status of this date slot.
     *
     * @return A string describing the booking status of this date slot
     */
    @Override
    public String toString()
    {
        if (booked && reservation != null)
        {
            return "Day " + dayNumber + " - BOOKED (" + reservation.getGuestName() + ")";
        } else {
            return "Day " + dayNumber + " - AVAILABLE";
        }
    }
}
