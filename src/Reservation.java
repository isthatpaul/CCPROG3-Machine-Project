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
     * Applies the guest-tier discount to nightlyRatesBeforeDiscount and computes totalPrice.
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

    /** @return guest name */
    public String getGuestName() { return guestName; }

    /** @return guest tier */
    public GuestTier getGuestTier() { return guestTier; }

    /** @return check-in day (inclusive) */
    public int getCheckInDay() { return checkInDay; }

    /** @return check-out day (exclusive) */
    public int getCheckOutDay() { return checkOutDay; }

    /** @return total price after discounts */
    public double getTotalPrice() { return totalPrice; }

    /** @return number of nights in reservation */
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

    @Override
    public String toString() {
        return String.format("%s | %s | Days %d-%d | Nights: %d | Total: PHP %.2f",
                guestName, guestTier.toString(), checkInDay, checkOutDay, getNumberOfNights(), totalPrice);
    }
}