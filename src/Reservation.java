import java.util.List;

/**
 * The Reservation class represents a confirmed booking, storing the pre-calculated
 * final nightly rates for dynamic pricing.
 * * @author Crisologo, Lim Un
 * @version 3.0 (Updated for MCO2)
 */
public class Reservation {
    private final String guestName;
    private final int checkInDay;
    private final int checkOutDay;
    private double totalPrice;
    // Stores the final calculated rate for each night of the stay
    private final List<Double> nightlyRates;

    /**
     * Creates a new Reservation for a specific guest and date range.
     *
     * @param guestName the name of the guest
     * @param checkIn the check-in day (1–30)
     * @param checkOut the check-out day (1–30)
     * @param nightlyRates the calculated final price for each night of the stay
     */
    public Reservation(String guestName, int checkIn, int checkOut, List<Double> nightlyRates) {
        this.guestName = guestName;
        this.checkInDay = checkIn;
        this.checkOutDay = checkOut;
        this.nightlyRates = nightlyRates;
        calculateTotal();
    }

    /** Calculates the total cost for the stay based on the provided nightly rates. */
    private void calculateTotal() {
        this.totalPrice = 0.0;
        for (double rate : nightlyRates) {
            this.totalPrice += rate;
        }
    }

    // --- Getters ---
    public String getGuestName() { return guestName; }
    public int getCheckInDay() { return checkInDay; }
    public int getCheckOutDay() { return checkOutDay; }
    public double getTotalPrice() { return totalPrice; }
    public int getNumberOfNights() { return nightlyRates.size(); }

    // Returns the nightly rate for the stay (0-indexed). Useful for display.
    public double getNightlyRateByIndex(int nightIndex) {
        if (nightIndex >= 0 && nightIndex < nightlyRates.size()) {
            return nightlyRates.get(nightIndex);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("%s | Days %d-%d | Nights: %d | Total: PHP %.2f",
                guestName, checkInDay, checkOutDay, getNumberOfNights(), totalPrice);
    }
}