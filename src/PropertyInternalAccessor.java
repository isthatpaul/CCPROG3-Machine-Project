/**
 * Internal accessor interface that allows BookingServiceImpl to persist a Reservation
 * into a Property without exposing the property's internal lists publicly.
 *
 * This is a controlled package-level contract to keep Property's internal state encapsulated
 * while allowing booking orchestration to occur in BookingServiceImpl (SRP).
 */
public interface PropertyInternalAccessor {
    /**
     * Persist a reservation into the property and mark the corresponding DateSlots as booked.
     *
     * @param reservation reservation to persist
     */
    void addReservationDirect(Reservation reservation);
}