import java.util.ArrayList;
import java.util.List;

/**
 * The abstract Property class serves as the base for all property types in the system.
 * It implements common property management logic and defines abstract methods for polymorphism.
 * * @author Crisologo, Lim Un
 * @version 5.0 (Updated for MCO2)
 */
public abstract class Property {
    private String propertyName;
    private double basePrice;
    private List<DateSlot> dates;
    private List<Reservation> reservations;

    public Property(String name, double basePrice) {
        this.propertyName = name;
        this.basePrice = basePrice;
        this.dates = new ArrayList<>();
        this.reservations = new ArrayList<>();

        for (int day = 1; day <= 30; day++) {
            // DateSlot now created without a price
            dates.add(new DateSlot(day));
        }
    }

    // --- ABSTRACT METHODS FOR POLYMORPHISM ---

    /** Returns the unique rate multiplier for the specific property type (e.g., 1.20 for Sustainable House). */
    protected abstract double getRateMultiplier();

    /** Returns the display name of the property type. */
    public abstract String getPropertyType();

    // --- NEW MCO2 LOGIC ---

    /**
     * Calculates the final nightly rate for a specific day, factoring in the
     * property type multiplier and the day's environmental impact modifier.
     * Final Rate = Base Price * Type Multiplier * Env Modifier
     * * @param day the day number (1–30)
     * @return the final calculated price per night
     */
    public double calculateFinalDailyRate(int day) {
        if (day < 1 || day > 30) {
            return 0.0;
        }
        DateSlot slot = dates.get(day - 1);
        return basePrice * getRateMultiplier() * slot.getEnvImpactModifier();
    }

    // --- UPDATED MCO1 METHODS ---

    /**
     * Adds a new reservation. The reservation's price is calculated dynamically
     * based on each reserved day's modifier.
     */
    public boolean addReservation(String guestName, int checkIn, int checkOut) {
        if (checkOut == 1 || checkIn == 30 || checkIn < 1 || checkOut > 30 || checkIn >= checkOut) {
            // Simplified error check for display, full logic is in MCO1
            // In a GUI, this validation would happen before calling the method.
            return false;
        }

        for (Reservation r : reservations) {
            int existingIn = r.getCheckInDay();
            int existingOut = r.getCheckOutDay();
            if (checkIn < existingOut && checkOut > existingIn) {
                return false; // Conflict found
            }
        }

        // 1. Calculate the nightly prices for the reservation
        List<Double> nightlyRates = new ArrayList<>();
        for (int d = checkIn; d < checkOut; d++) {
            nightlyRates.add(calculateFinalDailyRate(d));
        }

        // 2. Create the Reservation with dynamic rates
        Reservation newRes = new Reservation(guestName, checkIn, checkOut, nightlyRates);
        reservations.add(newRes);

        // 3. Book the DateSlot objects
        for (int d = checkIn; d < checkOut; d++) {
            dates.get(d - 1).book(newRes);
        }

        return true;
    }

    /**
     * Updates the base price if there are no existing reservations.
     * The loop updating DateSlot prices is removed as DateSlot no longer stores price.
     */
    public boolean updateBasePrice(double newPrice) {
        if (newPrice < 100.0) { return false; }
        if (!reservations.isEmpty()) { return false; }

        this.basePrice = newPrice;
        return true;
    }

    // --- EXISTING MCO1 METHODS (Getters) ---

    public String getPropertyName() { return propertyName; }
    public double getBasePrice() { return basePrice; }
    public List<DateSlot> getDates() { return dates; }
    public List<Reservation> getReservations() { return reservations; }

    // Renames the property.
    public void setPropertyName(String newName) { this.propertyName = newName; }

    // Removes a reservation
    public boolean removeReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) { return false; }

        for (int d = reservation.getCheckInDay(); d < reservation.getCheckOutDay(); d++) {
            dates.get(d - 1).cancelBooking();
        }

        reservations.remove(reservation);
        return true;
    }

    // Returns true if the property has no active reservations.
    public boolean canBeRemoved() { return reservations.isEmpty(); }

    // Returns the total earnings from all reservations.
    public double getTotalEarnings() {
        double total = 0.0;
        for (Reservation r : reservations) {
            total += r.getTotalPrice();
        }
        return total;
    }

    // Returns how many days are still available for booking.
    public int countAvailableDates() {
        int count = 0;
        for (DateSlot date : dates) {
            if (date.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    // Other MCO1 display methods (getDateInfo, getRangeSummary, displayCalendar, displayReservations) are now obsolete/moved to GUI logic.
}