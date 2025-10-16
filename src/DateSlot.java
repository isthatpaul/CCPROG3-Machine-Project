public class DateSlot {
    private final int dayNumber;       // 1..n
    private double pricePerNight;
    private boolean booked;
    private Reservation reservation;

    public DateSlot(int dayNumber, double basePrice)
    {
        this.dayNumber = dayNumber;
        this.pricePerNight = basePrice;
        this.booked = false;
        this.reservation = null;
    }

    public int getDayNumber()
    {
        return dayNumber;
    }

    public double getPricePerNight()
    {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight)
    {
        this.pricePerNight = pricePerNight;
    }

    public boolean isBooked()
    {
        return booked;
    }

    public Reservation getReservation()
    {
        return reservation;
    }

    public boolean isAvailable()
    {
        return !booked;
    }

    public boolean book(Reservation res)
    {
        if (booked) {
            return false;
        } else {
            booked = true;
            reservation = res;
            return true;
        }
    }

    public void cancelBooking()
    {
        booked = false;
        reservation = null;
    }
}
