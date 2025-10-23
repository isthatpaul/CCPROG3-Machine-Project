import java.util.ArrayList;
import java.util.List;

/**
 * The Property class represents a rental property in the Green Property System.
 * It maintains a name, a base price per night, a 30-day booking calendar, and a list of reservations.
 * <p>
 * This class provides methods to create, view, and manage reservations, update the base price,
 * and retrieve property details and earnings.
 * </p>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     4.2
 */
public class Property {
    private String propertyName;
    private double basePrice;
    private List<DateSlot> dates;
    private List<Reservation> reservations;

    /**
     * Creates a new Property with a fixed 30-day booking calendar.
     *
     * @param name the name of the property
     * @param basePrice the base price per night
     */
    public Property(String name, double basePrice) {
        this.propertyName = name;
        this.basePrice = basePrice;
        this.dates = new ArrayList<>();
        this.reservations = new ArrayList<>();

        for (int day = 1; day <= 30; day++) {
            dates.add(new DateSlot(day, basePrice));
        }
    }

    /** Returns the property name. */
    public String getPropertyName() {
        return propertyName;
    }

    /** Returns the base price per night. */
    public double getBasePrice() {
        return basePrice;
    }

    /** Returns the list of DateSlot objects representing the 30-day calendar. */
    public List<DateSlot> getDates() {
        return dates;
    }

    /** Returns all reservations made for this property. */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * Adds a new reservation if the dates are valid and there are no conflicts.
     *
     * @param guestName the name of the guest
     * @param checkIn the check-in day (1–30)
     * @param checkOut the check-out day (1–30)
     * @return true if the reservation was successfully added, false otherwise
     */
    public boolean addReservation(String guestName, int checkIn, int checkOut) {
        if (checkOut == 1) {
            System.out.println("Invalid: Cannot check out on Day 1.");
            return false;
        }

        if (checkIn == 30) {
            System.out.println("Invalid: Cannot check in on Day 30.");
            return false;
        }

        if (checkIn < 1 || checkOut > 30 || checkIn >= checkOut) {
            System.out.println("Invalid day range for reservation.");
            return false;
        }

        for (Reservation r : reservations) {
            int existingIn = r.getCheckInDay();
            int existingOut = r.getCheckOutDay();
            if (checkIn < existingOut && checkOut > existingIn) {
                System.out.println("Conflict: Overlaps with another reservation (" +
                        r.getGuestName() + " " + existingIn + "–" + existingOut + ").");
                return false;
            }
        }

        Reservation newRes = new Reservation(guestName, checkIn, checkOut, basePrice);
        reservations.add(newRes);

        for (int d = checkIn; d < checkOut; d++) {
            dates.get(d - 1).book(newRes);
        }

        System.out.println("Reservation confirmed for " + guestName +
                " (Days " + checkIn + "–" + checkOut + ")");
        return true;
    }

    /**
     * Updates the base price if there are no existing reservations.
     *
     * @param newPrice the new base price (must be at least PHP 100.00)
     * @return true if the update was successful, false otherwise
     */
    public boolean updateBasePrice(double newPrice) {
        if (newPrice < 100.0) {
            System.out.println("Price must be at least PHP 100.00.");
            return false;
        }

        if (!reservations.isEmpty()) {
            System.out.println("Cannot update price: existing reservations present.");
            return false;
        }

        this.basePrice = newPrice;
        for (DateSlot date : dates) {
            date.setPricePerNight(newPrice);
        }

        System.out.println("Base price updated to PHP " + newPrice);
        return true;
    }

    /**
     * Renames the property.
     *
     * @param newName the new property name
     */
    public void renameProperty(String newName) {
        this.propertyName = newName;
    }

    /**
     * Removes a reservation for the specified guest.
     *
     * @param reservation the reservation to remove
     * @return true if the reservation was removed, false otherwise
     */
    public boolean removeReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            System.out.println("No reservation found for " + reservation.getGuestName());
            return false;
        }

        for (int d = reservation.getCheckInDay(); d < reservation.getCheckOutDay(); d++) {
            dates.get(d - 1).cancelBooking();
        }

        reservations.remove(reservation);
        System.out.println("Reservation for " + reservation.getGuestName() + " removed.");
        return true;
    }

    /** Returns true if the property has no active reservations. */
    public boolean canBeRemoved() {
        return reservations.isEmpty();
    }

    /** Returns how many days are still available for booking. */
    public int countAvailableDates() {
        int count = 0;
        for (DateSlot date : dates) {
            if (date.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    /** Returns the total earnings from all reservations. */
    public double getTotalEarnings() {
        double total = 0.0;
        for (Reservation r : reservations) {
            total += r.getTotalPrice();
        }
        return total;
    }

    /**
     * Displays availability and price for a specific day.
     *
     * @param day the day number (1–30)
     */
    public void getDateInfo(int day) {
        if (day < 1 || day > 30) {
            System.out.println("Invalid day.");
            return;
        }

        DateSlot slot = dates.get(day - 1);
        if (slot.isBooked()) {
            System.out.println("Day " + day + ": PHP " + slot.getPricePerNight() + " - BOOKED");
        } else {
            System.out.println("Day " + day + ": PHP " + slot.getPricePerNight() + " - AVAILABLE");
        }
    }

    /**
     * Displays a summary of booked and available days in a range.
     *
     * @param start the start day (inclusive)
     * @param end the end day (inclusive)
     */
    public void getRangeSummary(int start, int end) {
        if (start < 1 || end > 30 || start > end) {
            System.out.println("Invalid range.");
            return;
        }

        int available = 0;
        int booked = 0;
        for (int i = start - 1; i < end; i++) {
            if (dates.get(i).isAvailable()) {
                available++;
            } else {
                booked++;
            }
        }

        System.out.println("Range " + start + "–" + end + ": " +
                available + " available, " + booked + " booked.");
    }

    /** Displays all 30 days of this property's booking calendar. */
    public void displayCalendar() {
        System.out.println("\n=== Calendar for " + propertyName + " ===");
        for (DateSlot d : dates) {
            System.out.println(d);
        }
    }

    /** Displays all reservations for this property. */
    public void displayReservations() {
        System.out.println("\n=== Reservations for " + propertyName + " ===");
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
        } else {
            for (Reservation r : reservations) {
                System.out.println(r);
            }
        }
    }

    /** Sets a new name for the property. */
    public void setPropertyName(String newName) {
        propertyName = newName;
    }

    /** Sets a new base price for the property. */
    public void setBasePrice(double newBasePrice) {
        basePrice = newBasePrice;
    }
}
