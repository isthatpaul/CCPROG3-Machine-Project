public class Reservation
{
    private final String guestName;
    private final int checkInDay;
    private final int checkOutDay;
    private double totalPrice;
    private double priceBreakdown[];

    public Reservation(String guest, int checkIn, int checkOut, double basePrice)
    {
        this.guestName = guest;
        this.checkInDay = checkIn;
        this.checkOutDay = checkOut;
        this.priceBreakdown = new double[30];
        calculatePrices(basePrice);
    }

    private void calculatePrices(double basePrice)
    {
        this.totalPrice = 0.0;
        int duration = checkOutDay - checkInDay;

        for (int i = 0; i < priceBreakdown.length; i++)
        {
            priceBreakdown[i] = 0.0;
        }

        for (int i = 0; i < duration; i++)
        {
            int currentDay = checkInDay + i;
            if (currentDay <= 30)
            {
                priceBreakdown[currentDay - 1] = basePrice;
                totalPrice += basePrice;
            }
        }

    }

    public String getGuestName()
    {
        return guestName;
    }

    public int getCheckInDay()
    {
        return checkInDay;
    }

    public int getCheckOutDay()
    {
        return checkOutDay;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public double[] getPriceBreakdown(int day) // im confused with what to do here
    {

    }
}