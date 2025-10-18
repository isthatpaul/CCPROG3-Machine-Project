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
 * @author Crisologo, Lim Un
 * @version 4.0
 */
public class Property
{
    /** The name of the property. */
    private String propertyName;
    /** The base price per night for this property. */
    private double basePrice;
    /** List of 30 DateSlot objects representing the booking calendar. */
    private List<DateSlot> dates;
    /** List of all reservations made for this property. */
    private List<Reservation> reservations;

    // CONSTRUCTOR

    /**
     * Constructs a {@code Property} with a fixed 30-day calendar.
     *
     * @param name       The name of the property
     * @param basePrice  The nightly base price for reservations
     */
    public Property(String name, double basePrice)
    {
        this.propertyName = name;
        this.basePrice = basePrice;
        this.dates = new ArrayList<DateSlot>();
        this.reservations = new ArrayList<Reservation>();

        // Initialize 30-day calendar
        for (int day = 1; day <= 30; day++) {
            dates.add(new DateSlot(day, basePrice));
        }
    }

    // GETTERS

    /**
     * Returns the property’s name.
     *
     * @return The property name
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Returns the list of {@link DateSlot} objects representing the property’s calendar.
     *
     * @return The list of 30 DateSlot entries
     */
    public List<DateSlot> getDates()
    {
        return dates;
    }

    /**
     * Returns all reservations made for this property.
     *
     * @return The list of Reservation objects
     */
    public List<Reservation> getReservations()
    {
        return reservations;
    }

    // CORE FUNCTIONALITY

    /**
     * Attempts to create and add a new reservation for this property.
     * <p>
     * This method enforces all MCO1 Clarifications:
     * <ul>
     *     <li>Prevents check-out on Day 1</li>
     *     <li>Prevents check-in on Day 30</li>
     *     <li>Rejects overlapping reservations, but allows back-to-back bookings</li>
     * </ul>
     *
     * @param guestName The guest making the reservation
     * @param checkIn   The check-in day (1–30)
     * @param checkOut  The check-out day (1–30)
     * @return {@code true} if the reservation was successfully created; {@code false} otherwise
     */
    public boolean addReservation(String guestName, int checkIn, int checkOut)
    {
        // Invalid days
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

        // Validations
        if (checkIn < 1 || checkOut > 30 || checkIn >= checkOut)
        {
            System.out.println("Invalid day range for reservation.");
            return false;
        }

        // Check for overlapping reservations
        for (int i = 0; i < reservations.size(); i++)
        {
            Reservation r = reservations.get(i);
            int existingIn = r.getCheckInDay();
            int existingOut = r.getCheckOutDay();


            boolean overlap = (checkIn < existingOut) && (checkOut > existingIn);

            if (overlap)
            {
                System.out.println("Conflict: Overlaps with another reservation (" +
                        r.getGuestName() + " " + existingIn + "–" + existingOut + ").");
                return false;
            }
        }

        // Create Reservation
        Reservation newRes = new Reservation(guestName, checkIn, checkOut, basePrice);
        reservations.add(newRes);

        // Mark booked days on the calendar
        for (int d = checkIn; d < checkOut; d++)
        {
            DateSlot slot = dates.get(d - 1);
            slot.book(newRes);
        }

        System.out.println("Reservation confirmed for " + guestName +
                " (Days " + checkIn + "–" + checkOut + ")");
        return true;
    }


    // DISPLAY METHODS

    /**
     * Displays all 30 days of this property’s booking calendar.
     * Shows which days are booked and by which guest.
     */
    public void displayCalendar()
    {
        System.out.println("\n=== Calendar for " + propertyName + " ===");
        for (DateSlot d : dates)
        {
            System.out.println(d);
        }
    }

    /**
     * Displays a list of all reservations recorded for this property.
     * Prints guest names, check-in/out days, total price, and number of nights.
     */
    public void displayReservations()
    {
        System.out.println("\n=== Reservations for " + propertyName + " ===");
        if (reservations.isEmpty())
        {
            System.out.println("No reservations yet.");
        } else {
            for (Reservation r : reservations)
            {
                System.out.println(r);
            }
        }
    }
}
