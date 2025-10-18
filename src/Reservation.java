/**
 * The {@code Reservation} class represents a confirmed booking
 * for a guest in the Green Property system.
 * <p>
 * Each reservation records:
 * <ul>
 *     <li>The guest's name</li>
 *     <li>Check-in and check-out days (1–30)</li>
 *     <li>The total computed price</li>
 *     <li>A nightly price breakdown for a 30-day calendar</li>
 * </ul>
 *
 * @author Crisologo, Lim Un
 * @version 2.0
 */
public class Reservation
{
    /** The name of the guest who made the reservation. */
    private final String guestName;

    /** The day number on which the guest checks in (1–30). */
    private final int checkInDay;

    /** The day number on which the guest checks out (1–30). */
    private final int checkOutDay;

    /** The total computed price for the stay. */
    private double totalPrice;

    /** The nightly price breakdown across 30 days (index 0 = Day 1). */
    private double[] priceBreakdown;

    // CONSTRUCTOR

    /**
     * Constructs a {@code Reservation} for a given guest and date range.
     * Automatically calculates the total price and nightly breakdown
     * using the provided base price.
     *
     * @param guestName  The guest’s name
     * @param checkIn    The check-in day (1–30)
     * @param checkOut   The check-out day (1–30)
     * @param basePrice  The nightly base price for the property
     */
    public Reservation(String guestName, int checkIn, int checkOut, double basePrice)
    {
        this.guestName = guestName;
        this.checkInDay = checkIn;
        this.checkOutDay = checkOut;
        this.priceBreakdown = new double[30];
        calculatePrices(basePrice);
    }

    // METHODS

    /**
     * Fills the 30-day price breakdown array and computes the total price.
     *
     * @param basePrice The base nightly rate of the property
     */
    private void calculatePrices(double basePrice)
    {
        this.totalPrice = 0.0;

        // Reset all days to zero
        for (int i = 0; i < priceBreakdown.length; i++)
        {
            priceBreakdown[i] = 0.0;
        }

        // Compute cost for each night between check-in and check-out
        int duration = checkOutDay - checkInDay;

        for (int i = 0; i < duration; i++)
        {
            int currentDay = checkInDay + i;

            // Ensure within 30-day limit
            if (currentDay <= 30)
            {
                priceBreakdown[currentDay - 1] = basePrice;
                totalPrice += basePrice;
            }
        }
    }

    // GETTERS

    /**
     * Returns the guest’s name.
     *
     * @return The guest’s name
     */
    public String getGuestName()
    {
        return guestName;
    }

    /**
     * Returns the check-in day.
     *
     * @return The check-in day number
     */
    public int getCheckInDay()
    {
        return checkInDay;
    }

    /**
     * Returns the check-out day.
     *
     * @return The check-out day number
     */
    public int getCheckOutDay()
    {
        return checkOutDay;
    }

    /**
     * Returns the total price of this reservation.
     *
     * @return The total computed price
     */
    public double getTotalPrice()
    {
        return totalPrice;
    }

    /**
     * Returns the nightly price for a specific day.
     * If the day is outside 1–30, returns 0.0.
     *
     * @param day The day number (1–30)
     * @return The nightly charge for that day, or 0.0 if invalid
     */
    public double getPriceBreakdown(int day)
    {
        if (day < 1 || day > 30)
        {
            return 0.0;
        }
        return priceBreakdown[day - 1];
    }

    /**
     * Returns the entire array of nightly price breakdowns.
     * Each index represents a day in the 30-day calendar.
     *
     * @return A 30-element array showing nightly rates
     */
    public double[] getAllPriceBreakdown()
    {
        return priceBreakdown;
    }

    /**
     * Calculates and returns the total number of nights stayed.
     *
     * @return The number of nights between check-in and check-out
     */
    public int getNumberOfNights()
    {
        int nights = checkOutDay - checkInDay;
        if (nights < 0)
        {
            nights = 0;
        }
        return nights;
    }

    // DISPLAY METHOD

    /**
     * Returns a formatted string summary of this reservation.
     *
     * @return A readable string containing guest and booking details
     */
    @Override
    public String toString()
    {
        return "Reservation for " + guestName +
                " | Check-in: Day " + checkInDay +
                " | Check-out: Day " + checkOutDay +
                " | Nights: " + getNumberOfNights() +
                " | Total: PHP " + String.format("%.2f", totalPrice);
    }
}
