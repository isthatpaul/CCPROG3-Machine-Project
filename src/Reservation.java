/**
 * The Reservation class represents a confirmed booking for a guest
 * in the Green Property System. It stores information about the guest,
 * the check-in and check-out days, and the total price for the stay.
 * <p>
 * This class automatically calculates the total cost based on the base price
 * and provides methods to retrieve booking details and pricing breakdowns.
 * </p>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     2.0
 */
public class Reservation {
    private final String guestName;
    private final int checkInDay;
    private final int checkOutDay;
    private double totalPrice;
    private double[] priceBreakdown;

    /**
     * Creates a new Reservation for a specific guest and date range.
     * Automatically calculates the total and nightly rates based on the given base price.
     *
     * @param guestName the name of the guest
     * @param checkIn the check-in day (1–30)
     * @param checkOut the check-out day (1–30)
     * @param basePrice the nightly base price for the property
     */
    public Reservation(String guestName, int checkIn, int checkOut, double basePrice) {
        this.guestName = guestName;
        this.checkInDay = checkIn;
        this.checkOutDay = checkOut;
        this.priceBreakdown = new double[30];
        calculatePrices(basePrice);
    }

    /**
     * Calculates nightly rates and the total cost for the stay.
     *
     * @param basePrice the base nightly rate of the property
     */
    private void calculatePrices(double basePrice) {
        this.totalPrice = 0.0;

        // Reset all days
        for (int i = 0; i < priceBreakdown.length; i++) {
            priceBreakdown[i] = 0.0;
        }

        // Compute total based on valid days
        int duration = checkOutDay - checkInDay;

        for (int i = 0; i < duration; i++) {
            int currentDay = checkInDay + i;
            if (currentDay <= 30) {
                priceBreakdown[currentDay - 1] = basePrice;
                totalPrice += basePrice;
            }
        }
    }

    /** Returns the guest’s name. */
    public String getGuestName() {
        return guestName;
    }

    /** Returns the check-in day number. */
    public int getCheckInDay() {
        return checkInDay;
    }

    /** Returns the check-out day number. */
    public int getCheckOutDay() {
        return checkOutDay;
    }

    /** Returns the total price for this reservation. */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Returns the nightly price for a specific day.
     * If the day is outside the 1–30 range, returns 0.0.
     *
     * @param day the day number (1–30)
     * @return the nightly rate for that day, or 0.0 if invalid
     */
    public double getPriceBreakdown(int day) {
        if (day < 1 || day > 30) {
            return 0.0;
        }
        return priceBreakdown[day - 1];
    }

    /** Returns the full 30-day price breakdown array. */
    public double[] getAllPriceBreakdown() {
        return priceBreakdown;
    }

    /** Returns the total number of nights between check-in and check-out. */
    public int getNumberOfNights() {
        int nights = checkOutDay - checkInDay;
        if (nights < 0) {
            nights = 0;
        }
        return nights;
    }

    /** Returns a readable summary of this reservation. */
    @Override
    public String toString() {
        return "Reservation for " + guestName +
                " | Check-in: Day " + checkInDay +
                " | Check-out: Day " + checkOutDay +
                " | Nights: " + getNumberOfNights() +
                " | Total: PHP " + String.format("%.2f", totalPrice);
    }
}
