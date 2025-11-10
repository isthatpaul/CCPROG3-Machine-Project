import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract Property class serves as the base for all property types in the system.
 * It implements common property management logic and defines abstract methods for polymorphism.
 * @author Crisologo, Lim Un
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
     * @param day the day number (1–30)
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
        if (newPrice < 100.0) {
            return false;
        }
        if (!reservations.isEmpty()) {
            return false;
        }

        this.basePrice = newPrice;
        return true;
    }

    // --- EXISTING MCO1 METHODS (Getters) ---

    public String getPropertyName() {
        return propertyName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public List<DateSlot> getDates() {
        return dates;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    // Renames the property.
    public void setPropertyName(String newName) {
        this.propertyName = newName;
    }

    // Removes a reservation
    public boolean removeReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            return false;
        }

        for (int d = reservation.getCheckInDay(); d < reservation.getCheckOutDay(); d++) {
            dates.get(d - 1).cancelBooking();
        }

        reservations.remove(reservation);
        return true;
    }

    // Returns true if the property has no active reservations.
    public boolean canBeRemoved() {
        return reservations.isEmpty();
    }

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

    /**
     * Determines the color to use for displaying the environmental impact level of a date.
     * @param day the day number (1-30)
     * @return Color.GREEN for reduced impact (80-89% of base price)
     *         Color.WHITE for standard impact (100% of base price)
     *         Color.YELLOW for increased impact (101-120% of base price)
     */
    public Color getEnvironmentalImpactColor(int day) {
        if (day < 1 || day > 30) return Color.WHITE;

        DateSlot slot = dates.get(day - 1);
        double modifier = slot.getEnvImpactModifier();

        if (modifier >= 0.80 && modifier <= 0.89) {
            return Color.GREEN;      // Reduced environmental impact (80-89%)
        } else if (modifier == 1.0) {
            return Color.WHITE;      // Standard impact (100%)
        } else if (modifier >= 1.01 && modifier <= 1.20) {
            return Color.YELLOW;     // Increased impact (101-120%)
        } else {
            return Color.WHITE;      // Default to white for any other cases
        }
    }

    /**
     * Sets the environmental impact modifier for a specific date.
     * @param day the day number (1-30)
     * @param modifier the environmental impact modifier (0.8 to 1.2)
     * @return true if successful, false if invalid day or modifier
     */
    public boolean setEnvironmentalImpact(int day, double modifier) {
        if (day < 1 || day > 30) return false;
        if (modifier < 0.8 || modifier > 1.2) return false;

        DateSlot slot = dates.get(day - 1);
        slot.setEnvImpactModifier(modifier);
        return true;
    }

    /**
     * Sets environmental impact modifiers for a range of dates.
     * @param startDay start day (inclusive)
     * @param endDay end day (inclusive)
     * @param modifier the environmental impact modifier (0.8 to 1.2)
     * @return true if successful, false if invalid range or modifier
     */
    public boolean setEnvironmentalImpactRange(int startDay, int endDay, double modifier) {
        if (startDay < 1 || endDay > 30 || startDay > endDay) return false;
        if (modifier < 0.8 || modifier > 1.2) return false;

        for (int day = startDay; day <= endDay; day++) {
            DateSlot slot = dates.get(day - 1);
            slot.setEnvImpactModifier(modifier);
        }
        return true;
    }

    /**
     * Resets the environmental impact modifier for a date to the standard rate (1.0).
     * @param day the day number (1-30)
     * @return true if successful, false if invalid day
     */
    public boolean resetEnvironmentalImpact(int day) {
        if (day < 1 || day > 30)
            return false;

        DateSlot slot = dates.get(day - 1);
        slot.setEnvImpactModifier(1.0);
        return true;
    }
}