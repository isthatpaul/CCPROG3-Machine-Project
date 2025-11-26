import java.util.ArrayList;
import java.util.List;

/**
 * Represents a confirmed booking.
 * Stores per-night rates (before and after guest-tier discount) and computes the total price.
 *
 * The nightlyRates passed to the constructor are the computed final nightly rates BEFORE any guest-tier discount.
 */
public class Reservation {
    private final String guestName;
    private final GuestTier guestTier;
    private final int checkInDay;
    private final int checkOutDay;
    private double totalPrice;
    private final List<Double> nightlyRatesBeforeDiscount;
    private final List<Double> nightlyRatesApplied;

    /**
     * Constructs a new Reservation.
     *
     * @param guestName the guest name
     * @param guestTier the guest loyalty tier (may be null; REGULAR used in that case)
     * @param checkIn the check-in day (inclusive)
     * @param checkOut the check-out day (exclusive)
     * @param nightlyRates nightly rates before discount (one per night)
     */
    public Reservation(String guestName, GuestTier guestTier, int checkIn, int checkOut, List<Double> nightlyRates) {
        this.guestName = guestName;
        this.guestTier = guestTier == null ? GuestTier.REGULAR : guestTier;
        this.checkInDay = checkIn;
        this.checkOutDay = checkOut;
        this.nightlyRatesBeforeDiscount = new ArrayList<>(nightlyRates);
        this.nightlyRatesApplied = new ArrayList<>();
        calculateTotalWithDiscount();
    }

    /**
     * Calculates the total price applying the guest tier discount to each nightly rate.
     * Modifies nightlyRatesApplied and totalPrice fields.
     * 
     * @return void
     */
    private void calculateTotalWithDiscount() {
        this.totalPrice = 0.0;
        double discount = guestTier.getDiscount();
        for (double rate : nightlyRatesBeforeDiscount) {
            double applied = rate * (1.0 - discount);
            nightlyRatesApplied.add(applied);
            this.totalPrice += applied;
        }
    }

    /**
     * Returns the guest name.
     * 
     * @return guest name
     */
    public String getGuestName() { return guestName; }

    /**
     * Returns the guest tier.
     * 
     * @return guest tier
     */
    public GuestTier getGuestTier() { return guestTier; }

    /**
     * Returns the check-in day.
     * 
     * @return check-in day
     */
    public int getCheckInDay() { return checkInDay; }

    /**
     * Returns the check-out day.
     * 
     * @return check-out day
     */
    public int getCheckOutDay() { return checkOutDay; }

    /**
     * Returns the total price of the reservation after discounts.
     * 
     * @return total price
     */
    public double getTotalPrice() { return totalPrice; }

    /**
     * Returns the number of nights in the reservation.
     * 
     * @return number of nights
     */
    public int getNumberOfNights() { return nightlyRatesApplied.size(); }

    /**
     * Returns the nightly rate AFTER discount for a specific night index (0-based).
     *
     * @param nightIndex zero-based night index
     * @return applied nightly rate or 0.0 if out of range
     */
    public double getNightlyRateByIndex(int nightIndex) {
        if (nightIndex >= 0 && nightIndex < nightlyRatesApplied.size()) {
            return nightlyRatesApplied.get(nightIndex);
        }
        return 0.0;
    }

    /**
     * Returns the nightly rate BEFORE discount for a specific night index (0-based).
     *
     * @param nightIndex zero-based night index
     * @return nightly rate before discount or 0.0 if out of range
     */
    public double getNightlyRateBeforeDiscountByIndex(int nightIndex) {
        if (nightIndex >= 0 && nightIndex < nightlyRatesBeforeDiscount.size()) {
            return nightlyRatesBeforeDiscount.get(nightIndex);
        }
        return 0.0;
    }

    /**
     * Returns a defensive copy of applied nightly rates (after discount).
     *
     * @return list of nightly rates after discount
     */
    public List<Double> getNightlyRatesApplied() {
        return new ArrayList<>(nightlyRatesApplied);
    }

    /**
     * Returns a defensive copy of nightly rates before discount.
     *
     * @return list of nightly rates before discount
     */
    public List<Double> getNightlyRatesBeforeDiscount() {
        return new ArrayList<>(nightlyRatesBeforeDiscount);
    }

    /**
     * Returns a string representation of the reservation.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("%s | %s | Days %d-%d | Nights: %d | Total: PHP %.2f",
                guestName, guestTier.toString(), checkInDay, checkOutDay, getNumberOfNights(), totalPrice);
    }
}