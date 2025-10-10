import java.util.ArrayList;
import java.util.List;

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
        if (name == null)
            name = "";
        this.name = name;
        this.basePrice = DEFAULT_BASE_PRICE;
        this.dates = new ArrayList<>();
        int safeNum = Math.max(1, Math.min(numDates, 30));
        for (int i = 1; i <= safeNum; i++)
        {
            dates.add(new DateSlot(i, basePrice));
        }
        this.reservations = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public boolean setName(String newName)
    {
        if (newName == null || newName.isBlank())
            return false;
        this.name = newName;
        return true;
    }
}
