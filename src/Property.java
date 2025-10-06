import java.util.ArrayList;

public class Property
{
    private String name;
    private double basePrice;
    private final List<DateSlot> dates;
    private final List<Reservation> reservations;
    public static final double DEFAULT_BASE_PRICE = 1500.0;
    public static final double MIN_BASE_PRICE = 100.0;

    public Property(String name, int numDates)
    {
        this.name = name;
        this.basePrice = DEFAULT_BASE_PRICE;
        this.dates = new ArrayList<>();
        for (int i = 1; i <= numDates; i++)
        {

        }
    }
}
