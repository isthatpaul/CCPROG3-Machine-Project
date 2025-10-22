import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Property} class represents a rental property in the
 * Green Property system for MCO1.
 * <p>
 * Each property maintains:
 * <ul>
 *     <li>A 30-day booking calendar represented by {@link DateSlot} objects</li>
 *     <li>A list of {@link Reservation} records</li>
 *     <li>A base price per night for all reservations</li>
 * </ul>
 *
 * @author
 *     Crisologo, Lim Un
 * @version
 *     4.2
 */
public class Property
{
    private String propertyName;
    private double basePrice;
    private List<DateSlot> dates;
    private List<Reservation> reservations;

    /**
     * Constructs a {@code Property} with a fixed 30-day calendar.
     *
     * @param name       the name of the property
     * @param basePrice  the nightly base price for reservations
     */
    public Property(String name, double basePrice)
    {
        this.propertyName = name;
        this.basePrice = basePrice;
        this.dates = new ArrayList<DateSlot>();
        this.reservations = new ArrayList<Reservation>();

        for (int day = 1; day <= 30; day++)
        {
            dates.add(new DateSlot(day, basePrice));
        }
    }

    /**
     * Returns the property name.
     *
     * @return the property's name
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Returns the base price per night.
     *
     * @return the base price per night
     */
    public double getBasePrice()
    {
        return basePrice;
    }

    /**
     * Returns the list of all {@link DateSlot} objects representing
     * the 30-day calendar.
     *
     * @return a list of {@code DateSlot} objects
     */
    public List<DateSlot> getDates()
    {
        return dates;
    }

    /**
     * Returns the list of all reservations for this property.
     *
     * @return a list of {@code Reservation} objects
     */
    public List<Reservation> getReservations()
    {
        return reservations;
    }

    /**
     * Adds a new reservation if it does not conflict with existing ones
     * and the given dates are valid.
     *
     * @param guestName  the name of the guest making the reservation
     * @param checkIn    the check-in day (1–30)
     * @param checkOut   the check-out day (1–30)
     * @return {@code true} if the reservation was successfully added;
     *         {@code false} otherwise
     */
    public boolean addReservation(String guestName, int checkIn, int checkOut)
    {
        if (checkOut == 1)
        {
            System.out.println("Invalid: Cannot check out on Day 1.");
            return false;
        }

        if (checkIn == 30)
        {
            System.out.println("Invalid: Cannot check in on Day 30.");
            return false;
        }

        if (checkIn < 1 || checkOut > 30 || checkIn >= checkOut)
        {
            System.out.println("Invalid day range for reservation.");
            return false;
        }

        for (int i = 0; i < reservations.size(); i++)
        {
            Reservation r = reservations.get(i);
            int existingIn = r.getCheckInDay();
            int existingOut = r.getCheckOutDay();

            if (checkIn < existingOut && checkOut > existingIn)
            {
                System.out.println("Conflict: Overlaps with another reservation (" +
                        r.getGuestName() + " " + existingIn + "–" + existingOut + ").");
                return false;
            }
        }

        Reservation newRes = new Reservation(guestName, checkIn, checkOut, basePrice);
        reservations.add(newRes);

        for (int d = checkIn; d < checkOut; d++)
        {
            dates.get(d - 1).book(newRes);
        }

        System.out.println("Reservation confirmed for " + guestName +
                " (Days " + checkIn + "–" + checkOut + ")");
        return true;
    }

    /**
     * Updates the base price per night for the property,
     * provided there are no existing reservations.
     *
     * @param newPrice  the new base price (must be at least PHP 100.00)
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public boolean updateBasePrice(double newPrice)
    {
        if (newPrice < 100.0)
        {
            System.out.println("Price must be at least PHP 100.00.");
            return false;
        }

        if (reservations.size() > 0)
        {
            System.out.println("Cannot update price: existing reservations present.");
            return false;
        }

        this.basePrice = newPrice;

        for (int i = 0; i < dates.size(); i++)
        {
            dates.get(i).setPricePerNight(newPrice);
        }

        System.out.println("Base price updated to PHP " + newPrice);
        return true;
    }

    /**
     * Renames the property.
     *
     * @param newName  the new name of the property
     */
    public void renameProperty(String newName)
    {
        this.propertyName = newName;
    }

