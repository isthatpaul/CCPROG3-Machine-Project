import java.util.List;

/**
 * Service interface defining booking operations used by the UI.
 * Implementations should enforce MCO1 booking rules and coordinate persistence.
 */
public interface BookingService {
    /**
     * Books a property for a guest, applying discounts based on guest tier.
     * 
     * @param propertyName 
     * @param guestName 
     * @param tier
     * @param checkIn
     * @param checkOut
     * @return
     */
    boolean book(String propertyName, String guestName, GuestTier tier, int checkIn, int checkOut);

    /**
     * Cancels a reservation for a given property.
     * 
     * @param propertyName
     * @param reservation
     * @return true if cancellation was successful, false otherwise
     */
    boolean removeReservation(String propertyName, Reservation reservation);

    /**
     * Retrieves all reservations for a given property.
     * 
     * @param propertyName 
     * @return list of reservations
     */
    List<Reservation> getReservations(String propertyName);
}