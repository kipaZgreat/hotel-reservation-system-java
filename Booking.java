package hotel_reservation_systems;
import java.time.LocalDate;

public class Booking {
    private final Guest guest;
    private final Room room;
    private final int nights;
    private LocalDate bookingDate;
    private boolean isCancelled;
    private boolean isRefunded;
    private double refundAmount;

    public Booking(Guest guest, Room room, int nights) {
        this.guest = guest;
        this.room = room;
        this.nights = nights;
        this.bookingDate = LocalDate.now();
    }

    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public int getNights() { return nights; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate date) { this.bookingDate = date; }
    public void setCancelled(boolean cancelled) { this.isCancelled = cancelled; }
    public void setRefunded(boolean refunded) { this.isRefunded = refunded; }
    public void setRefundAmount(double amount) { this.refundAmount = amount; }
    public double getRefundAmount() { return refundAmount; }
}