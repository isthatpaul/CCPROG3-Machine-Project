import java.util.List;

/**
 * Service interface defining booking operations used by the UI.
 * Implementations should enforce MCO1 booking rules and coordinate persistence.
 */
public interface BookingService {
    boolean book(String propertyName, String guestName, GuestTier tier, int checkIn, int checkOut);
    boolean removeReservation(String propertyName, Reservation reservation);
    List<Reservation> getReservations(String propertyName);
}