    /**
     * Removes a reservation associated with the given guest name.
     *
     * @param guestName  the name of the guest whose reservation is to be removed
     * @return {@code true} if the reservation was successfully removed;
     *         {@code false} if no matching reservation was found
     */
    public boolean removeReservation(String guestName)
    {
        Reservation target = null;

        for (int i = 0; i < reservations.size(); i++)
        {
            Reservation r = reservations.get(i);
            if (r.getGuestName().equalsIgnoreCase(guestName))
            {
                target = r;
                break;
            }
        }

        if (target == null)
        {
            System.out.println("No reservation found for " + guestName);
            return false;
        }

        for (int d = target.getCheckInDay(); d < target.getCheckOutDay(); d++)
        {
            dates.get(d - 1).cancelBooking();
        }

        reservations.remove(target);
        System.out.println("Reservation for " + guestName + " removed.");
        return true;
    }

    /**
     * Checks if the property can be safely removed,
     * i.e., it has no existing reservations.
     *
     * @return {@code true} if the property has no reservations;
     *         {@code false} otherwise
     */
    public boolean canBeRemoved()
    {
        return reservations.size() == 0;
    }

    /**
     * Counts how many dates in the calendar are still available.
     *
     * @return the number of available days
     */
    public int countAvailableDates()
    {
        int count = 0;

        for (int i = 0; i < dates.size(); i++)
        {
            if (dates.get(i).isAvailable())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Computes the total earnings from all reservations.
     *
     * @return the total earnings in PHP
     */
    public double getTotalEarnings()
    {
        double total = 0.0;

        for (int i = 0; i < reservations.size(); i++)
        {
            total += reservations.get(i).getTotalPrice();
        }

        return total;
    }

    /**
     * Displays information for a specific day in the calendar,
     * including its availability and price.
     *
     * @param day  the day number (1–30)
     */
    public void getDateInfo(int day)
    {
        if (day < 1 || day > 30)
        {
            System.out.println("Invalid day.");
            return;
        }

        DateSlot slot = dates.get(day - 1);

        if (slot.isBooked())
        {
            System.out.println("Day " + day + ": PHP " + slot.getPricePerNight() + " - BOOKED");
        }
        else
        {
            System.out.println("Day " + day + ": PHP " + slot.getPricePerNight() + " - AVAILABLE");
        }
    }

    /**
     * Displays a summary of booked and available days within a given range.
     *
     * @param start  the start day (inclusive)
     * @param end    the end day (inclusive)
     */
    public void getRangeSummary(int start, int end)
    {
        if (start < 1 || end > 30 || start > end)
        {
            System.out.println("Invalid range.");
            return;
        }

        int available = 0;
        int booked = 0;

        for (int i = start - 1; i < end; i++)
        {
            if (dates.get(i).isAvailable())
            {
                available++;
            }
            else
            {
                booked++;
            }
        }

        System.out.println("Range " + start + "–" + end + ": " +
                available + " available, " + booked + " booked.");
    }

    /**
     * Displays the entire 30-day calendar for the property.
     */
    public void displayCalendar()
    {
        System.out.println("\n=== Calendar for " + propertyName + " ===");

        for (int i = 0; i < dates.size(); i++)
        {
            System.out.println(dates.get(i));
        }
    }

    /**
     * Displays all reservations made for this property.
     */
    public void displayReservations()
    {
        System.out.println("\n=== Reservations for " + propertyName + " ===");

        if (reservations.size() == 0)
        {
            System.out.println("No reservations yet.");
        }
        else
        {
            for (int i = 0; i < reservations.size(); i++)
            {
                System.out.println(reservations.get(i));
            }
        }
    }
}
